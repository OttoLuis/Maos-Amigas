# Especificação de funcionalidade: [Agendamento de consulta]

**ID:** SPEC-[001]  
**Status:** Pendente Validação com o PO
**Responsáveis:** [Otto Luís, Luiz Felipe, Victor Raphael, Rafael Loureiro, Wellington Perovano]  
**Última atualização:** [08/10/2026]

## 1. Problema e evidências

**Usuário prioritário:** [Pessoas que buscam atendimento psicológico e profissionais de psicologia que oferecem atendimento online.]  
**Situação:** [Ocorre quando o usuário necessita de suporte psicológico e precisa encontrar um profissional disponível, consultar informações sobre o atendimento e realizar o agendamento de uma consulta de forma acessível e organizada.]  
**Problema:** [Existe dificuldade em centralizar a busca por profissionais, a consulta de disponibilidade e o agendamento de atendimentos psicológicos em um único fluxo.]  
**Alternativa atual:** [O usuário pode recorrer a pesquisas em diferentes plataformas, redes sociais, contatos diretos com profissionais ou serviços especializados, realizando etapas de busca e agendamento de maneira descentralizada.]  
**Impacto:** [A dificuldade de encontrar um profissional adequado e realizar o agendamento pode tornar o acesso ao atendimento mais demorado, dificultando a conexão entre usuários e psicólogos.]  
**Evidências disponíveis:** [Observação do processo de busca e agendamento de serviços de atendimento psicológico online, análise das necessidades dos usuários e levantamento dos requisitos funcionais do sistema.]  
**Suposições ainda não confirmadas:** [A centralização das informações dos profissionais e do processo de agendamento pode reduzir o tempo e a dificuldade necessários para encontrar e marcar uma consulta.]

## 2. Objetivo e resultado esperado

**Objetivo:** [Facilitar o acesso a profissionais de psicologia e tornar o processo de busca e agendamento de consultas psicológicas online mais simples, organizado e acessível.]  
**Hipótese:** [Acreditamos que disponibilizar, em um único ambiente, informações sobre profissionais e recursos para agendamento ajudará os usuários a encontrar atendimento psicológico com maior facilidade e concluir o processo de marcação de consultas de forma mais eficiente.] 

**Linha de base:** [Atualmente, o usuário pode precisar pesquisar profissionais em diferentes canais, verificar disponibilidade por meios distintos e realizar o agendamento diretamente com o profissional ou por plataformas separadas.]  
**Sinais de sucesso:** [O usuário consegue localizar um profissional, consultar suas informações e realizar o agendamento de uma consulta mais rapidamente seguindo um fluxo simples, sem depender de múltiplos canais para concluir essas etapas.]

## 3. História prioritária

Como usuário que busca atendimento psicológico, quero encontrar profissionais disponíveis e agendar uma consulta online, para conseguir acesso ao atendimento psicológico de forma simples e organizada.

**Prioridade:** P1  
**Teste independente:** [O valor da história pode ser demonstrado quando um usuário consegue, de forma independente, consultar os profissionais disponíveis e concluir o agendamento de uma consulta, mesmo que funcionalidades complementares do sistema ainda não estejam implementadas.]

## 4. Escopo

### Incluído
 
- Cadastro e autenticação de usuários para acesso às funcionalidades do sistema.
- Consulta das informações dos profissionais de psicologia disponíveis na plataforma.
- Visualização dos dados relevantes dos psicólogos, incluindo informações profissionais e registro no CRP.
- Agendamento de consultas psicológicas conforme a disponibilidade cadastrada.
- Registro e gerenciamento das consultas realizadas pelo usuário.
- Definição e acompanhamento do status das consultas, como AGENDADA e CONFIRMADA.
- Disponibilização do link da consulta online quando houver um atendimento agendado.
- Gerenciamento dos profissionais e consultas por usuários com permissões administrativas.
- Validação dos dados enviados pelo usuário para evitar registros inválidos.
- Tratamento de erros durante as operações, informando ao usuário quando uma ação não puder ser concluída.
- Atendimento voltado ao contexto de suporte psicológico online, com foco na conexão entre usuários e profissionais de psicologia.

