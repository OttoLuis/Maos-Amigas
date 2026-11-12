"""
Infraestrutura compartilhada do Mãos Amigas.

Centraliza a configuração do modelo Groq.
O FastAPI continua responsável pelas sessões e pelo histórico.
"""

import os
from pathlib import Path

from dotenv import load_dotenv
from agno.models.groq import Groq


# Diretório do serviço Agno
PASTA = Path(__file__).resolve().parent
RAIZ = PASTA.parent

# Carrega as variáveis do .env
load_dotenv(RAIZ / ".env", override=False)


# ---------------------------------------------------------
# MODELO DE INTELIGÊNCIA ARTIFICIAL
# ---------------------------------------------------------

MODELO_ID = os.getenv(
    "GROQ_MODEL",
    "openai/gpt-oss-20b"
)


def modelo() -> Groq:
    """Cria o modelo de inteligência artificial do Mãos Amigas."""

    api_key = os.getenv("GROQ_API_KEY")

    if not api_key:
        raise ValueError(
            "GROQ_API_KEY não encontrada no arquivo .env"
        )

    return Groq(
        id=MODELO_ID,
        api_key=api_key
    )
