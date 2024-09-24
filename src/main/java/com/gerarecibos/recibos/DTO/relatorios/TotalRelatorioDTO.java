package com.gerarecibos.recibos.DTO.relatorios;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TotalRelatorioDTO {
    private Integer totalPago;       // Total pago
    private Integer totalParcela;    // Total das parcelas não renegociadas
    private Integer diferencaParcela; // Diferença entre o total das parcelas e o total pago
}
