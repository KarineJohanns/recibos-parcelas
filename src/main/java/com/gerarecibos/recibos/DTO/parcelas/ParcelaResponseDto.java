package com.gerarecibos.recibos.DTO.parcelas;

import lombok.Data;

@Data
public class ParcelaResponseDto {
    private Long parcelaId;
    private boolean paga;
    private boolean escolhaNecessaria;
    private String mensagem;
    // Getters e Setters
}