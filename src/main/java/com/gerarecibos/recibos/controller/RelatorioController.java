package com.gerarecibos.recibos.controller;

import com.gerarecibos.recibos.DTO.RelatorioRequestDTO;
import com.gerarecibos.recibos.service.RelatorioService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    @Autowired
    private RelatorioService relatorioService;

    @PostMapping("/parcelas/pdf")
    @ResponseStatus(HttpStatus.OK)
    public void gerarRelatorioParcelasClientePDF(@RequestBody RelatorioRequestDTO request, HttpServletResponse response) throws IOException {
        byte[] pdfBytes = relatorioService.gerarRelatorioPdf(request);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=relatorio_parcelas.pdf");
        response.getOutputStream().write(pdfBytes);
        response.getOutputStream().flush();
    }

    @PostMapping("/parcelas/xls")
    @ResponseStatus(HttpStatus.OK)
    public void gerarRelatorioParcelasClienteXLS(@RequestBody RelatorioRequestDTO request, HttpServletResponse response) {
        // Implementação para gerar o relatório em XLS (se necessário)
    }
}
