
package com.ottoluis.MaosAmigas.ia;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GroqClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final IaToolService iaToolService;

    private final Map<String, List<Map<String, Object>>> conversas =
            new ConcurrentHashMap<>();

    @Value("${groq.api.url}")
    private String apiUrl;

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.model}")
    private String model;

    public GroqClient(
            WebClient.Builder webClientBuilder,
            ObjectMapper objectMapper,
            IaToolService iaToolService
    ) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
        this.iaToolService = iaToolService;
    }

    public String conversar(String mensagem) {

        if (mensagem == null || mensagem.isBlank()) {
            throw new IllegalArgumentException(
                    "A mensagem não pode estar vazia."
            );
        }

        String identificadorUsuario = obterIdentificadorUsuario();

        if (deveListarPsicologos(mensagem)) {
            return responderListaPsicologos();
        }

        List<Map<String, Object>> messages =
                conversas.computeIfAbsent(
                        identificadorUsuario,
                        key -> criarNovaConversa()
                );

        synchronized (messages) {

            messages.add(Map.of(
                    "role", "user",
                    "content", mensagem
            ));

            try {

                for (int tentativa = 0; tentativa < 5; tentativa++) {

                    Map<String, Object> body = Map.of(
                            "model", model,
                            "messages", messages,
                            "tools", criarFerramentas(),
                            "tool_choice", "auto",
                            "temperature", 0.3,
                            "max_completion_tokens", 1000
                    );

                    Map response = chamarGroq(body);

                    JsonNode json =
                            objectMapper.valueToTree(response);

                    JsonNode choices =
                            json.path("choices");

                    if (!choices.isArray() || choices.isEmpty()) {
                        throw new RuntimeException(
                                "A API do Groq não retornou nenhuma escolha. "
                                        + "Resposta: " + json
                        );
                    }

                    JsonNode message =
                            choices.get(0).path("message");

                    JsonNode toolCalls =
                            message.path("tool_calls");

                    if (!toolCalls.isArray()
                            || toolCalls.isEmpty()) {

                        JsonNode content =
                                message.path("content");

                        if (content.isMissingNode()
                                || content.isNull()) {

                            throw new RuntimeException(
                                    "O Groq não retornou conteúdo. "
                                            + "Resposta: " + json
                            );
                        }

                        String resposta = content.asText();

                        if (resposta.isBlank()) {
                            throw new RuntimeException(
                                    "O Groq retornou uma resposta vazia."
                            );
                        }

                        messages.add(
                                objectMapper.convertValue(
                                        message,
                                        Map.class
                                )
                        );

                        limitarHistorico(messages);

                        return resposta;
                    }

                    Map<String, Object> assistantMessage =
                            objectMapper.convertValue(
                                    message,
                                    Map.class
                            );

                    messages.add(assistantMessage);

                    for (JsonNode toolCall : toolCalls) {

                        String toolCallId =
                                toolCall.path("id").asText();

                        String toolName =
                                toolCall.path("function")
                                        .path("name")
                                        .asText();

                        String arguments =
                                toolCall.path("function")
                                        .path("arguments")
                                        .asText();

                        String resultado =
                                executarFerramenta(
                                        toolName,
                                        arguments
                                );

                        messages.add(Map.of(
                                "role", "tool",
                                "tool_call_id", toolCallId,
                                "name", toolName,
                                "content", resultado
                        ));
                    }
                }

                throw new RuntimeException(
                        "A IA atingiu o limite de chamadas "
                                + "de ferramentas."
                );

            } catch (Exception e) {

                e.printStackTrace();

                throw new RuntimeException(
                        "Erro ao comunicar com a API do Groq: "
                                + e.getMessage(),
                        e
                );
            }
        }
    }

    private List<Map<String, Object>> criarNovaConversa() {

        List<Map<String, Object>> messages = new ArrayList<>();

        messages.add(Map.of(
                "role", "system",
                "content", """
                        Você é o assistente de agendamento do Mãos Amigas.

                        Sua função é ajudar o usuário exclusivamente com
                        agendamento de consultas psicológicas dentro da
                        plataforma.

                        Você possui acesso a ferramentas que consultam os
                        dados reais do sistema.

                        REGRAS:

                        1. Nunca invente psicólogos.
                        2. Nunca invente disponibilidade.
                        3. Nunca invente consultas.
                        4. Para consultar psicólogos, utilize buscarPsicologos.
                        5. Para verificar um horário, utilize verificarDisponibilidade.
                        6. Utilize somente informações retornadas pelas ferramentas.
                        7. Nunca diga que uma consulta foi agendada sem confirmação
                           de sucesso do sistema.
                        8. Não ofereça atendimento psicológico.
                        9. Não faça diagnósticos.
                        10. Não ofereça tratamentos.
                        11. Não responda sobre pagamentos.
                        12. Não responda sobre avaliações de psicólogos.
                        13. Não responda sobre localização.
                        14. Se o assunto estiver fora do agendamento, explique
                            educadamente sua função.
                        15. Seja claro, objetivo e natural.
                        16. Se faltar informação, pergunte ao usuário.
                        17. true significa que o horário está disponível.
                        18. false significa que o horário está ocupado.
                        19. Antes de criar uma consulta, confirme explicitamente
                            com o usuário.
                        20. "Sim", "pode" ou "confirmo" só são confirmação quando
                            existir uma solicitação de agendamento pendente.
                        21. Nunca crie consulta apenas porque o usuário mencionou
                            uma data e horário.
                        22. Depois de verificar disponibilidade, informe o
                            psicólogo, a data e o horário antes de solicitar
                            confirmação.
                        23. Só utilize criarConsulta depois de confirmação explícita.
                        24. Utilize exatamente os dados confirmados pelo usuário.
                        25. Nunca altere data, horário ou psicólogo durante a criação.
                        26. Para consultar horários, utilize listarHorariosDisponiveis.
                        27. Ofereça somente horários retornados pela ferramenta.
                        28. Cada consulta dura 50 minutos, de segunda a sexta,
                            das 08:00 às 18:00.
                        29. Se não houver horários, pergunte se deseja consultar
                            outra data.
                        30. Nunca considere um horário livre sem consultar
                            as ferramentas do sistema.
                        """
        ));

        return messages;
    }

    private boolean deveListarPsicologos(String mensagem) {

        String texto = mensagem.toLowerCase(java.util.Locale.ROOT);

        boolean mencionaProfissionais =
                texto.contains("psicolog")
                        || texto.contains("profissional");

        boolean pedeLista =
                texto.contains("quais")
                        || texto.contains("lista")
                        || texto.contains("listar")
                        || texto.contains("cadastrad")
                        || texto.contains("cadastro")
                        || texto.contains("atualmente")
                        || texto.contains("quantos")
                        || texto.contains("quem")
                        || texto.contains("mostrar")
                        || texto.contains("mostra")
                        || texto.contains("todos")
                        || texto.contains("todas")
                        || texto.contains("não aparece")
                        || texto.contains("nao aparece")
                        || texto.contains("não exibe")
                        || texto.contains("nao exibe")
                        || texto.contains("postman")
                        || texto.contains("adicion");

        return mencionaProfissionais && pedeLista;
    }

    private String responderListaPsicologos() {

        var psicologos = iaToolService.buscarPsicologos();

        if (psicologos.isEmpty()) {
            return "No momento, não há psicólogos cadastrados "
                    + "na plataforma Mãos Amigas.";
        }

        StringBuilder resposta = new StringBuilder();

        resposta.append(
                "Psicólogos cadastrados na plataforma Mãos Amigas: "
        ).append(psicologos.size()).append("\n");

        for (var psicologo : psicologos) {

            resposta.append("\nID: ")
                    .append(psicologo.getId())
                    .append("\nNome: ")
                    .append(psicologo.getNomePsicologa())
                    .append("\nEspecialidade: ")
                    .append(psicologo.getEspecialidade())
                    .append("\n");
        }

        return resposta.toString().trim();
    }

    private String obterIdentificadorUsuario() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null
                || authentication.getName().isBlank()
                || "anonymousUser".equals(authentication.getName())) {

            throw new RuntimeException(
                    "Usuário não autenticado."
            );
        }

        return authentication.getName();
    }

    private Map<String, Object> chamarGroq(
            Map<String, Object> body
    ) {

        return webClient
                .post()
                .uri(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(body)
                .retrieve()
                .onStatus(
                        status -> status.isError(),
                        response -> response
                                .bodyToMono(String.class)
                                .map(errorBody ->
                                        new RuntimeException(
                                                "Erro da API Groq. Status: "
                                                        + response.statusCode()
                                                        + " | Resposta: "
                                                        + errorBody
                                        )
                                )
                )
                .bodyToMono(Map.class)
                .block();
    }

    private List<Map<String, Object>> criarFerramentas() {

        Map<String, Object> buscarPsicologos = Map.of(
                "type", "function",
                "function", Map.of(
                        "name", "buscarPsicologos",
                        "description",
                        "Busca todos os psicólogos cadastrados na plataforma.",
                        "parameters", Map.of(
                                "type", "object",
                                "properties", Map.of(),
                                "required", List.of()
                        )
                )
        );

        Map<String, Object> listarHorariosDisponiveis = Map.of(
                "type", "function",
                "function", Map.of(
                        "name", "listarHorariosDisponiveis",
                        "description",
                        "Lista os horários realmente disponíveis para um psicólogo.",
                        "parameters", Map.of(
                                "type", "object",
                                "properties", Map.of(
                                        "psicologoId", Map.of(
                                                "type", "integer",
                                                "description", "ID do psicólogo."
                                        ),
                                        "data", Map.of(
                                                "type", "string",
                                                "description", "Data YYYY-MM-DD."
                                        )
                                ),
                                "required", List.of("psicologoId", "data")
                        )
                )
        );

        Map<String, Object> verificarDisponibilidade = Map.of(
                "type", "function",
                "function", Map.of(
                        "name", "verificarDisponibilidade",
                        "description",
                        "Verifica a disponibilidade de um psicólogo em uma data e horário.",
                        "parameters", Map.of(
                                "type", "object",
                                "properties", Map.of(
                                        "psicologoId", Map.of(
                                                "type", "integer",
                                                "description", "ID do psicólogo."
                                        ),
                                        "data", Map.of(
                                                "type", "string",
                                                "description", "Data YYYY-MM-DD."
                                        ),
                                        "hora", Map.of(
                                                "type", "string",
                                                "description", "Horário HH:mm:ss."
                                        )
                                ),
                                "required", List.of(
                                        "psicologoId", "data", "hora"
                                )
                        )
                )
        );

        Map<String, Object> criarConsulta = Map.of(
                "type", "function",
                "function", Map.of(
                        "name", "criarConsulta",
                        "description",
                        "Cria uma consulta para o usuário autenticado. "
                                + "Só pode ser chamada após confirmação explícita.",
                        "parameters", Map.of(
                                "type", "object",
                                "properties", Map.of(
                                        "psicologoId", Map.of(
                                                "type", "integer",
                                                "description", "ID do psicólogo."
                                        ),
                                        "data", Map.of(
                                                "type", "string",
                                                "description", "Data YYYY-MM-DD."
                                        ),
                                        "hora", Map.of(
                                                "type", "string",
                                                "description", "Horário HH:mm:ss."
                                        ),
                                        "observacao", Map.of(
                                                "type", "string",
                                                "description", "Observação opcional."
                                        )
                                ),
                                "required", List.of(
                                        "psicologoId", "data", "hora"
                                )
                        )
                )
        );

        return List.of(
                buscarPsicologos,
                listarHorariosDisponiveis,
                verificarDisponibilidade,
                criarConsulta
        );
    }

    private String executarFerramenta(
            String nome,
            String argumentos
    ) {

        try {

            JsonNode args = objectMapper.readTree(argumentos);

            switch (nome) {

                case "buscarPsicologos":

                    return objectMapper.writeValueAsString(
                            iaToolService.buscarPsicologos()
                                    .stream()
                                    .map(psicologo ->
                                            Map.of(
                                                    "id", psicologo.getId(),
                                                    "nome", psicologo.getNomePsicologa(),
                                                    "crp", psicologo.getCrp(),
                                                    "especialidade", psicologo.getEspecialidade(),
                                                    "sobreMim", psicologo.getSobreMim()
                                            )
                                    )
                                    .toList()
                    );

                case "listarHorariosDisponiveis":

                    Long psicologoIdHorarios =
                            args.path("psicologoId").asLong();

                    LocalDate dataHorarios =
                            LocalDate.parse(args.path("data").asText());

                    List<String> horarios =
                            iaToolService.listarHorariosDisponiveis(
                                            psicologoIdHorarios,
                                            dataHorarios
                                    )
                                    .stream()
                                    .map(LocalTime::toString)
                                    .toList();

                    return objectMapper.writeValueAsString(
                            Map.of(
                                    "psicologoId", psicologoIdHorarios,
                                    "data", dataHorarios.toString(),
                                    "duracaoMinutos", 50,
                                    "horariosDisponiveis", horarios
                            )
                    );

                case "verificarDisponibilidade":

                    Long psicologoId =
                            args.path("psicologoId").asLong();

                    LocalDate data =
                            LocalDate.parse(args.path("data").asText());

                    LocalTime hora =
                            LocalTime.parse(args.path("hora").asText());

                    boolean disponivel =
                            iaToolService.verificarDisponibilidade(
                                    psicologoId,
                                    data,
                                    hora
                            );

                    return objectMapper.writeValueAsString(
                            Map.of(
                                    "psicologoId", psicologoId,
                                    "data", data.toString(),
                                    "hora", hora.toString(),
                                    "disponivel", disponivel
                            )
                    );

                case "criarConsulta":

                    Long psicologoIdConsulta =
                            args.path("psicologoId").asLong();

                    LocalDate dataConsulta =
                            LocalDate.parse(args.path("data").asText());

                    LocalTime horaConsulta =
                            LocalTime.parse(args.path("hora").asText());

                    String observacao =
                            args.has("observacao")
                                    && !args.path("observacao").isNull()
                                    ? args.path("observacao").asText()
                                    : null;

                    var consulta = iaToolService.criarConsulta(
                            psicologoIdConsulta,
                            dataConsulta,
                            horaConsulta,
                            observacao
                    );

                    return objectMapper.writeValueAsString(
                            Map.of(
                                    "sucesso", true,
                                    "mensagem", "Consulta criada com sucesso.",
                                    "consultaId", consulta.id(),
                                    "psicologoId", consulta.psicologoId(),
                                    "data", consulta.data().toString(),
                                    "hora", consulta.hora().toString(),
                                    "status", consulta.status().name()
                            )
                    );

                default:

                    return objectMapper.writeValueAsString(
                            Map.of(
                                    "erro",
                                    "Ferramenta não encontrada: " + nome
                            )
                    );
            }

        } catch (Exception e) {

            return objectMapper.writeValueAsString(
                    Map.of(
                            "erro", "Não foi possível executar a ferramenta.",
                            "detalhes", e.getMessage() == null
                                    ? "Erro desconhecido."
                                    : e.getMessage()
                    )
            );
        }
    }

    private void limitarHistorico(
            List<Map<String, Object>> messages
    ) {

        int limite = 30;

        while (messages.size() > limite) {
            messages.remove(1);
        }
    }
}
