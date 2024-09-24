package com.gerarecibos.recibos.service.relatorios;

import com.gerarecibos.recibos.DTO.relatorios.RelatorioRequestDTO;
import com.gerarecibos.recibos.model.Parcela;
import com.gerarecibos.recibos.repository.RelatorioRepository;
import com.gerarecibos.recibos.DTO.relatorios.TotalRelatorioDTO;
import com.gerarecibos.recibos.report.ParcelaPorDataReportGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
public class RelatorioDeParcelasPorData {

    @Autowired
    private RelatorioRepository relatorioRepository;

    public byte[] gerarRelatorioPdf(RelatorioRequestDTO request) throws IOException {
        LocalDate dataInicio = request.getDataInicio() != null ? request.getDataInicio() : LocalDate.of(1970, 1, 1);
        LocalDate dataFim = request.getDataFim() != null ? request.getDataFim() : LocalDate.of(2070, 12, 31);
        Boolean statusParcela = request.getStatusParcela();

        List<Parcela> parcelas = relatorioRepository.findByClienteIdAndFilters(
                request.getClienteId(),
                dataInicio,
                dataFim,
                statusParcela
        );

        parcelas.sort(Comparator.comparing(Parcela::getDataVencimento));

        List<Object[]> resultados = relatorioRepository.findTotalByClienteIdAndFilters(
                request.getClienteId(),
                dataInicio,
                dataFim,
                statusParcela
        );

        TotalRelatorioDTO totais = new TotalRelatorioDTO();
        if (!resultados.isEmpty()) {
            Object[] resultado = resultados.get(0);
            totais.setTotalPago(resultado[0] != null ? ((Long) resultado[0]).intValue() : 0);
            totais.setTotalParcela(resultado[1] != null ? ((Long) resultado[1]).intValue() : 0);
            totais.setDiferencaParcela(resultado[2] != null ? ((Long) resultado[2]).intValue() : 0);
        }

        ParcelaPorDataReportGenerator reportGenerator = new ParcelaPorDataReportGenerator(parcelas, totais);
        return reportGenerator.gerarRelatorio();
    }
}
