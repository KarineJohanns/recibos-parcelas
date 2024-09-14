package com.gerarecibos.recibos.DTO;

import lombok.Data;

@Data
public class ProdutoDto {
    private String produtoNome;
    private Integer produtoValorTotal;
    private String produtoDescricao; // Novo campo para descrição
}
