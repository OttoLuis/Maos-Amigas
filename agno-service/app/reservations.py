import hashlib
import secrets
from datetime import datetime, timedelta, timezone
from threading import RLock
from uuid import uuid4


TEMPO_EXPIRACAO_MINUTOS = 10

_reservas: dict[str, dict] = {}
_reservas_por_requisicao: dict[str, str] = {}
_lock = RLock()


class ReservaExpirada(Exception):
    pass


def _hash_token(token: str) -> str:
    return hashlib.sha256(token.encode("utf-8")).hexdigest()


def _limpar_expiradas() -> None:
    agora = datetime.now(timezone.utc)

    expiradas = [
        reserva_id
        for reserva_id, reserva in _reservas.items()
        if reserva["expira_em"] <= agora
    ]

    for reserva_id in expiradas:
        _reservas.pop(reserva_id, None)

    requisicoes_expiradas = [
        request_id
        for request_id, reserva_id in _reservas_por_requisicao.items()
        if reserva_id not in _reservas
    ]

    for request_id in requisicoes_expiradas:
        _reservas_por_requisicao.pop(request_id, None)


def criar_reserva_pendente(
    request_id: str,
    token: str,
    dados: dict
) -> dict:
    agora = datetime.now(timezone.utc)
    reserva_id = str(uuid4())

    reserva = {
        "id": reserva_id,
        "token_hash": _hash_token(token),
        "psicologo_id": dados["psicologo_id"],
        "psicologo": dados["psicologo"],
        "data": dados["data"],
        "hora": dados["hora"],
        "observacao": dados.get("observacao"),
        "criada_em": agora,
        "expira_em": agora + timedelta(
            minutes=TEMPO_EXPIRACAO_MINUTOS
        )
    }

    with _lock:
        _limpar_expiradas()

        reserva_anterior = _reservas_por_requisicao.get(request_id)

        if reserva_anterior:
            _reservas.pop(reserva_anterior, None)

        _reservas[reserva_id] = reserva
        _reservas_por_requisicao[request_id] = reserva_id

    return {
        "id": reserva_id,
        "expira_em": reserva["expira_em"].isoformat()
    }


def obter_reserva_da_requisicao(
    request_id: str,
    token: str
) -> dict | None:
    with _lock:
        _limpar_expiradas()

        reserva_id = _reservas_por_requisicao.pop(
            request_id,
            None
        )

        if not reserva_id:
            return None

        reserva = _reservas.get(reserva_id)

        if not reserva:
            return None

        hash_token = _hash_token(token)

        if not secrets.compare_digest(
            reserva["token_hash"],
            hash_token
        ):
            return None

        return {
            "id": reserva["id"],
            "expira_em": reserva["expira_em"].isoformat()
        }


def consumir_reserva(
    reserva_id: str,
    token: str
) -> dict | None:
    with _lock:
        reserva = _reservas.get(reserva_id)

        if not reserva:
            return None

        if reserva["expira_em"] <= datetime.now(timezone.utc):
            _reservas.pop(reserva_id, None)

            requisicoes = [
                request_id
                for request_id, identificador in
                _reservas_por_requisicao.items()
                if identificador == reserva_id
            ]

            for request_id in requisicoes:
                _reservas_por_requisicao.pop(request_id, None)

            raise ReservaExpirada()

        hash_token = _hash_token(token)

        if not secrets.compare_digest(
            reserva["token_hash"],
            hash_token
        ):
            return None

        # Remove antes da chamada ao Spring Boot.
        # Isso impede que duas confirmações usem a mesma reserva.
        _reservas.pop(reserva_id, None)

        requisicoes = [
            request_id
            for request_id, identificador in
            _reservas_por_requisicao.items()
            if identificador == reserva_id
        ]

        for request_id in requisicoes:
            _reservas_por_requisicao.pop(request_id, None)

        return {
            "psicologo_id": reserva["psicologo_id"],
            "data": reserva["data"],
            "hora": reserva["hora"],
            "observacao": reserva["observacao"]
        }
