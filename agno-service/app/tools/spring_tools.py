import os
from datetime import datetime

import httpx
from agno.run import RunContext


def _obter_base_url() -> str:
    return (
        "http://"
        + os.getenv("SPRING_API_HOST", "127.0.0.1")
        + ":"
        + os.getenv("SPRING_API_PORT", "8080")
    )


def _obter_token(run_context: RunContext) -> str | None:
    dependencies = run_context.dependencies or {}
    return dependencies.get("jwt_token")


def buscar_psicologos(run_context: RunContext) -> dict:
    """
    Busca os psicólogos cadastrados na plataforma Mãos Amigas.
    Retorna somente ID, nome e especialidade.
    """

    token = _obter_token(run_context)

    if not token:
        return {
            "sucesso": False,
            "mensagem": "Autenticação necessária para consultar os profissionais."
        }

    try:
        response = httpx.get(
            f"{_obter_base_url()}/psicologos",
            headers={"Authorization": f"Bearer {token}"},
            timeout=15.0
        )

        if response.status_code == 401:
            return {
                "sucesso": False,
                "mensagem": "Sua sessão expirou. Faça login novamente."
            }

        if response.status_code == 403:
            return {
                "sucesso": False,
                "mensagem": "Você não tem permissão para consultar os profissionais."
            }

        response.raise_for_status()

        psicologos = response.json()

        return {
            "sucesso": True,
            "quantidade": len(psicologos),
            "psicologos": [
                {
                    "id": psicologo.get("id"),
                    "nome": psicologo.get("nomePsicologa"),
                    "especialidade": psicologo.get("especialidade")
                }
                for psicologo in psicologos
            ]
        }

    except httpx.TimeoutException:
        return {
            "sucesso": False,
            "mensagem": "A consulta demorou demais. Tente novamente."
        }

    except httpx.HTTPError:
        return {
            "sucesso": False,
            "mensagem": "Não foi possível consultar os profissionais no momento."
        }


def listar_horarios_disponiveis(
    psicologo_id: int,
    data: str,
    run_context: RunContext
) -> dict:
    """
    Consulta os horários disponíveis de um psicólogo em uma data.
    A disponibilidade é validada pelo backend Spring Boot.
    """

    token = _obter_token(run_context)

    if not token:
        return {
            "sucesso": False,
            "mensagem": "Autenticação necessária para consultar horários."
        }

    try:
        datetime.strptime(data, "%Y-%m-%d")
    except (ValueError, TypeError):
        return {
            "sucesso": False,
            "mensagem": "Data inválida. Utilize o formato AAAA-MM-DD."
        }

    if psicologo_id <= 0:
        return {
            "sucesso": False,
            "mensagem": "ID de psicólogo inválido."
        }

    try:
        response = httpx.get(
            f"{_obter_base_url()}/ia/ferramentas/horarios",
            params={
                "psicologoId": psicologo_id,
                "data": data
            },
            headers={"Authorization": f"Bearer {token}"},
            timeout=15.0
        )

        if response.status_code == 401:
            return {
                "sucesso": False,
                "mensagem": "Sua sessão expirou. Faça login novamente."
            }

        if response.status_code == 403:
            return {
                "sucesso": False,
                "mensagem": "Você não tem permissão para consultar horários."
            }

        response.raise_for_status()

        horarios = response.json()

        return {
            "sucesso": True,
            "psicologo_id": psicologo_id,
            "data": data,
            "quantidade": len(horarios),
            "horarios_disponiveis": horarios
        }

    except httpx.TimeoutException:
        return {
            "sucesso": False,
            "mensagem": "A consulta de horários demorou demais. Tente novamente."
        }

    except httpx.HTTPError:
        return {
            "sucesso": False,
            "mensagem": "Não foi possível consultar os horários no momento."
        }
