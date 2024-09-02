package com.gerarecibos.recibos.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class EscolhaDto {
    private Long parcelaId;
    private Boolean gerarNovasParcelas;
    private Integer numeroParcelasRenegociacao;
    private String novoIntervalo;
    private LocalDate dataPrimeiraParcela;

}