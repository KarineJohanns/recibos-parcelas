package com.gerarecibos.recibos.DTO.relatorios;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RelatorioInfoDTO {
    private String nome;        // Nome do relatório
    private String descricao;   // Descrição do relatório
}
