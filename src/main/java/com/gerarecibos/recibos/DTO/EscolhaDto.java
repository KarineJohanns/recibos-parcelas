package com.gerarecibos.recibos.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EscolhaDto {
    private Boolean gerarNovasParcelas;
    private Integer numeroParcelasRenegociacao;

}