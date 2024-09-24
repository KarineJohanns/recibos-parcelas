package com.gerarecibos.recibos.report;

import com.gerarecibos.recibos.DTO.relatorios.TotalRelatorioDTO;
import com.gerarecibos.recibos.model.Parcela;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ParcelaReportGenerator extends RelatorioGenerator {

    private final List<Parcela> parcelas;
    private final TotalRelatorioDTO totais;

    public ParcelaReportGenerator(List<Parcela> parcelas, TotalRelatorioDTO totais) {
        this.parcelas = parcelas;
        this.totais = totais;
    }

    @Override
    protected void addContent(Document document) throws IOException {
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
    }
}
