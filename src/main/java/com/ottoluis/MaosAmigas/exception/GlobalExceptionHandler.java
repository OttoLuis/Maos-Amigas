package com.ottoluis.MaosAmigas.exception;

import com.ottoluis.MaosAmigas.exception.ErroResponse;
import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 404 - Recurso não encontrado
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> handleNaoEncontrado(
            RecursoNaoEncontradoException ex,
            HttpServletRequest request) {

        return construirResposta(
                HttpStatus.NOT_FOUND,
                "Não Encontrado",
                ex.getMessage(),
                request,
                null
        );
    }

    // 409 - Recurso duplicado, como e-mail já cadastrado
    @ExceptionHandler(RecursoDuplicadoException.class)
    public ResponseEntity<ErroResponse> handleDuplicado(
            RecursoDuplicadoException ex,
            HttpServletRequest request) {

        return construirResposta(
                HttpStatus.CONFLICT,
                "Conflito",
                ex.getMessage(),
                request,
                null
        );
    }

    // 422 - Regra de negócio violada
    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<ErroResponse> handleRegraDeNegocio(
            RegraDeNegocioException ex,
            HttpServletRequest request) {

        return construirResposta(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Regra de Negócio Violada",
                ex.getMessage(),
                request,
                null
        );
    }

    // 400 - Erros de validação do @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> handleValidacao(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        List<String> detalhes = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(erro -> erro.getField() + ": "
                        + erro.getDefaultMessage())
                .toList();

        return construirResposta(
                HttpStatus.BAD_REQUEST,
                "Erro de Validação",
                "Um ou mais campos estão inválidos",
                request,
                detalhes
        );
    }

    // 409 - Violação de integridade do banco de dados
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponse> handleIntegridade(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {

        log.warn(
                "Violação de integridade na requisição {} {}: {}",
                request.getMethod(),
                request.getRequestURI(),
                ex.getMessage()
        );

        return construirResposta(
                HttpStatus.CONFLICT,
                "Conflito de Dados",
                "A operação viola uma restrição de integridade do banco de dados",
                request,
                null
        );
    }

    // 405 - Método HTTP não suportado
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErroResponse> handleMetodoNaoSuportado(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request) {

        return construirResposta(
                HttpStatus.METHOD_NOT_ALLOWED,
                "Método Não Permitido",
                ex.getMessage(),
                request,
                null
        );
    }

    // 401 - Usuário não autenticado; 403 - Usuário sem permissão
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErroResponse> handleAcessoNegado(
            AuthorizationDeniedException ex,
            HttpServletRequest request) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        boolean anonimo = authentication == null
                || authentication instanceof AnonymousAuthenticationToken;

        HttpStatus status = anonimo
                ? HttpStatus.UNAUTHORIZED
                : HttpStatus.FORBIDDEN;

        return construirResposta(
                status,
                anonimo ? "Não Autenticado" : "Acesso Negado",
                anonimo
                        ? "É necessário fazer login para acessar este recurso."
                        : "Você não tem permissão para acessar este recurso.",
                request,
                null
        );
    }

    // 500 - Exceções inesperadas
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> handleGenerico(
            Exception ex,
            HttpServletRequest request) {

        log.error(
                "Erro inesperado na requisição {} {}",
                request.getMethod(),
                request.getRequestURI(),
                ex
        );

        return construirResposta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro Interno",
                "Ocorreu um erro inesperado. Tente novamente mais tarde.",
                request,
                null
        );
    }

    private ResponseEntity<ErroResponse> construirResposta(
            HttpStatus status,
            String erro,
            String mensagem,
            HttpServletRequest request,
            List<String> detalhes) {

        ErroResponse body = new ErroResponse(
                LocalDateTime.now(),
                status.value(),
                erro,
                mensagem,
                request.getRequestURI(),
                detalhes
        );

        return ResponseEntity
                .status(status)
                .body(body);
    }

    // Preserva os status HTTP lançados pelos serviços
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErroResponse> handleResponseStatus(
            ResponseStatusException ex,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.valueOf(
                ex.getStatusCode().value()
        );

        return construirResposta(
                status,
                status.getReasonPhrase(),
                ex.getReason() != null
                        ? ex.getReason()
                        : "A operação não pôde ser realizada.",
                request,
                null
        );
    }
}

