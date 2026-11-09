import os
from uuid import UUID, uuid4

import httpx
from fastapi import FastAPI, Depends, HTTPException
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from pydantic import BaseModel

from app.agents.acolhimento import agent_acolhimento
from app.reservations import (
    ReservaExpirada,
    obter_reserva_da_requisicao,
    consumir_reserva
)


app = FastAPI(title="Mãos Amigas - Agno")

security = HTTPBearer()


class ChatRequest(BaseModel):
    mensagem: str


class ConfirmacaoRequest(BaseModel):
    reserva_id: UUID


def obter_spring_base_url() -> str:
    return (
        "http://"
        + os.getenv("SPRING_API_HOST", "127.0.0.1")
        + ":"
        + os.getenv("SPRING_API_PORT", "8080")
    )


@app.post("/agentes/acolhimento")
def acolhimento(
    request: ChatRequest,
    credentials: HTTPAuthorizationCredentials = Depends(security)
):
    token = credentials.credentials
    request_id = str(uuid4())

    resposta = agent_acolhimento.run(
        request.mensagem,
        dependencies={
            "jwt_token": token,
            "request_id": request_id
        }
    )

    resultado = {
        "resposta": resposta.content
    }

    reserva = obter_reserva_da_requisicao(
        request_id,
        token
    )

    if reserva:
        resultado["reserva_pendente"] = reserva

    return resultado


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
