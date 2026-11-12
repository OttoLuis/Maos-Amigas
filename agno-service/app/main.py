
import os
import hashlib

from threading import RLock
from uuid import UUID, uuid4

import httpx

from fastapi import FastAPI, Depends, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from pydantic import BaseModel

from app.agents.acolhimento import agent_acolhimento

from app.reservations import (
    ReservaExpirada,
    obter_reserva_da_requisicao,
    consumir_reserva
)


app = FastAPI(title="Mãos Amigas - Agno")


# ==========================================================
# CONFIGURAÇÃO
# ==========================================================

origens_permitidas = [
    origem.strip()
    for origem in os.getenv(
        "AGNO_CORS_ORIGINS",
        "http://localhost:5173,http://localhost:5174,http://localhost:3000"
    ).split(",")
    if origem.strip()
]


app.add_middleware(
    CORSMiddleware,
    allow_origins=origens_permitidas,
    allow_credentials=True,
    allow_methods=["GET", "POST", "OPTIONS"],
    allow_headers=["Authorization", "Content-Type"],
)


security = HTTPBearer()


# ==========================================================
# HISTÓRICO DAS CONVERSAS
# ==========================================================

_historicos: dict[tuple[str, str], list[dict]] = {}

_historicos_lock = RLock()

LIMITE_HISTORICO = 20


def obter_chave_sessao(
    token: str,
    session_id: UUID
) -> tuple[str, str]:
    """
    Identifica a conversa usando o usuário autenticado
    e o identificador da sessão.

    O token não é armazenado diretamente.
    """

    usuario_hash = hashlib.sha256(
        token.encode("utf-8")
    ).hexdigest()

    return usuario_hash, str(session_id)


def montar_prompt(
    historico: list[dict],
    mensagem: str
) -> str:
    """
    Monta o contexto anterior para o agente.

    O histórico contém somente mensagens do usuário
    e respostas anteriores do assistente.
    """

    if not historico:
        return mensagem

    contexto = "\n".join(
        f"{item['role'].upper()}: {item['content']}"
        for item in historico
    )

    return (
        "CONTEXTO DA CONVERSA ANTERIOR:\n"
        f"{contexto}\n\n"
        "NOVA MENSAGEM DO USUÁRIO:\n"
        f"{mensagem}\n\n"
        "INSTRUÇÕES IMPORTANTES:\n"
        "- Considere todo o contexto anterior.\n"
        "- Não pergunte novamente informações já fornecidas.\n"
        "- Se o usuário informar somente um horário, "
        "considere o psicólogo e a data definidos anteriormente.\n"
        "- Se o usuário corrigir uma informação, atualize "
        "somente a informação corrigida.\n"
        "- Nunca invente profissionais, IDs ou horários.\n"
        "- Consulte as ferramentas para validar informações reais.\n"
    )


# ==========================================================
# MODELOS
# ==========================================================

class ChatRequest(BaseModel):
    mensagem: str
    session_id: UUID


class ConfirmacaoRequest(BaseModel):
    reserva_id: UUID


# ==========================================================
# URL DO SPRING BOOT
# ==========================================================

def obter_spring_base_url() -> str:
    return (
        "http://"
        + os.getenv("SPRING_API_HOST", "127.0.0.1")
        + ":"
        + os.getenv("SPRING_API_PORT", "8080")
    )


# ==========================================================
# HEALTH CHECK
# ==========================================================

@app.get("/health")
def health():
    return {
        "status": "ok",
        "servico": "agno-service"
    }


# ==========================================================
# AGENTE DE ACOLHIMENTO
# ==========================================================

