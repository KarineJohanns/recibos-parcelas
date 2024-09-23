package com.gerarecibos.recibos.service;

import com.gerarecibos.recibos.DTO.RelatorioRequestDTO;
import com.gerarecibos.recibos.model.Parcela;
import com.gerarecibos.recibos.repository.RelatorioRepository;
import com.gerarecibos.recibos.DTO.TotalRelatorioDTO;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RelatorioService {

    @Autowired
    private RelatorioRepository relatorioRepository;

    private static final Logger logger = LoggerFactory.getLogger(RelatorioService.class);

    public byte[] gerarRelatorioPdf(RelatorioRequestDTO request) throws IOException {
        logger.info("Recebido: clienteId={}, dataInicio={}, dataFim={}, statusParcela={}",
                request.getClienteId(),
                request.getDataInicio(),
                request.getDataFim(),
                request.getStatusParcela());

        // Definindo datas limite suportadas
        LocalDate dataInicio = request.getDataInicio() != null ? request.getDataInicio() : LocalDate.of(1970, 1, 1);
        LocalDate dataFim = request.getDataFim() != null ? request.getDataFim() : LocalDate.of(2070, 12, 31);
        Boolean statusParcela = request.getStatusParcela();

        List<Parcela> parcelas = relatorioRepository.findByClienteIdAndFilters(
                request.getClienteId(),
                dataInicio,
                dataFim,
                statusParcela
        );

        // Ordenar as parcelas por data de vencimento
        parcelas.sort(Comparator.comparing(Parcela::getDataVencimento));

        // Obtendo os totais
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

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

        // Título do relatório
        document.add(new Paragraph("Relatório de Parcelas")
                .setFontSize(24)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));

        // Tabela de parcelas
        Table parcelasTable = new Table(new float[]{1, 3, 2, 2, 2});
        parcelasTable.setWidth(UnitValue.createPercentValue(100));

        // Cabeçalho
        parcelasTable.addHeaderCell(new Cell().add(new Paragraph("Documento").setBold()));
        parcelasTable.addHeaderCell(new Cell().add(new Paragraph("Cliente").setBold()));
        parcelasTable.addHeaderCell(new Cell().add(new Paragraph("Valor").setBold()));
        parcelasTable.addHeaderCell(new Cell().add(new Paragraph("Data Pagamento").setBold()));
        parcelasTable.addHeaderCell(new Cell().add(new Paragraph("Status").setBold()));

        float fontSize = 10;

        // Adiciona dados à tabela de parcelas
        for (Parcela parcela : parcelas) {
            parcelasTable.addCell(new Cell().add(new Paragraph(String.valueOf(parcela.getDocumento())).setFontSize(fontSize)));
            parcelasTable.addCell(new Cell().add(new Paragraph(parcela.getCliente().getClienteNome()).setFontSize(fontSize)));

            int valorPago = parcela.getValorPago() != null ? parcela.getValorPago() : 0;

            String valorPagoFormatado = (parcela.getValorPago() != null)
                    ? currencyFormat.format(valorPago / 100.0)
                    : currencyFormat.format(parcela.getValorParcela() / 100.0);

            parcelasTable.addCell(new Cell().add(new Paragraph(valorPagoFormatado).setFontSize(fontSize)));

            String dataPagamentoFormatada = (parcela.getDataPagamento() != null)
                    ? parcela.getDataPagamento().format(dateFormatter)
                    : ""; // Adiciona uma célula vazia se a data for nula

            parcelasTable.addCell(new Cell().add(new Paragraph(dataPagamentoFormatada).setFontSize(fontSize)));
            parcelasTable.addCell(new Cell().add(new Paragraph(parcela.isPaga() ? "Pago" : "Pendente").setFontSize(fontSize)));
        }

        // Adiciona a tabela de parcelas no documento
        document.add(parcelasTable);

        // Adiciona um espaço entre a tabela de parcelas e os somatórios
        document.add(new Paragraph("\n\n"));

        // Tabela de somatórios
        Table somatoriosTable = new Table(new float[]{2, 2});
        somatoriosTable.setWidth(UnitValue.createPercentValue(50)); // Ajusta a largura da tabela de somatórios

        // Adiciona os totais à tabela de somatórios
        somatoriosTable.addHeaderCell(new Cell().add(new Paragraph("Descrição").setBold()));
        somatoriosTable.addHeaderCell(new Cell().add(new Paragraph("Valor").setBold()));

        somatoriosTable.addCell(new Cell().add(new Paragraph("Total Parcelas:").setFontSize(fontSize)));
        somatoriosTable.addCell(new Cell().add(new Paragraph(currencyFormat.format(totais.getTotalParcela() / 100.0)).setFontSize(fontSize)));

        somatoriosTable.addCell(new Cell().add(new Paragraph("Total Pago:").setFontSize(fontSize)));
        somatoriosTable.addCell(new Cell().add(new Paragraph(currencyFormat.format(totais.getTotalPago() / 100.0)).setFontSize(fontSize)));

        somatoriosTable.addCell(new Cell().add(new Paragraph("Diferença:").setBold().setFontSize(fontSize)));
        somatoriosTable.addCell(new Cell().add(new Paragraph(currencyFormat.format(totais.getDiferencaParcela() / 100.0)).setFontSize(fontSize)));

        // Adiciona a tabela de somatórios ao documento
        document.add(somatoriosTable);

        document.close();
        return baos.toByteArray();
    }

}
