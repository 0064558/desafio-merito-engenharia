package com.merito.engenharia.posto_de_gasolina_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/*
* Classe de tratamento global para exceções lançadas pela aplicação.
*/

@RestControllerAdvice
public class GlobalExceptionHandler {

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