@app.post("/agentes/acolhimento")
def acolhimento(
    request: ChatRequest,
    credentials: HTTPAuthorizationCredentials = Depends(security)
):
    token = credentials.credentials

    # Identificador exclusivo desta requisição.
    # Continua separado do identificador da conversa.
    request_id = str(uuid4())

    chave_sessao = obter_chave_sessao(
        token,
        request.session_id
    )

    # Recupera o histórico da conversa atual.
    with _historicos_lock:
        historico = list(
            _historicos.get(chave_sessao, [])
        )

    # Inclui o contexto anterior na mensagem enviada ao Agno.
    prompt = montar_prompt(
        historico,
        request.mensagem
    )

    try:
        resposta = agent_acolhimento.run(
            prompt,
            dependencies={
                "jwt_token": token,
                "request_id": request_id
            }
        )

    except Exception:
        raise HTTPException(
            status_code=502,
            detail="Não foi possível processar sua mensagem."
        )

    conteudo = str(resposta.content or "")

    if not conteudo.strip():
        raise HTTPException(
            status_code=502,
            detail="O assistente não conseguiu gerar uma resposta."
        )

    # Salva a interação no histórico da conversa.
    with _historicos_lock:
        historico_atualizado = historico + [
            {
                "role": "user",
                "content": request.mensagem
            },
            {
                "role": "assistant",
                "content": conteudo
            }
        ]

        _historicos[chave_sessao] = (
            historico_atualizado[-LIMITE_HISTORICO:]
        )

    resultado = {
        "resposta": conteudo
    }

    # Recupera eventual reserva criada durante esta requisição.
    reserva = obter_reserva_da_requisicao(
        request_id,
        token
    )

    if reserva:
        resultado["reserva_pendente"] = reserva

    return resultado


# ==========================================================
# CONFIRMAÇÃO DA CONSULTA
# ==========================================================

@app.post("/agentes/acolhimento/confirmar")
def confirmar_consulta(
    request: ConfirmacaoRequest,
    credentials: HTTPAuthorizationCredentials = Depends(security)
):
    token = credentials.credentials

    try:
        dados = consumir_reserva(
            str(request.reserva_id),
            token
        )

    except ReservaExpirada:
        raise HTTPException(
            status_code=410,
            detail="Esta reserva expirou. Consulte os horários novamente."
        )

    if not dados:
        raise HTTPException(
            status_code=404,
            detail="Reserva não encontrada ou não pertence à sessão atual."
        )

    payload = {
        "psicologoId": dados["psicologo_id"],
        "data": dados["data"],
        "hora": dados["hora"],
        "observacao": dados["observacao"]
    }

    try:
        response = httpx.post(
            f"{obter_spring_base_url()}/consulta",
            json=payload,
            headers={
                "Authorization": f"Bearer {token}"
            },
            timeout=20.0
        )

    except httpx.TimeoutException:
        raise HTTPException(
            status_code=504,
            detail=(
                "Não foi possível confirmar o resultado da operação. "
                "Verifique suas consultas antes de tentar novamente."
            )
        )

    except httpx.HTTPError:
        raise HTTPException(
            status_code=502,
            detail="Não foi possível comunicar com o serviço de agendamento."
        )

    if response.status_code in (200, 201):
        try:
            consulta = response.json()
        except ValueError:
            consulta = None

        return {
            "sucesso": True,
            "mensagem": "Consulta agendada com sucesso.",
            "consulta": consulta
        }

    if response.status_code == 401:
        raise HTTPException(
            status_code=401,
            detail="Sua sessão expirou. Faça login novamente."
        )

    if response.status_code == 403:
        raise HTTPException(
            status_code=403,
            detail="Você não tem permissão para realizar este agendamento."
        )

    if response.status_code == 404:
        raise HTTPException(
            status_code=404,
            detail="O psicólogo ou recurso solicitado não foi encontrado."
        )

    if response.status_code == 409:
        raise HTTPException(
            status_code=409,
            detail=(
                "Este horário acabou de ficar indisponível. "
                "Consulte os horários novamente."
            )
        )

    if response.status_code in (400, 422):
        raise HTTPException(
            status_code=400,
            detail="Os dados do agendamento não foram aceitos."
        )

    raise HTTPException(
        status_code=502,
        detail="O serviço de agendamento não conseguiu concluir a operação."
    )