### Fora do escopo

- Realização da consulta psicológica diretamente pelo sistema.
- Substituição do profissional de psicologia por recursos automatizados ou inteligência artificial.
- Diagnóstico, prescrição ou tratamento psicológico automatizado.
- Atendimento presencial entre usuário e psicólogo.
- Processamento ou gerenciamento de pagamentos e cobranças pelas consultas.
- Integração com planos de saúde ou convênios.
- Disponibilização de serviços médicos ou de outras especialidades da área da saúde.
- Funcionalidades de emergência ou atendimento psicológico para situações de risco imediato.
- Gerenciamento de prontuários clínicos completos dos pacientes.
- Funcionalidades destinadas a usuários ou situações que estejam fora do propósito de conectar pessoas a profissionais de psicologia para atendimento online.


## 5. Entradas, saídas e fluxo

### Entradas

- Dados de cadastro do usuário:
    - Nome
    - E-mail
    - Senha
    - Data de nascimento
    - Endereço
    - Contato
    - Contato de confiança
- Dados dos psicólogos:
    - Nome
    - CRP
    - Especialidade
    - Contato
    - Rede Social
    - Informações profissionais
- Dados das consultas:
    - Usuário
    - Psicólogo
    - Data
    - Horário
    - Status
    - Observação
    - Link de atendimento
- Credenciais de autenticação:
    - E-mail
    - Senha
    - Token JWT
- Requisições realizadas pela aplicação web ou por clientes da API.

### Saídas

- Cadastro e autenticação de usuários.
- Listagem de psicólogos disponíveis.
- Consulta de informações dos psicólogos.
- Agendamento de consultas.
- Atualização do status das consultas.
- Informações sobre consultas agendadas.
- Geração e validação de token JWT.
- Respostas HTTP em formato JSON.
- Link para atendimento psicológico online.
- Mensagens de erro e validação quando uma operação não pode ser realizada.

### Precondições

- A aplicação deve estar em execução.
- O banco de dados PostgreSQL deve estar disponível.
- O usuário deve estar autenticado para acessar funcionalidades protegidas.
- Os dados enviados devem respeitar as regras de validação da aplicação.
- Para realizar uma consulta, deve existir um usuário e um psicólogo válidos.
- Para realizar um agendamento, os dados de data e horário devem ser válidos.

---

## Fluxo principal

1. **Usuário acessa a aplicação**
    - O sistema disponibiliza as funcionalidades de cadastro, login, consulta de psicólogos e gerenciamento de consultas.

2. **Usuário realiza o cadastro**
    - O sistema recebe os dados pessoais e credenciais.
    - Os dados são validados.
    - A senha é armazenada de forma segura utilizando criptografia/hash.
    - O usuário é registrado no banco de dados.

3. **Usuário realiza o login**
    - O sistema recebe e-mail e senha.
    - As credenciais são verificadas.
    - Caso sejam válidas, o sistema gera um token JWT.
    - O token é utilizado para autenticar as próximas requisições.

4. **Usuário consulta os psicólogos**
    - O sistema recebe a requisição autenticada.
    - Busca os psicólogos cadastrados.
    - Retorna as informações disponíveis em formato JSON.

5. **Usuário seleciona um psicólogo**
    - O sistema recebe a identificação do psicólogo.
    - Verifica se o psicólogo existe.
    - Disponibiliza as informações necessárias para a realização do agendamento.

6. **Usuário agenda uma consulta**
    - O sistema recebe o psicólogo, data, horário e demais informações da consulta.
    - Valida os dados enviados.
    - Registra a consulta no banco de dados.
    - A consulta é criada com seu respectivo status.
    - O sistema retorna os dados da consulta criada.

7. **Usuário consulta seus agendamentos**
    - O sistema identifica o usuário autenticado.
    - Busca suas consultas cadastradas.
    - Retorna as consultas e seus respectivos dados.

