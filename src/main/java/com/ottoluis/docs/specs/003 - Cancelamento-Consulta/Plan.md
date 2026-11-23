# Plano de implementação — Cancelamento de consulta

**ID:** PLAN-003  
**Funcionalidade:** Cancelamento de consulta  
**Projeto:** Mãos Amigas  
**Módulo:** Consultas

## 1. Resumo técnico

O cancelamento de consultas permite que usuários autenticados solicitem a alteração do status de uma consulta para `CANCELADA`.

A implementação utiliza a API REST Spring Boot, persistência PostgreSQL e controle de acesso por perfil.

O cancelamento não deve excluir o registro da consulta. A alteração do status deve permitir que o horário seja considerado disponível para novos agendamentos.

## 2. Contexto técnico

- **Linguagem:** Java.
- **Framework:** Spring Boot.
- **API:** REST.
- **Banco de dados:** PostgreSQL.
- **Persistência:** JPA/Hibernate.
- **Segurança:** Spring Security e JWT.
- **Autorização:** `ROLE_USUARIO` e `ROLE_ADMIN`.
- **Frontend:** React.
- **Testes:** JUnit e Mockito.

## 3. Arquitetura

```text
Usuário autenticado
       |
       v
Frontend Mãos Amigas
       |
       v
PATCH /consulta/{id}/cancelar
       |
       v
ConsultaController
       |
       v
ConsultaService
       |
       +--> Verificar autenticação
       |
       +--> Verificar propriedade/permissão
       |
       +--> Verificar status atual
       |
       v
ConsultaRepository
       |
       v
PostgreSQL
       |
       v
Resposta atualizada
```

## 4. Componentes envolvidos

### ConsultaController

Responsável por receber a solicitação de cancelamento por meio do endpoint PATCH.

### ConsultaService

Responsável por verificar a existência da consulta, validar a autorização e executar a mudança de status.

### ConsultaRepository

Responsável por localizar e persistir a consulta.

### StatusConsulta

Enum responsável pelos estados possíveis da consulta, incluindo `AGENDADA`, `CONFIRMADA`, `CANCELADA` e `CONCLUIDA`.

### Segurança

A API deve identificar o usuário autenticado e respeitar as permissões de acesso.

## 5. Estratégia de implementação

1. Revisar o endpoint de cancelamento existente.
2. Revisar a identificação do usuário autenticado.
3. Garantir que usuários comuns só cancelem suas próprias consultas.
4. Permitir operações administrativas conforme as permissões existentes.
5. Verificar se a consulta existe.
6. Verificar se o status permite cancelamento.
7. Rejeitar consultas já canceladas ou concluídas.
8. Atualizar o status para `CANCELADA`.
9. Persistir a alteração no PostgreSQL.
10. Retornar os dados atualizados da consulta.
11. Garantir que consultas canceladas não bloqueiem novos agendamentos.
12. Integrar a ação à interface do usuário.

## 6. Endpoint

**Método:** PATCH

**Rota:** `/consulta/{id}/cancelar`

**Autenticação:** Obrigatória.

**Resposta de sucesso:** HTTP 200 OK, com os dados atualizados da consulta.

**Possíveis respostas de erro:**

- 401 Unauthorized — usuário não autenticado.
- 404 Not Found — consulta inexistente ou não acessível ao usuário comum.
- 409 Conflict — consulta já cancelada ou concluída.

## 7. Estratégia de testes

- Cancelamento de consulta válida.
- Cancelamento de consulta de outro usuário.
- Cancelamento por administrador.
- Consulta inexistente.
- Consulta já cancelada.
- Consulta concluída.
- Usuário não autenticado.
- Verificação do status atualizado.
- Verificação da permanência do registro.
- Verificação da liberação do horário para novo agendamento.

## 8. Riscos e mitigação

| Risco | Mitigação | Verificação |
|---|---|---|
| Cancelamento indevido | Verificação de propriedade | Teste de autorização |
| Cancelamento repetido | Verificação do status atual | Teste de conflito |
| Cancelamento de consulta concluída | Regra de negócio no serviço | Teste de estado |
| Exclusão acidental | Atualização de status em vez de exclusão | Verificação no banco |
| Horário continuar bloqueado | Ignorar status CANCELADA na consulta de conflitos | Teste de novo agendamento |
| Falha na persistência | Transação e tratamento de exceções | Teste de integração |

## 9. Critério de conclusão

A funcionalidade será considerada concluída quando o usuário autorizado conseguir cancelar uma consulta válida, o sistema preservar seu registro, atualizar o status e liberar o horário para novos agendamentos, respeitando todas as regras de acesso.