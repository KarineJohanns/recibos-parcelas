package com.gerarecibos.recibos.Exceptions;

import com.gerarecibos.recibos.DTO.ApiResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ClienteVinculadoException.class)
    public ResponseEntity<String> handleClienteVinculadoException(ClienteVinculadoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO> handleException(Exception e) {
        String message = e.getMessage() != null ? e.getMessage() : "Erro ao processar a solicitação.";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDTO(message, false));
    }
}
