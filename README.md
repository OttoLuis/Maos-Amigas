# Mãos Amigas

<p align="center">
  <img width="500" height="500" alt="a325c96e-bbeb-4c5c-b215-6eaad40cca04" src="https://github.com/user-attachments/assets/7c13447f-0e21-4ddc-8b16-de41ad0fa70f" />
</p>

<p align="center">
  Plataforma de suporte psicológico online que conecta pessoas a profissionais de psicologia e facilita o gerenciamento de consultas.
</p>

---

## Sobre o projeto

O **Mãos Amigas** é um sistema desenvolvido para facilitar o acesso ao suporte psicológico por meio de uma plataforma digital. A aplicação permite organizar o atendimento, consultar profissionais de psicologia e gerenciar agendamentos, oferecendo uma experiência acessível e intuitiva.

O projeto utiliza Java e Spring Boot no backend, com uma API REST responsável pelas regras de negócio, autenticação e gerenciamento dos dados. A aplicação também conta com uma integração com inteligência artificial utilizando a API da Groq, com o objetivo de oferecer um assistente conversacional para auxiliar os usuários durante sua interação com a plataforma.

O frontend está sendo desenvolvido com React, separado do backend, permitindo uma arquitetura desacoplada e facilitando a evolução independente das duas aplicações.

## Objetivos

* Facilitar o acesso a serviços de suporte psicológico online.
* Permitir a consulta de profissionais de psicologia cadastrados.
* Disponibilizar funcionalidades para agendamento e acompanhamento de consultas.
* Implementar autenticação e controle de acesso por meio de JWT.
* Integrar inteligência artificial para proporcionar uma interação mais natural com o sistema.
* Aplicar boas práticas de desenvolvimento, organização de código e separação de responsabilidades.

## Funcionalidades

### Gestão de usuários

* Cadastro de usuários.
* Autenticação por e-mail e senha.
* Proteção de senhas com BCrypt.
* Autenticação baseada em JWT.
* Controle de acesso conforme os papéis definidos na aplicação.

### Gestão de psicólogos

* Cadastro e gerenciamento de profissionais.
* Consulta aos psicólogos disponíveis na plataforma.
* Organização das informações profissionais, incluindo o registro no conselho de psicologia (CRP).

### Agendamento de consultas

* Criação e gerenciamento de consultas.
* Associação entre usuários e psicólogos.
* Definição de data e horário.
* Acompanhamento do status da consulta.
* Armazenamento do link de atendimento online, quando disponível.

### Inteligência artificial com Groq

* Integração com modelos de linguagem disponibilizados pela Groq.
* Assistente conversacional integrado ao sistema.
* Interpretação de mensagens em linguagem natural.
* Apoio à interação com as funcionalidades da plataforma.
* Possibilidade de evoluir a experiência de agendamento por meio de comandos em linguagem natural.

A inteligência artificial funciona como um recurso complementar de atendimento e navegação. Ela não substitui psicólogos, não realiza diagnósticos e não substitui o acompanhamento profissional.

### API REST

* Endpoints organizados por responsabilidade.
* Comunicação por HTTP utilizando JSON.
* Tratamento centralizado de exceções.
* Validação de dados de entrada.
* Documentação da API com OpenAPI/Swagger, conforme a configuração do projeto.

## Tecnologias utilizadas

### Backend

* Java
* Spring Boot
* Spring Web
* Spring Security
* Spring Data JPA
* Hibernate
* JWT
* BCrypt
* Maven

### Inteligência artificial

* Groq API
* Modelos de linguagem (LLMs)

### Frontend

* React
* JavaScript
* Vite
* HTML5
* CSS3
* Axios para comunicação com a API

### Banco de dados e infraestrutura

* PostgreSQL
* Docker
* Docker Compose
* Colima
* Git e GitHub

## Arquitetura

O sistema adota uma arquitetura desacoplada, dividida em três componentes principais:

```text
Mãos Amigas
│
├── Frontend
│   └── React + Vite
│       ├── Interface do usuário
│       ├── Autenticação
│       ├── Consulta de psicólogos
│       ├── Agendamento de consultas
│       └── Assistente de IA
│
├── Backend
│   └── Java + Spring Boot
│       ├── Controllers REST
│       ├── Services
│       ├── Repositories
│       ├── Entities e DTOs
│       ├── Spring Security + JWT
│       └── Integração com Groq
│
└── Banco de dados
    └── PostgreSQL
```

O frontend realiza requisições à API REST. O backend valida as solicitações, executa as regras de negócio e acessa o PostgreSQL por meio da camada de persistência.

Quando necessário, o backend também se comunica com a API da Groq para processar as mensagens destinadas ao assistente de inteligência artificial.

