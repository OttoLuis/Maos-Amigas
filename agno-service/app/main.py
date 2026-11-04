from fastapi import FastAPI
from pydantic import BaseModel

from app.agents.acolhimento import agent_acolhimento

app = FastAPI(title="Mãos Amigas - Agno")


class ChatRequest(BaseModel):
    mensagem: str


@app.post("/agentes/acolhimento")
def acolhimento(request: ChatRequest):
    resposta = agent_acolhimento.run(request.mensagem)

    return {
        "resposta": resposta.content
    }
