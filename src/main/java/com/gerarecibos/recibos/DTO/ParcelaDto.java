package com.gerarecibos.recibos.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ParcelaDto {

    private Long clienteId;
    private Long produtoId;
    private String nomeCliente;
    private String nomeProduto;
    private Integer valorTotalProduto; // Valor total do produto
    private Integer ValorParcela;
    private Integer numeroParcelas;
    private Long emitenteId;
    private String intervalo; // "MENSAL", "SEMANAL", etc.
    private LocalDate dataCriacao; // Data completa
    private LocalDate dataVencimento;
    private String documento;

    // getters e setters
}