import os
from datetime import date, datetime, time

import httpx
from agno.run import RunContext

from app.reservations import criar_reserva_pendente


def _obter_base_url() -> str:
    return (
        "http://"
        + os.getenv("SPRING_API_HOST", "127.0.0.1")
        + ":"
        + os.getenv("SPRING_API_PORT", "8080")
    )


def _obter_dependencia(
    run_context: RunContext,
    nome: str
) -> str | None:
    dependencies = run_context.dependencies or {}
    return dependencies.get(nome)


def registrar_consulta_pendente(
    psicologo_id: int,
    data: str,
    hora: str,
    run_context: RunContext,
    observacao: str | None = None
) -> dict:
    """
    Valida os dados no Spring Boot e registra uma reserva temporária.
    Não cria uma consulta no banco de dados.
    """

    token = _obter_dependencia(run_context, "jwt_token")
    request_id = _obter_dependencia(run_context, "request_id")

    if not token or not request_id:
        return {
            "sucesso": False,
            "mensagem": "Não foi possível validar a sessão da reserva."
        }

    if not isinstance(psicologo_id, int) or psicologo_id <= 0:
        return {
            "sucesso": False,
            "mensagem": "ID de psicólogo inválido."
        }

    try:
        data_consulta = date.fromisoformat(data)
        hora_consulta = time.fromisoformat(hora)

        if data_consulta < date.today():
            raise ValueError

        if data_consulta.weekday() >= 5:
            return {
                "sucesso": False,
                "mensagem": "As consultas são realizadas somente de segunda a sexta."
            }

    except (ValueError, TypeError):
        return {
            "sucesso": False,
            "mensagem": "Data ou horário inválido. Utilize AAAA-MM-DD e HH:MM."
        }

    hora_formatada = hora_consulta.isoformat(timespec="seconds")

    headers = {
        "Authorization": f"Bearer {token}"
    }

    try:
        with httpx.Client(timeout=15.0) as client:
            resposta_psicologos = client.get(
                f"{_obter_base_url()}/psicologos",
                headers=headers
            )

            if resposta_psicologos.status_code in (401, 403):
                return {
                    "sucesso": False,
                    "mensagem": "Sua sessão expirou ou não possui permissão."
                }

            resposta_psicologos.raise_for_status()
            psicologos = resposta_psicologos.json()

            psicologo = next(
                (
                    item for item in psicologos
                    if item.get("id") == psicologo_id
                ),
                None
            )

            if not psicologo:
                return {
                    "sucesso": False,
                    "mensagem": "O psicólogo informado não foi encontrado."
                }

            resposta_horarios = client.get(
                f"{_obter_base_url()}/ia/ferramentas/horarios",
                params={
                    "psicologoId": psicologo_id,
                    "data": data_consulta.isoformat()
                },
                headers=headers
            )

            if resposta_horarios.status_code in (401, 403):
                return {
                    "sucesso": False,
                    "mensagem": "Sua sessão expirou ou não possui permissão."
                }

            resposta_horarios.raise_for_status()
            horarios = resposta_horarios.json()

            horarios_normalizados = {
                time.fromisoformat(str(item)).isoformat(
                    timespec="seconds"
                )
                for item in horarios
            }

            if hora_formatada not in horarios_normalizados:
                return {
                    "sucesso": False,
                    "mensagem": "Esse horário não está disponível. Consulte os horários novamente."
                }

            reserva = criar_reserva_pendente(
                request_id=request_id,
                token=token,
                dados={
                    "psicologo_id": psicologo_id,
                    "psicologo": psicologo.get("nomePsicologa"),
                    "data": data_consulta.isoformat(),
                    "hora": hora_formatada,
                    "observacao": observacao
                }
            )

            return {
                "sucesso": True,
                "mensagem": (
                    "Reserva pendente criada. "
                    "O agendamento ainda NÃO foi confirmado. "
                    "Aguarde a confirmação explícita do usuário."
                ),
                "expira_em": reserva["expira_em"]
            }

    except httpx.TimeoutException:
        return {
            "sucesso": False,
            "mensagem": "A validação demorou demais. Tente novamente."
        }

    except httpx.HTTPStatusError:
        return {
            "sucesso": False,
            "mensagem": "Não foi possível validar os dados da reserva no momento."
        }

    except (httpx.HTTPError, ValueError, TypeError):
        return {
            "sucesso": False,
            "mensagem": "Não foi possível validar os dados da reserva no momento."
        }
