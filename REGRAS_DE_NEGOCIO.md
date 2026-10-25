# Regras de Negócio — Mãos Amigas

## 1. Sobre o sistema

O Mãos Amigas é uma plataforma de apoio psicológico online que conecta usuários a psicólogos por meio de consultas agendadas. O sistema permite gerenciar contas, consultar profissionais, realizar agendamentos e utilizar um assistente de inteligência artificial para auxiliar na navegação e no processo de agendamento.

## 2. Cadastro e autenticação

- **RN01:** O usuário deve informar os dados obrigatórios exigidos pelo sistema para realizar o cadastro.
- **RN02:** O e-mail deve ser válido e único no sistema.
- **RN03:** A senha deve ser armazenada de forma segura, utilizando um algoritmo de hash apropriado, como BCrypt.
- **RN04:** O acesso às funcionalidades protegidas deve exigir autenticação.
- **RN05:** Após a autenticação, o sistema deve fornecer um token JWT para autorizar as requisições protegidas.
- **RN06:** O sistema deve validar o token JWT antes de permitir o acesso a recursos protegidos.
- **RN07:** Cada usuário deve possuir permissões compatíveis com seu papel no sistema.
- **RN08:** Um usuário não pode acessar ou modificar dados privados de outros usuários sem autorização.

## 3. Gerenciamento de psicólogos

- **RN09:** O sistema deve permitir consultar os psicólogos cadastrados e disponíveis na plataforma.
- **RN10:** As informações apresentadas sobre os psicólogos devem corresponder aos dados registrados no sistema.
- **RN11:** Somente usuários autorizados podem cadastrar, atualizar ou remover os registros de psicólogos.
- **RN12:** A disponibilidade de um psicólogo deve ser considerada antes da confirmação de um agendamento.
- **RN13:** O sistema não deve oferecer profissionais que não estejam cadastrados.

## 4. Agendamento de consultas

- **RN14:** O usuário deve selecionar um psicólogo e um horário disponível para solicitar uma consulta.
- **RN15:** Cada consulta deve possuir um usuário responsável, um psicólogo, uma data, um horário e um status.
- **RN16:** Cada consulta deve ter duração de 50 minutos.
- **RN17:** Os atendimentos devem respeitar os dias úteis e o horário de funcionamento da plataforma, das 08h às 18h.
- **RN18:** O sistema deve impedir o agendamento em horários incompatíveis com as regras de funcionamento.
- **RN19:** O sistema deve verificar se o psicólogo está disponível antes de confirmar a consulta.
- **RN20:** O sistema não deve permitir que o mesmo psicólogo tenha duas consultas simultâneas.
- **RN21:** A disponibilidade deve considerar as consultas já registradas e seus respectivos status.
- **RN22:** A confirmação de um agendamento deve ocorrer somente após a validação dos dados e da disponibilidade.
- **RN23:** O sistema deve impedir a criação de consultas com informações obrigatórias ausentes ou inválidas.
- **RN24:** Cada consulta deve possuir um identificador único.
- **RN25:** O sistema deve disponibilizar o link da reunião online quando ele estiver associado à consulta.

## 5. Status das consultas

- **RN26:** Toda consulta deve possuir um status que represente sua situação atual.
- **RN27:** Uma consulta pode ser registrada com o status `AGENDADA`.
- **RN28:** Uma consulta pode passar para o status `CONFIRMADA` quando houver confirmação.
- **RN29:** As mudanças de status devem respeitar as operações permitidas pelo sistema.
- **RN30:** Consultas com status que ocupem um horário devem ser consideradas no cálculo de disponibilidade.
- **RN31:** Consultas canceladas, caso essa funcionalidade esteja implementada, não devem continuar bloqueando o horário.
- **RN32:** Somente usuários autorizados podem realizar operações que alterem o status de uma consulta.

## 6. Privacidade e autorização

- **RN33:** O usuário autenticado deve poder consultar suas próprias consultas.
- **RN34:** O acesso aos dados das consultas deve respeitar as permissões do usuário autenticado.
- **RN35:** O psicólogo deve acessar somente as informações necessárias para gerenciar os atendimentos sob sua responsabilidade, conforme suas permissões.
- **RN36:** O administrador pode executar operações administrativas autorizadas pelo sistema.
- **RN37:** Informações pessoais e dados relacionados aos atendimentos devem ser protegidos contra acessos não autorizados.
- **RN38:** O tratamento de dados pessoais deve respeitar a legislação aplicável, especialmente a Lei Geral de Proteção de Dados (LGPD).

## 7. Inteligência artificial — Groq

- **RN39:** O assistente de IA deve auxiliar o usuário na navegação pela plataforma e no processo de agendamento.
- **RN40:** A IA deve utilizar os dados reais disponibilizados pelo backend para consultar psicólogos, horários e agendamentos.
- **RN41:** A IA não deve inventar profissionais, horários disponíveis ou consultas existentes.
- **RN42:** A IA deve consultar a disponibilidade real antes de informar que determinado horário está disponível.
- **RN43:** A IA pode orientar o usuário na escolha de um psicólogo e na seleção de data e horário.
- **RN44:** A IA não deve confirmar ou criar um agendamento sem a autorização explícita do usuário.
- **RN45:** A criação de uma consulta deve respeitar as mesmas validações e regras aplicadas aos agendamentos realizados diretamente pela plataforma.
- **RN46:** A IA deve utilizar os mecanismos de autenticação e autorização aplicáveis às operações solicitadas.
- **RN47:** A IA não deve realizar diagnósticos psicológicos, prescrever tratamentos ou substituir o acompanhamento de um profissional.
- **RN48:** A atuação da IA deve permanecer restrita às funcionalidades relacionadas à plataforma e ao agendamento de consultas.
- **RN49:** A IA deve informar quando não houver dados suficientes para responder com segurança ou precisão.

## 8. Validação e integridade dos dados

- **RN50:** O backend deve validar os dados recebidos nas requisições antes de executar operações.
- **RN51:** O sistema deve impedir a persistência de registros que violem as restrições de integridade do banco de dados.
- **RN52:** As operações devem retornar respostas apropriadas quando houver dados inválidos, ausência de recursos ou falta de autorização.
- **RN53:** Os erros devem ser tratados de maneira consistente, sem expor senhas, tokens ou detalhes internos sensíveis.
- **RN54:** As operações de criação e atualização devem manter a consistência entre usuários, psicólogos e consultas.

## 9. Considerações finais

As regras de negócio devem ser aplicadas no backend para garantir a integridade dos dados e impedir que sejam contornadas por meio de requisições diretas à API ou pelo assistente de inteligência artificial.

As regras relacionadas a cancelamentos, permissões específicas e transições de status devem corresponder às funcionalidades efetivamente implementadas no sistema. Este documento deve ser atualizado sempre que houver mudanças nos requisitos ou nas regras operacionais do Mãos Amigas.
