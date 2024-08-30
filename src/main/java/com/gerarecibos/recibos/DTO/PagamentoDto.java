package com.gerarecibos.recibos.DTO;


import lombok.Data;

import java.time.LocalDate;

@Data
public class PagamentoDto {
    private Double valorPago;
    private LocalDate dataPagamento;
}
