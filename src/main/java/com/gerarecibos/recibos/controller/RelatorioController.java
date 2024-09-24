package com.gerarecibos.recibos.controller;

import com.gerarecibos.recibos.DTO.relatorios.RelatorioInfoDTO;
import com.gerarecibos.recibos.DTO.relatorios.RelatorioRequestDTO;
import com.gerarecibos.recibos.service.relatorios.RelatorioDeParcelas;
import com.gerarecibos.recibos.service.relatorios.RelatorioDeParcelasPorData;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    @Autowired
    private RelatorioDeParcelas relatorioDeParcelas;
    @Autowired
    private RelatorioDeParcelasPorData relatorioDeParcelasPorData;

    @GetMapping("/lista")
    public List<RelatorioInfoDTO> listarRelatoriosDisponiveis() {
        return List.of(
                new RelatorioInfoDTO("parcelas", "Relatório de parcelas"),
                new RelatorioInfoDTO("parcelas_por_data", "Relatório de parcelas por data")
        );
    }

    // Endpoint unificado para gerar relatórios
    @PostMapping("/parcelas")
    @ResponseStatus(HttpStatus.OK)
    public void gerarRelatorioPDF(@RequestBody RelatorioRequestDTO request, HttpServletResponse response) throws IOException {
        String tipoRelatorio = request.getTipoRelatorio();
        byte[] pdfBytes;

        switch (tipoRelatorio) {
            case "parcelas":
                pdfBytes = relatorioDeParcelas.gerarRelatorioPdf(request);
                response.setHeader("Content-Disposition", "attachment; filename=relatorio_parcelas.pdf");
                break;

            case "parcelas_por_data":
                pdfBytes = relatorioDeParcelasPorData.gerarRelatorioPdf(request);
                response.setHeader("Content-Disposition", "attachment; filename=relatorio_parcelas_por_data.pdf");
                break;

            default:
                throw new IllegalArgumentException("Tipo de relatório desconhecido: " + tipoRelatorio);
        }

        // Enviar o PDF na resposta
        response.setContentType("application/pdf");
        response.getOutputStream().write(pdfBytes);
        response.getOutputStream().flush();
    }
}
