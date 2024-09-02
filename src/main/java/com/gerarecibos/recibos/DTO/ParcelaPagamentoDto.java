package com.gerarecibos.recibos.DTO;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class ParcelaPagamentoDto {
    private Double valorPago;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataVencimento;

    private Boolean gerarNovasParcelas;
    private Double desconto;
}