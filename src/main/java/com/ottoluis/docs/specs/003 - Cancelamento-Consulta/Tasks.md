# Tarefas — Cancelamento de consulta

**ID:** TASK-003  
**Entrada:** `spec.md` e `plan.md` da SPEC-003.

**Objetivo:** implementar e validar o cancelamento seguro de consultas na plataforma Mãos Amigas.

**Formato:** [ID] [P?] ação com caminho e resultado verificável.

- `[P]` indica tarefas que podem ser executadas em paralelo.
- Marcar `[x]` somente após implementação e verificação.

## Fase 1 — Preparação

- [ ] T001 Revisar os requisitos em `src/main/java/com/ottoluis/docs/specs/003 - Cancelamento-Consulta/Spec.md`.
- [ ] T002 [P] Revisar o plano em `src/main/java/com/ottoluis/docs/specs/003 - Cancelamento-Consulta/plan.md`.
- [ ] T003 Inspecionar `ConsultaController` em `src/main/java/com/ottoluis/MaosAmigas/controller/ConsultaController.java`.
- [ ] T004 Inspecionar `ConsultaService` em `src/main/java/com/ottoluis/MaosAmigas/services/ConsultaService.java`.
- [ ] T005 Inspecionar `ConsultaRepository`, `Consulta` e `StatusConsulta`.

## Fase 2 — Backend

- [ ] T006 Revisar o endpoint `PATCH /consulta/{id}/cancelar`.
- [ ] T007 Revisar a identificação do usuário autenticado no controller.
- [ ] T008 Garantir a verificação de propriedade da consulta para `ROLE_USUARIO`.
- [ ] T009 Garantir que `ROLE_ADMIN` respeite as permissões administrativas implementadas.
- [ ] T010 Verificar o tratamento de consulta inexistente.
- [ ] T011 Verificar a rejeição de consultas com status `CANCELADA`.
- [ ] T012 Verificar a rejeição de consultas com status `CONCLUIDA`.
- [ ] T013 Garantir a alteração do status para `CANCELADA`.
- [ ] T014 Garantir a persistência da alteração sem excluir a consulta.
- [ ] T015 Revisar as respostas HTTP para sucesso e erros.
- [ ] T016 Verificar que consultas canceladas não são consideradas ocupadas pelo mecanismo de disponibilidade.

## Fase 3 — Frontend

- [ ] T017 Inspecionar a tela de listagem de consultas do frontend `Maos-Amigas-Front`.
- [ ] T018 [P] Revisar os componentes visuais relacionados às consultas.
- [ ] T019 Implementar ou revisar a ação de cancelamento na consulta selecionada.
- [ ] T020 Implementar confirmação antes de enviar a solicitação.
- [ ] T021 Integrar a ação ao endpoint `PATCH /consulta/{id}/cancelar`.
- [ ] T022 Atualizar a interface após receber a resposta de sucesso.
- [ ] T023 Exibir mensagens adequadas para erros 401, 404 e 409.
- [ ] T024 Impedir que a interface apresente cancelamento como concluído antes da confirmação da API.

## Fase 4 — Testes

- [ ] T025 [P] Criar testes para cancelamento válido em `src/test/java/com/ottoluis/MaosAmigas/`.
- [ ] T026 [P] Criar testes para usuário sem autenticação.
- [ ] T027 Criar testes para impedir cancelamento de consulta de outro usuário.
- [ ] T028 Criar testes para operação administrativa autorizada.
- [ ] T029 Criar testes para consulta inexistente.
- [ ] T030 Criar testes para consulta já cancelada.
- [ ] T031 Criar testes para consulta concluída.
- [ ] T032 Verificar a atualização do status no banco.
- [ ] T033 Verificar que o registro original permanece no PostgreSQL.
- [ ] T034 Criar teste de integração verificando que o horário cancelado pode ser utilizado em novo agendamento, quando todas as demais regras forem atendidas.

## Fase 5 — Validação e entrega

- [ ] T035 Executar os testes automatizados do backend.
- [ ] T036 Executar os testes disponíveis do frontend.
- [ ] T037 Testar o cancelamento pela interface.
- [ ] T038 Testar o endpoint diretamente pela API.
- [ ] T039 Verificar os dados atualizados no PostgreSQL.
- [ ] T040 Verificar que a consulta cancelada não bloqueia novos agendamentos.
- [ ] T041 Conferir a rastreabilidade entre requisitos, critérios de aceitação e testes.
- [ ] T042 Atualizar a documentação com os resultados realmente verificados.

## Dependências

- T001 a T005 estabelecem a preparação.
- T006 a T016 verificam e implementam as regras do backend.
- T017 a T024 integram a interface.
- T025 a T034 verificam o comportamento.
- T035 a T042 realizam a validação final.

## Critérios de conclusão

- [ ] Cancelamento autorizado funcionando.
- [ ] Consulta de outro usuário protegida.
- [ ] Consulta concluída não pode ser cancelada.
- [ ] Cancelamento repetido rejeitado.
- [ ] Status atualizado para `CANCELADA`.
- [ ] Registro preservado no banco.
- [ ] Horário liberado para novos agendamentos.
- [ ] Interface apresenta o resultado real da API.
- [ ] Testes executados e resultados registrados.