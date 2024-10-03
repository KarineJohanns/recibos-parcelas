package com.gerarecibos.recibos.DTO.parcelas;

import com.gerarecibos.recibos.model.Parcela;
import lombok.Data;

import java.util.List;

@Data
public class ParcelaResponseDto {
    private Long parcelaId;
    private boolean paga;
    private boolean escolhaNecessaria;
    private String mensagem;
    private List<Parcela> parcelas;
    // Getters e Setters
}