8. **Usuário acessa o atendimento**
    - Quando disponível, o sistema fornece o link de atendimento associado à consulta.
    - O usuário pode utilizar o link para realizar o atendimento psicológico online.

---

## Alternativas e erros

### Ausência de informação

- Caso um campo obrigatório não seja informado, o sistema rejeita a requisição.
- O sistema retorna uma mensagem indicando os dados necessários para concluir a operação.

### Entrada inválida

- Caso os dados enviados não estejam de acordo com as regras de validação, a operação não é realizada.
- O sistema retorna uma resposta de erro informando a inconsistência encontrada.

### Usuário não autenticado

- Caso o usuário tente acessar um recurso protegido sem autenticação válida, o sistema bloqueia a requisição.
- É retornado o status HTTP `401 Unauthorized`.

### Usuário sem permissão

- Caso o usuário esteja autenticado, mas não possua a permissão necessária para acessar determinado recurso, o sistema bloqueia a operação.
- É retornado o status HTTP `403 Forbidden`.

### Recurso inexistente

- Caso o usuário solicite um psicólogo, usuário ou consulta que não exista, o sistema informa que o recurso não foi encontrado.
- É retornado o status HTTP `404 Not Found`.

### E-mail já cadastrado

- Caso o usuário tente realizar um cadastro utilizando um e-mail já existente, o sistema rejeita a operação.
- Uma mensagem informa que o e-mail já está cadastrado.

### Credenciais inválidas

- Caso o e-mail ou senha informados no login estejam incorretos, o sistema não gera o token JWT.
- A autenticação é recusada.

### Serviço indisponível

- Caso o banco de dados ou outro componente necessário esteja indisponível, a operação não é concluída.
- O sistema retorna uma resposta de erro apropriada e registra o problema para análise.

### Consulta inválida

- Caso os dados da consulta sejam inválidos ou estejam incompletos, o agendamento não é realizado.
- O sistema informa quais dados precisam ser corrigidos.

### Limite de segurança

- Endpoints protegidos exigem autenticação por JWT.
- As permissões de acesso são controladas de acordo com o perfil do usuário.
- Senhas não são armazenadas em texto puro.
- Dados inválidos ou requisições não autorizadas são rejeitados antes de executar operações protegidas.

### Fluxo principal

1. [ação ou evento]
2. [comportamento do sistema]
3. [resultado entregue ao usuário]

### Alternativas e erros

- [ausência de informação]
- [entrada inválida]
- [serviço indisponível]
- [limite de segurança]

## 6. Requisitos Funcionais

**RF-001:** O sistema deverá permitir que o usuário realize seu cadastro informando seus dados pessoais e credenciais de acesso.

**RF-002:** Quando o usuário informar credenciais válidas, o sistema deverá realizar sua autenticação e permitir o acesso às funcionalidades correspondentes ao seu perfil.

**RF-003:** Quando o usuário informar credenciais inválidas, o sistema deverá rejeitar a autenticação e informar que os dados de acesso são inválidos.

**RF-004:** O sistema deverá permitir que usuários autenticados consultem os psicólogos disponíveis para atendimento.

**RF-005:** O sistema deverá permitir que o usuário consulte as informações dos psicólogos cadastrados, incluindo dados profissionais e registro no CRP.

**RF-006:** Quando o usuário selecionar um psicólogo, o sistema deverá permitir a consulta das datas e horários disponíveis para atendimento.

**RF-007:** O sistema deverá permitir que o usuário agende uma consulta com um psicólogo em uma data e horário disponíveis.

**RF-008:** Quando uma consulta for agendada, o sistema deverá registrar o usuário, o psicólogo, a data, o horário e o status da consulta.

**RF-009:** O sistema deverá atribuir o status **AGENDADA** às novas consultas.

**RF-010:** O sistema deverá permitir que o usuário consulte suas consultas agendadas e seus respectivos status.

**RF-011:** Quando uma consulta possuir um link de atendimento, o sistema deverá disponibilizar o link ao usuário autorizado.

