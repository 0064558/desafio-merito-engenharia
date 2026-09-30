package com.merito.engenharia.posto_de_gasolina_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/*
* Classe de tratamento global para exceções lançadas pela aplicação.
*/

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Tratamento para exceções de validação de argumentos
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidacao(MethodArgumentNotValidException exception) {
        String campos = exception.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .distinct()
                .reduce((primeiro, seguinte) -> primeiro + "; " + seguinte)
                .orElse("Dados inválidos na requisição.");
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, campos);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    // Tratamento para exceções de JSON inválido ou campos não permitidos
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleJsonInvalido(HttpMessageNotReadableException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "JSON inválido ou contém campos não permitidos.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    // Tratamento para exceções de requisição inválida
    @ExceptionHandler(RequisicaoInvalidaException.class)
    public ResponseEntity<ProblemDetail> handleRequisicaoInvalida(
            RequisicaoInvalidaException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                exception.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    // Tratamento para exceções de conflito de negócio
    @ExceptionHandler(ConflitoDeNegocioException.class)
    public ResponseEntity<ProblemDetail> handleConflitoDeNegocio(
            ConflitoDeNegocioException exception) {
        // Cria um objeto ProblemDetail com o status HTTP 409 e a mensagem da exceção
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }

    // Tratamento para exceções de recurso não encontrado
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ProblemDetail> handleRecursoNaoEncontrado(
            RecursoNaoEncontradoException exception) {
        // Cria um objeto ProblemDetail com o status HTTP 404 e a mensagem da exceção
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }
}
