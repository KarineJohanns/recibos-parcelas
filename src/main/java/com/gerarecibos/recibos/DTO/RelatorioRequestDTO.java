package com.gerarecibos.recibos.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDate;

@Data
public class RelatorioRequestDTO {
    private String tipoRelatorio; // "parcelas_por_cliente", etc.
    private Long clienteId; // ID do cliente (obrigatório)

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataInicio; // Data de início no formato "yyyy-MM-dd"

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataFim; // Data de fim no formato "yyyy-MM-dd"

    private Boolean statusParcela; // Status da parcela (opcional)
}