**RF-012:** O sistema deverá permitir o registro de informações adicionais relacionadas à consulta, quando aplicável.

**RF-013:** O sistema deverá permitir que usuários autorizados alterem o status de uma consulta conforme as regras definidas pelo sistema.

**RF-014:** O sistema deverá impedir o agendamento de uma consulta em horário que não esteja disponível para o psicólogo.

**RF-015:** O sistema deverá impedir o cadastro de mais de um psicólogo utilizando o mesmo número de CRP.

**RF-016:** O sistema deverá impedir o cadastro de mais de um usuário utilizando o mesmo endereço de e-mail.

**RF-017:** O sistema deverá permitir que usuários com perfil administrativo gerenciem os dados dos psicólogos cadastrados.

**RF-018:** O sistema deverá permitir que usuários com perfil administrativo consultem e gerenciem as consultas registradas no sistema.

**RF-019:** Quando uma requisição exigir autenticação, o sistema deverá verificar as credenciais e permissões do usuário antes de permitir o acesso ao recurso.

**RF-020:** Quando um usuário autenticado tentar acessar um recurso sem a permissão necessária, o sistema deverá negar o acesso ao recurso.

**RF-021:** O sistema deverá disponibilizar uma API REST para comunicação entre o sistema e seus clientes.

**RF-022:** O sistema deverá utilizar JSON para representar os dados enviados e recebidos pela API.

**RF-023:** O sistema deverá validar os dados recebidos nas operações de cadastro, atualização e agendamento antes de persistí-los no banco de dados.

**RF-024:** Quando ocorrer uma operação inválida, o sistema deverá retornar uma resposta HTTP compatível com o tipo de erro ocorrido.

---

## 7. Critérios de Aceitação

### CA-001 — Cadastro de usuário

**Dado** que o usuário esteja na tela de cadastro  
**Quando** informar todos os dados obrigatórios válidos e confirmar o cadastro  
**Então** o sistema deverá criar o usuário e permitir sua autenticação.

### CA-002 — Cadastro com e-mail existente

**Dado** que já exista um usuário cadastrado com determinado e-mail  
**Quando** outro usuário tentar utilizar o mesmo e-mail  
**Então** o sistema deverá rejeitar o cadastro e informar que o e-mail já está cadastrado.

### CA-003 — Autenticação válida

**Dado** que o usuário possua uma conta cadastrada  
**Quando** informar e-mail e senha válidos  
**Então** o sistema deverá autenticar o usuário e permitir o acesso aos recursos autorizados.

### CA-004 — Autenticação inválida

**Dado** que o usuário esteja na tela de login  
**Quando** informar credenciais inválidas  
**Então** o sistema deverá rejeitar a autenticação e impedir o acesso aos recursos protegidos.

### CA-005 — Consulta de psicólogos

**Dado** que o usuário esteja autenticado  
**Quando** solicitar a lista de psicólogos  
**Então** o sistema deverá retornar os psicólogos cadastrados e suas informações profissionais.

### CA-006 — Visualização de psicólogo

**Dado** que exista um psicólogo cadastrado  
**Quando** o usuário solicitar suas informações  
**Então** o sistema deverá apresentar os dados profissionais disponíveis, incluindo o CRP.

### CA-007 — Consulta de disponibilidade

**Dado** que o usuário tenha selecionado um psicólogo  
**Quando** solicitar seus horários disponíveis  
**Então** o sistema deverá apresentar as datas e horários disponíveis para agendamento.

### CA-008 — Agendamento de consulta

**Dado** que exista um horário disponível para determinado psicólogo  
**Quando** o usuário selecionar a data e o horário e confirmar o agendamento  
**Então** o sistema deverá registrar a consulta e atribuir o status **AGENDADA**.

### CA-009 — Agendamento em horário indisponível

**Dado** que determinado horário esteja ocupado ou indisponível  
**Quando** o usuário tentar realizar um agendamento nesse horário  
**Então** o sistema deverá rejeitar a operação e informar que o horário não está disponível.

