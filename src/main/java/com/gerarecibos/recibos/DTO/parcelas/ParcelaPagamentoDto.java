package com.gerarecibos.recibos.DTO.parcelas;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class ParcelaPagamentoDto {
    private Integer valorPago;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataPagamento;

    private Boolean gerarNovasParcelas;
    private Integer desconto;
}