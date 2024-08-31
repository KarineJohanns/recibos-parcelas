package com.gerarecibos.recibos.controller;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
public class PagamentoDto {
    private Double valorPago;
    private LocalDate dataPagamento;
}