### CA-010 — Consulta das consultas do usuário

**Dado** que o usuário possua consultas registradas  
**Quando** solicitar suas consultas  
**Então** o sistema deverá apresentar as consultas vinculadas ao usuário, incluindo data, horário, psicólogo e status.

### CA-011 — Acesso ao atendimento online

**Dado** que uma consulta possua um link de atendimento  
**Quando** o usuário autorizado acessar os dados da consulta  
**Então** o sistema deverá disponibilizar o link correspondente.

### CA-012 — CRP duplicado

**Dado** que já exista um psicólogo cadastrado com determinado CRP  
**Quando** um usuário autorizado tentar cadastrar outro psicólogo com o mesmo CRP  
**Então** o sistema deverá rejeitar o cadastro.

### CA-013 — Acesso administrativo

**Dado** que o usuário possua perfil administrativo  
**Quando** acessar uma funcionalidade exclusiva de administração  
**Então** o sistema deverá permitir o acesso conforme suas permissões.

### CA-014 — Acesso não autorizado

**Dado** que o usuário esteja autenticado, mas não possua a permissão necessária  
**Quando** tentar acessar uma funcionalidade restrita  
**Então** o sistema deverá negar o acesso.

### CA-015 — Validação de dados

**Dado** que o usuário esteja realizando uma operação de cadastro ou agendamento  
**Quando** enviar dados obrigatórios inválidos ou incompletos  
**Então** o sistema deverá rejeitar a operação e informar os dados que precisam ser corrigidos.

### CA-016 — Persistência da consulta

**Dado** que uma consulta tenha sido agendada com sucesso  
**Quando** o sistema concluir o processamento  
**Então** os dados da consulta deverão ser armazenados no banco de dados e permanecer disponíveis para consulta.

## 8. Qualidade, riscos e decisões
**Requisitos de qualidade:** 
- Interface utilizável através de teclado e mouse
- Manter fontes visíveis nas buscas auxiliares da IA
- O sistema deve estar em conformidade com a LGPD
- O sistema deverá utilizar controle de acesso baseado em papéis (roles), garantindo que cada usuário possa executar apenas as operações permitidas para seu perfil
- O sistema deverá armazenar as senhas dos usuários de forma segura, utilizando algoritmo de hash apropriado, não permitindo o armazenamento de senhas em texto puro
- O sistema deverá retornar respostas padronizadas e adequadas para situações de erro, evitando a exposição de informações internas da aplicação
- A API deverá possuir documentação das principais operações, endpoints, parâmetros, respostas e requisitos de autenticação, facilitando sua utilização e manutenção
- O código-fonte e as alterações estruturais do sistema deverão ser controlados por versionamento, permitindo acompanhar alterações e recuperar versões anteriores quando necessário
- O sistema deverá utilizar migrações versionadas para controlar a evolução do banco de dados, permitindo que a estrutura necessária seja reproduzida de forma consistente em diferentes ambientes

**Riscos principais:** [Uma busca com filtragem indicar uma resposta plausível mas que fuja de algum dos filtros, levando o paciente a agendar uma consulta com um psicólogo que não se adequa as necessidades dele, um paciente agendar uma consulta mas o sistema falhar nesse agendamento e ainda assim mostrar um resultado de sucesso no agendamento]  

**Mitigações:** [Escopo restrito, resposta baseadas unicamente em fontes, a consulta só mudará o status do agendamento caso o paciente confirme de certeza, a busca só retornará resultados após validar os filtros]  

**Questões para o PO:** [Caso haja conflito nos arquivos que informam os psicólogos/clinicas psicológicas cadastradas no sistema, qual arquivo deve prevalecer como oficial? Caso ocorra de o paciente confirmar um agendamento e o sistema não registrar esse agendamento, como devemos prosseguir com esse paciente?]  

**Decisões confirmadas:** [decisão, data e responsável]                 

**Testes relacionados:** [T-001 a T-016 correspondem aos critérios CA-001 a CA-016.]
