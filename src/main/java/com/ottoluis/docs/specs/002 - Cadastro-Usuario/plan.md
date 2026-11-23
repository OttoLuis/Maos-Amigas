# Plano de implementação — Cadastro de usuário

**ID:** PLAN-002  
**Funcionalidade:** Cadastro de usuário  
**Projeto:** Mãos Amigas  
**Módulo:** Usuários e Autenticação

## 1. Resumo técnico

O cadastro de usuários permite que visitantes criem uma conta na plataforma Mãos Amigas para acessar os serviços de suporte psicológico online.

A funcionalidade utiliza a API REST desenvolvida com Java e Spring Boot, persistência em PostgreSQL e validações no backend.

O cadastro deve garantir a unicidade do e-mail, o armazenamento seguro da senha e a atribuição do perfil padrão `ROLE_USUARIO`.

## 2. Contexto técnico

- **Linguagem:** Java.
- **Framework:** Spring Boot.
- **API:** REST.
- **Banco de dados:** PostgreSQL.
- **Persistência:** Spring Data JPA / Hibernate.
- **Validação:** Bean Validation.
- **Segurança:** Spring Security.
- **Criptografia de senha:** BCrypt.
- **Autenticação posterior:** JWT.
- **Frontend:** React.
- **Testes:** JUnit e Mockito.

## 3. Arquitetura

```text
Visitante
   |
   v
Frontend Mãos Amigas
   |
   v
UsuarioController
   |
   v
Validação dos dados
   |
   v
Verificação do e-mail
   |
   v
Criptografia da senha
   |
   v
UsuarioRepository
   |
   v
PostgreSQL
```

## 4. Componentes envolvidos

### UsuarioController

Responsável por receber os dados enviados pelo frontend e disponibilizar a operação de cadastro pela API REST.

### UsuarioService

Responsável pela lógica de cadastro, validação de regras de negócio e preparação dos dados para persistência, caso essa camada seja utilizada na implementação.

### UsuarioRepository

Responsável pelas operações de persistência e consulta de usuários no PostgreSQL.

### Usuario

Entidade responsável por representar os dados do usuário e suas associações com os perfis de acesso.

### Segurança

O mecanismo de segurança deve garantir que a senha seja armazenada com BCrypt e que o cadastro público não permita atribuição de privilégios administrativos.

## 5. Estratégia de implementação

1. Revisar a entidade Usuario e os campos obrigatórios.
2. Revisar o endpoint de cadastro existente.
3. Validar os dados recebidos pela API.
4. Verificar a existência de e-mail cadastrado.
5. Garantir a normalização e unicidade do e-mail.
6. Aplicar BCrypt à senha antes da persistência.
7. Associar o perfil padrão `ROLE_USUARIO`.
8. Persistir o usuário no PostgreSQL.
9. Garantir que a resposta não exponha a senha ou informações sensíveis.
10. Integrar o formulário de cadastro do frontend à API.
11. Validar os cenários de sucesso e erro.

## 6. Estratégia de testes

- Cadastro com dados válidos.
- Cadastro com e-mail já utilizado.
- Cadastro com e-mail inválido.
- Cadastro com campos obrigatórios ausentes.
- Verificação da criptografia da senha.
- Verificação do perfil padrão.
- Tentativa de atribuição de perfil administrativo.
- Verificação de resposta sem senha.
- Verificação da persistência no PostgreSQL.

Os testes devem verificar o comportamento real da aplicação. Não considerar cenários como automatizados antes de sua implementação e execução.

## 7. Riscos e mitigação

| Risco | Mitigação | Verificação |
|---|---|---|
| E-mail duplicado | Validação no backend e restrição de unicidade | Teste de duplicidade |
| Senha exposta | BCrypt e DTO de resposta seguro | Inspeção da resposta |
| Perfil indevido | Atribuição controlada no backend | Teste de autorização |
| Dados inválidos | Bean Validation | Testes de validação |
| Falha no banco | Tratamento de exceções | Teste de persistência |
| Exposição de dados pessoais | Retorno somente dos campos necessários | Testes de resposta |

## 8. Critério de conclusão

A funcionalidade será considerada concluída quando o usuário conseguir realizar o cadastro com dados válidos, as regras de segurança forem respeitadas e os cenários de erro estiverem tratados e verificados.