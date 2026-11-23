# Tarefas — Cadastro de usuário

**ID:** TASK-002  
**Entrada:** `spec.md` e `plan.md` da SPEC-002.

**Objetivo:** implementar e validar o cadastro de usuários da plataforma Mãos Amigas.

**Formato:** [ID] [P?] ação com caminho e resultado verificável.

- `[P]` indica tarefas que podem ser executadas em paralelo.
- Marcar `[x]` somente após implementação e verificação.

## Fase 1 — Preparação

- [ ] T001 Revisar os requisitos e critérios de aceitação em `src/main/java/com/ottoluis/docs/specs/002 - Cadastro-Usuario/spec.md`.
- [ ] T002 [P] Revisar o plano de implementação em `src/main/java/com/ottoluis/docs/specs/002 - Cadastro-Usuario/plan.md`.
- [ ] T003 Inspecionar a entidade `Usuario` em `src/main/java/com/ottoluis/MaosAmigas/models/Usuario.java`.
- [ ] T004 Inspecionar `UsuarioController` em `src/main/java/com/ottoluis/MaosAmigas/controller/UsuarioController.java`.
- [ ] T005 Inspecionar `UsuarioRepository` e as configurações de segurança existentes.

## Fase 2 — Backend

- [ ] T006 Revisar os dados obrigatórios da entidade `Usuario`.
- [ ] T007 [P] Revisar os DTOs utilizados no cadastro e garantir que não retornem a senha.
- [ ] T008 Implementar ou revisar a validação dos campos recebidos pela API.
- [ ] T009 Implementar ou revisar a verificação de e-mail já cadastrado.
- [ ] T010 Implementar ou revisar a normalização do e-mail antes da persistência.
- [ ] T011 Garantir a utilização de BCrypt para armazenar a senha.
- [ ] T012 Garantir a atribuição exclusiva do perfil padrão `ROLE_USUARIO` no cadastro público.
- [ ] T013 Garantir que o cadastro não permita definir privilégios administrativos pelo corpo da requisição.
- [ ] T014 Revisar o tratamento de erros e respostas do endpoint de cadastro.
- [ ] T015 Verificar a persistência dos dados no PostgreSQL.

## Fase 3 — Frontend

- [ ] T016 Inspecionar a página de cadastro existente no frontend `Maos-Amigas-Front`.
- [ ] T017 [P] Revisar os componentes e estilos do formulário de cadastro.
- [ ] T018 Integrar o formulário ao endpoint `/usuarios`, respeitando o contrato real da API.
- [ ] T019 Implementar mensagens de validação para campos obrigatórios e e-mail inválido.
- [ ] T020 Implementar tratamento visual para e-mail já cadastrado.
- [ ] T021 Implementar feedback de sucesso após o cadastro.
- [ ] T022 Verificar o comportamento da interface durante carregamento e falhas de comunicação.

## Fase 4 — Testes

- [ ] T023 [P] Criar testes para cadastro com dados válidos.
- [ ] T024 [P] Criar testes para e-mail duplicado.
- [ ] T025 Criar testes para e-mail inválido e campos obrigatórios ausentes.
- [ ] T026 Criar testes para verificar o armazenamento da senha com BCrypt.
- [ ] T027 Criar testes para verificar a atribuição de `ROLE_USUARIO`.
- [ ] T028 Criar testes para impedir atribuição pública de `ROLE_ADMIN`.
- [ ] T029 Criar testes para garantir que a resposta não exponha a senha.
- [ ] T030 Verificar a persistência dos usuários no PostgreSQL.

## Fase 5 — Validação e entrega

- [ ] T031 Executar os testes automatizados do backend.
- [ ] T032 Executar os testes disponíveis do frontend.
- [ ] T033 Testar o cadastro pela interface.
- [ ] T034 Testar o cadastro diretamente pela API.
- [ ] T035 Verificar o usuário criado no PostgreSQL.
- [ ] T036 Conferir a rastreabilidade entre requisitos, critérios de aceitação e testes.
- [ ] T037 Atualizar a documentação com os resultados realmente verificados.

## Dependências

- T001 a T005 estabelecem o entendimento da implementação existente.
- T006 a T015 implementam e validam o backend.
- T016 a T022 integram a interface ao backend.
- T023 a T030 verificam as regras de negócio.
- T031 a T037 realizam a validação final.

## Critérios de conclusão

- [ ] Cadastro com dados válidos.
- [ ] E-mail único.
- [ ] Senha armazenada com BCrypt.
- [ ] Perfil padrão atribuído.
- [ ] Nenhuma atribuição indevida de privilégios.
- [ ] Respostas sem exposição de senha.
- [ ] Integração com frontend funcionando.
- [ ] Testes executados e resultados registrados.