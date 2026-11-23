# Especificação de funcionalidade: Cadastro de usuário

**ID:** SPEC-002  
**Status:** Pendente Validação com o PO  
**Responsáveis:** Otto Luís, Luiz Felipe, Victor Raphael, Rafael Loureiro, Wellington Perovano  
**Última atualização:** 23/11/2026

**Projeto:** Mãos Amigas  
**Módulo:** Usuários e Autenticação

## 1. Objetivo

Permitir que novos usuários realizem seu cadastro na plataforma Mãos Amigas, criando uma conta para acessar os serviços de atendimento psicológico online.

## 2. História do usuário

Como visitante da plataforma, quero realizar meu cadastro informando meus dados pessoais e credenciais de acesso, para utilizar os serviços disponíveis.

## 3. Pré-condições

- A plataforma deve estar disponível.
- A API deve possuir conexão com o PostgreSQL.
- O visitante deve acessar a funcionalidade de cadastro.
- O e-mail informado não deve estar associado a outra conta.

## 4. Fluxo principal

1. O visitante acessa a página de cadastro.
2. O sistema apresenta o formulário.
3. O visitante informa nome, e-mail e senha.
4. O visitante solicita a criação da conta.
5. O frontend envia os dados para a API REST.
6. A API valida os campos obrigatórios.
7. O sistema verifica se o e-mail já está cadastrado.
8. A senha é processada utilizando mecanismo seguro de hash.
9. O usuário é registrado no PostgreSQL.
10. O sistema atribui o perfil padrão `ROLE_USUARIO`.
11. A API retorna uma resposta de sucesso.
12. A interface apresenta a confirmação do cadastro.

## 5. Fluxos alternativos e exceções

**FA01 — E-mail já cadastrado**

1. O visitante informa um e-mail existente.
2. O sistema identifica a duplicidade.
3. O cadastro é rejeitado.
4. O sistema apresenta uma mensagem informativa.

**FA02 — Campos obrigatórios ausentes**

1. O visitante deixa campos obrigatórios vazios.
2. O sistema identifica os campos ausentes.
3. A operação é interrompida.
4. A interface solicita a correção.

**FA03 — E-mail inválido**

1. O visitante informa um e-mail inválido.
2. O sistema rejeita os dados.
3. A interface apresenta uma mensagem de validação.

**FA04 — Senha inválida**

1. O visitante informa uma senha que não atende aos critérios definidos.
2. O sistema rejeita a solicitação.
3. A interface solicita a correção.

**FE01 — Falha no banco de dados**

1. O sistema tenta registrar o usuário.
2. O banco apresenta indisponibilidade.
3. O cadastro não é confirmado.
4. A API retorna erro apropriado.

## 6. Regras de negócio

- RN01 — O usuário deve informar os dados obrigatórios exigidos pelo sistema.
- RN02 — O e-mail deve possuir formato válido.
- RN03 — O e-mail deve ser único no sistema.
- RN04 — A senha nunca pode ser armazenada em texto puro.
- RN05 — A senha deve utilizar mecanismo seguro de hash, como BCrypt.
- RN06 — O cadastro público deve atribuir o perfil padrão `ROLE_USUARIO`.
- RN07 — O visitante não pode atribuir privilégios administrativos a si próprio.
- RN08 — Os dados devem ser validados antes da persistência.
- RN09 — A API não deve expor senhas nas respostas.
- RN10 — O cadastro deve respeitar os mecanismos de segurança da aplicação.

## 7. Pós-condições

- Usuário registrado no banco de dados.
- E-mail armazenado de maneira única.
- Senha armazenada de forma segura.
- Perfil de acesso corretamente associado.
- Usuário apto a realizar autenticação.

## 8. Critérios de aceitação

- CA01 — Permitir cadastro com dados válidos.
- CA02 — Rejeitar e-mails duplicados.
- CA03 — Rejeitar e-mails inválidos.
- CA04 — Impedir cadastro sem campos obrigatórios.
- CA05 — Garantir armazenamento seguro da senha.
- CA06 — Impedir criação pública de administradores.
- CA07 — Não expor senhas nas respostas.
- CA08 — Apresentar mensagens adequadas de erro.

## 9. Componentes envolvidos

- UsuarioController
- UsuarioService
- UsuarioRepository
- Usuario
- DTOs de cadastro
- Configurações de segurança
- PostgreSQL

## 10. Testes necessários

- Cadastro com dados válidos.
- Cadastro com e-mail duplicado.
- Cadastro com e-mail inválido.
- Cadastro sem nome.
- Cadastro sem senha.
- Verificação do hash da senha.
- Verificação do perfil atribuído.
- Tentativa de atribuição indevida de perfil administrativo.
- Verificação de que a senha não é retornada pela API.
- Testes de integração com PostgreSQL.