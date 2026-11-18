# Especificação de funcionalidade: [Cancelamento de consulta]

**ID:** SPEC-[003]  
**Status:** Pendente Validação com o PO
**Responsáveis:** [Otto Luís, Luiz Felipe, Victor Raphael, Rafael Loureiro, Wellington Perovano]  
**Última atualização:** [18/11/2026]
**Projeto:** Mãos Amigas  
**Módulo:** Consultas  

## 1. Objetivo

Permitir que um usuário autenticado cancele uma consulta psicológica previamente agendada na plataforma Mãos Amigas.
O cancelamento deve atualizar o status da consulta, preservar seu histórico e liberar o horário para novos agendamentos.

## 2. História do usuário

Como usuário da plataforma Mãos Amigas, quero cancelar uma consulta agendada, para que eu possa desistir de um atendimento quando não puder comparecer.

## 3. Pré-condições

- O usuário deve possuir uma conta cadastrada.
- O usuário deve estar autenticado.
- A consulta deve existir no sistema.
- A consulta deve estar associada ao usuário solicitante.
- O status deve permitir cancelamento.

## 4. Fluxo principal

1. O usuário acessa sua área de consultas.
2. O sistema apresenta as consultas vinculadas à sua conta.
3. O usuário seleciona uma consulta.
4. O sistema apresenta os dados do agendamento.
5. O usuário solicita o cancelamento.
6. O sistema solicita confirmação.
7. O usuário confirma a operação.
8. O frontend envia a solicitação para a API REST.
9. A API verifica a autenticação.
10. O sistema verifica a existência da consulta.
11. O sistema verifica se a consulta pertence ao usuário.
12. O sistema verifica se o status permite cancelamento.
13. O sistema atualiza o status para CANCELADA.
14. A API confirma a operação.
15. A interface apresenta uma mensagem de sucesso.

## 5. Fluxos alternativos e exceções

**FA01 — Usuário desiste do cancelamento**

1. O usuário solicita o cancelamento.
2. O sistema solicita confirmação.
3. O usuário decide não continuar.
4. O sistema mantém a consulta sem alterações.

**FA02 — Consulta já cancelada**

1. O usuário solicita o cancelamento.
2. O sistema identifica o status CANCELADA.
3. A operação é rejeitada.
4. O sistema informa que a consulta já foi cancelada.

**FA03 — Consulta concluída**

1. O usuário solicita o cancelamento.
2. O sistema identifica o status CONCLUIDA.
3. A operação é rejeitada.
4. O sistema informa que consultas concluídas não podem ser canceladas.

**FA04 — Consulta inexistente**

1. O usuário solicita o cancelamento de uma consulta inexistente.
2. O sistema não encontra o registro.
3. A API retorna HTTP 404 Not Found.

**FA05 — Consulta de outro usuário**

1. O usuário tenta cancelar uma consulta de outra conta.
2. O sistema verifica a propriedade da consulta.
3. A operação é rejeitada.
4. A API retorna HTTP 403 Forbidden.

**FE01 — Usuário não autenticado**

1. O visitante tenta acessar a funcionalidade.
2. O sistema identifica a ausência de autenticação válida.
3. A operação é rejeitada.
4. A API retorna HTTP 401 Unauthorized.

**FE02 — Falha na atualização**

1. O sistema inicia o cancelamento.
2. Ocorre uma falha durante a persistência.
3. A operação não é confirmada.
4. A API retorna um erro apropriado.

## 6. Regras de negócio

- RN01 — Somente usuários autenticados podem solicitar cancelamento.
- RN02 — O usuário somente pode cancelar suas próprias consultas, respeitadas as permissões administrativas implementadas.
- RN03 — Consultas inexistentes não podem ser canceladas.
- RN04 — Consultas CANCELADAS não podem ser canceladas novamente.
- RN05 — Consultas CONCLUIDAS não podem ser canceladas.
- RN06 — O cancelamento deve atualizar o status para CANCELADA.
- RN07 — O cancelamento não deve excluir o registro do banco.
- RN08 — O histórico da consulta deve ser preservado.
- RN09 — Consultas canceladas não devem bloquear horários para novos agendamentos.
- RN10 — A operação deve respeitar autenticação e autorização.
- RN11 — O sistema deve validar o estado atual antes de atualizar.
- RN12 — A atualização deve preservar a integridade dos dados em operações concorrentes.

## 7. Pós-condições

- A consulta permanece registrada no banco.
- O status passa a ser CANCELADA.
- O horário deixa de ser considerado ocupado.
- A API retorna a confirmação da operação.
- A consulta deixa de aparecer como agendada ou confirmada nas consultas ativas.

## 8. Critérios de aceitação

- CA01 — Permitir cancelar uma consulta válida.
- CA02 — Rejeitar cancelamento sem autenticação.
- CA03 — Impedir cancelamento de consultas de outros usuários.
- CA04 — Rejeitar consultas inexistentes.
- CA05 — Impedir cancelamento de consultas concluídas.
- CA06 — Impedir cancelamento repetido.
- CA07 — Atualizar o status para CANCELADA.
- CA08 — Preservar o registro original.
- CA09 — Liberar o horário para novos agendamentos.
- CA10 — Preservar a integridade dos dados em operações concorrentes.

## 9. Componentes envolvidos

- ConsultaController
- ConsultaService
- ConsultaRepository
- Consulta
- StatusConsulta
- DTOs relacionados às consultas
- Configurações de segurança
- PostgreSQL

## 10. Endpoint relacionado

**Método HTTP:** PATCH

**Rota:** `/consulta/{id}/cancelar`

**Autenticação:** Obrigatória.

**Autorização:** Usuário autenticado, respeitando a propriedade da consulta e as permissões administrativas implementadas.

**Códigos HTTP esperados:**

- 200 OK — Cancelamento realizado.
- 401 Unauthorized — Usuário não autenticado.
- 403 Forbidden — Usuário sem autorização.
- 404 Not Found — Consulta inexistente.
- 400 Bad Request — Operação incompatível com o estado atual.

## 11. Testes necessários

- Cancelamento de consulta válida.
- Cancelamento sem autenticação.
- Tentativa de cancelar consulta de outro usuário.
- Cancelamento de consulta inexistente.
- Tentativa de cancelar consulta já cancelada.
- Tentativa de cancelar consulta concluída.
- Verificação da atualização do status.
- Verificação da permanência do registro no banco.
- Verificação da liberação do horário.
- Testes de concorrência e integridade dos dados.