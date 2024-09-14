package com.gerarecibos.recibos.DTO;

import lombok.Data;

@Data
public class ApiResponseDTO {
    private String message;
    private boolean success;

    public ApiResponseDTO(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

}