## Organização do backend

O backend segue uma organização por responsabilidades, utilizando os principais componentes do ecossistema Spring:

| Componente          | Responsabilidade                                    |
| ------------------- | --------------------------------------------------- |
| Controller          | Receber e responder às requisições HTTP.            |
| Service             | Implementar as regras de negócio.                   |
| Repository          | Realizar operações de persistência.                 |
| Entity              | Representar as entidades persistidas no banco.      |
| DTO                 | Controlar os dados recebidos e retornados pela API. |
| Security            | Gerenciar autenticação e autorização.               |
| Exception Handler   | Centralizar o tratamento de erros.                  |
| Integração com Groq | Encaminhar solicitações à IA e tratar as respostas. |

## Pré-requisitos

Para executar o ambiente local, você precisará de:

* Java compatível com a versão configurada no projeto.
* Maven ou Maven Wrapper.
* Docker.
* Docker Compose.
* PostgreSQL, caso opte por executar o banco fora do Docker.
* Uma chave de API da Groq para habilitar a integração com inteligência artificial.
* Node.js e npm para executar o frontend React.

## Configuração do ambiente

### 1. Clone o repositório

```bash
git clone https://github.com/OttinLuis/Maos-Amigas.git
cd Maos-Amigas
```

### 2. Configure as variáveis de ambiente

Configure as credenciais do banco de dados, a chave da Groq e os demais parâmetros utilizados pela aplicação.

Exemplo de variáveis que podem ser necessárias, conforme os nomes configurados no projeto:

```env
GROQ_API_KEY=sua_chave_da_groq
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/MaosAmigas
SPRING_DATASOURCE_USERNAME=seu_usuario
SPRING_DATASOURCE_PASSWORD=sua_senha
```

A chave da Groq pode ser obtida no portal oficial: https://console.groq.com/

### 3. Execute com Docker Compose

Com o Docker e o Colima em execução, utilize:

```bash
docker compose up -d --build
```

Para verificar os serviços:

```bash
docker compose ps
```

Para acompanhar os logs:

```bash
docker compose logs -f
```

Para acompanhar apenas os logs da API, caso o serviço esteja configurado com esse nome:

```bash
docker compose logs -f app
```

O nome do serviço deve corresponder ao definido no arquivo `docker-compose.yml`.

Para interromper os serviços:

```bash
docker compose down
```

Esse comando interrompe e remove os containers do projeto, mas normalmente preserva os dados armazenados em volumes nomeados.

### 4. Execute o backend sem Docker

Se preferir executar a API diretamente na máquina:

```bash
./mvnw spring-boot:run
```

O comando pressupõe que o Java e o banco de dados estejam configurados corretamente.

### 5. Execute o frontend React

Entre na pasta do frontend e instale as dependências:

```bash
npm install
npm run dev
```

Configure a URL da API de acordo com o ambiente utilizado. Em um projeto Vite, isso pode ser feito por meio de uma variável como:

```env
VITE_API_URL=http://localhost:8080
```

O frontend deve utilizar essa variável para realizar as requisições ao backend, sem armazenar segredos no código do navegador.

## Segurança

O sistema utiliza mecanismos de segurança para proteger o acesso aos recursos da aplicação:

* Autenticação baseada em JWT.
* Senhas armazenadas com hash BCrypt.
* Controle de acesso por papéis.
* Proteção de endpoints conforme as permissões configuradas.
* Uso de variáveis de ambiente para credenciais e chaves externas.

Como a plataforma lida com informações relacionadas à saúde psicológica, é importante limitar a coleta de dados ao necessário, proteger as informações transmitidas à inteligência artificial e evitar o envio de dados pessoais ou sensíveis à Groq sem uma justificativa adequada e controles de privacidade.

## Documentação da API

A documentação interativa pode ser disponibilizada por meio do Swagger UI, caso o módulo OpenAPI esteja habilitado.

Em uma configuração padrão, o endereço costuma ser:

```text
http://localhost:8080/swagger-ui/index.html
```

A disponibilidade desse endereço depende das dependências e das configurações presentes no projeto.

## Melhorias futuras

* Aprimorar a interface responsiva com React.
* Evoluir o assistente de IA para facilitar a localização de profissionais e a interação com os agendamentos.
* Melhorar o gerenciamento de disponibilidade dos psicólogos.
* Implementar testes automatizados para serviços e controllers.
* Ampliar a documentação técnica.
* Melhorar o monitoramento, os logs e o tratamento de falhas.
* Reforçar os mecanismos de privacidade e proteção de dados.

## Autor

**Otto Luis**

* GitHub: https://github.com/OttinLuis
