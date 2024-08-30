package com.gerarecibos.recibos.DTO;

import lombok.Data;

@Data
public class ParcelaDto {

    private Long clienteId;
    private Long produtoId;
    private String nomeCliente;
    private String nomeProduto;
    private Double valorTotalProduto; // Valor total do produto
    private Integer numeroParcelas;

    // getters e setters
}