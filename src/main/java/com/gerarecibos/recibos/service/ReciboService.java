package com.gerarecibos.recibos.service;

import com.gerarecibos.recibos.Utils.NumeroPorExtenso;
import com.gerarecibos.recibos.model.Recibo;
import com.gerarecibos.recibos.repository.ReciboRepository;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.layout.element.Text;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class ReciboService {

    @Autowired
    private ReciboRepository reciboRepository;

    public byte[] gerarReciboPdf(Long parcelaId) throws IOException {
        // Obtenha o recibo do banco de dados
        Recibo recibo = reciboRepository.findByParcelaId(parcelaId)
                .orElseThrow(() -> new RuntimeException("Recibo não encontrado para a parcela."));

        // Crie um documento PDF
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy");
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

        // Obtenha a data de pagamento da parcela
        LocalDate dataPagamento = recibo.getParcela().getDataPagamento();
        String dataPagamentoFormatada = dataPagamento.format(dateFormatter);
        LocalDate dataVencimento = recibo.getParcela().getDataVencimento();
        String dataVencimentoFormatada = dataVencimento.format(dateFormatter);

        // Obtenha o valor pago
        double valorPago = recibo.getParcela().getValorPago() / 100.0;
        String valorPagoFormatado = currencyFormat.format(valorPago);
        String valorPorExtenso = NumeroPorExtenso.valorPorExtenso(valorPago);

        // Crie uma tabela com 2 colunas
        Table table = new Table(2);
        table.setWidth(UnitValue.createPercentValue(100)); // Largura da tabela em 100% da largura da página
        table.setBorder(new SolidBorder(1)); // Borda externa da tabela
        table.setMarginBottom(5);

        // Adicione o título na primeira coluna
        Cell titleCell = new Cell()
                .add(new Paragraph("RECIBO").setFontSize(26).setBold())
                .setTextAlignment(TextAlignment.LEFT)
                .setPadding(10)
                .setBorder(Border.NO_BORDER); // Remove as bordas da célula
        table.addCell(titleCell);

        // Adicione o número do recibo e o valor na segunda coluna
        Cell receiptInfoCell = new Cell()
                .add(new Paragraph()
                        .add(new Text("Nº: ").setBold()) // Texto "Nº" em negrito
                        .add(new Text(recibo.getParcela().getNumeroParcela() + " de " + recibo.getParcela().getNumeroParcelas()).setFontSize(16)) // Texto normal
                        .setFontSize(16)
                        .setTextAlignment(TextAlignment.LEFT))
                .add(new Paragraph()
                        .add(new Text("VALOR: ").setBold()) // Texto "VALOR" em negrito
                        .add(new Text(valorPagoFormatado).setFontSize(16)) // Texto normal
                        .setFontSize(16)
                        .setTextAlignment(TextAlignment.LEFT))
                .setPadding(10)
                .setBorder(Border.NO_BORDER); // Remove as bordas da célula
        table.addCell(receiptInfoCell);

        // Crie uma nova tabela com uma única coluna para o corpo do recibo
        Table declarationTable = new Table(1);
        declarationTable.setWidth(UnitValue.createPercentValue(100));
        declarationTable.setBorder(new SolidBorder(1));

        // Adicionar o texto formatado com o valor por extenso
        Cell declarationCell = new Cell().setBorder(Border.NO_BORDER);
        declarationCell.add(new Paragraph()
                .add(new Text("Eu, ").setFontSize(12))
                .add(new Text(recibo.getEmitente().getEmitenteNome().toUpperCase()).setBold().setFontSize(12))
                .add(new Text(", Portador (a) do CPF ").setFontSize(12))
                .add(new Text(recibo.getEmitente().getEmitenteCpf()).setBold().setFontSize(12))
                .add(new Text(", Declaro ter recebido nesta data a quantia de: ").setFontSize(12))
                .add(new Text(valorPagoFormatado + " (" + valorPorExtenso + ")").setBold().setFontSize(12)) // Inclui o valor por extenso
                .add(new Text(" de ").setFontSize(12))
                .add(new Text(recibo.getCliente().getClienteNome().toUpperCase()).setBold().setFontSize(12))
                .add(new Text(", portador do CPF nº ").setFontSize(12))
                .add(new Text(recibo.getCliente().getClienteCpf()).setFontSize(12))
                .add(new Text(", REFERENTE AO PAGAMENTO DO DOCUMENTO ").setBold().setFontSize(12))
                .add(new Text(recibo.getParcela().getDocumento()).setBold().setFontSize(12))
                .add(new Text(", com vencimento para dia ").setFontSize(12))
                .add(new Text(dataVencimentoFormatada))
                .add(new Text(" da compra de "))
                .add(new Text(recibo.getParcela().getProduto().getProdutoNome()).setBold().setFontSize(12))
                .add(new Text(".").setFontSize(12))
                .setPadding(10)
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setMultipliedLeading(1.5f)
        );

// Adicionar a célula à tabela
        declarationTable.addCell(declarationCell);

        declarationTable.addCell(new Cell()
                .add(new Paragraph("E para maior clareza, afirmo o presente.")
                        .setTextAlignment(TextAlignment.LEFT)
                        .setFontSize(12))
                .setBorder(Border.NO_BORDER)
                .setPadding(10)
        );

// Adicionar o Local e a Data por extenso centralizados
        declarationTable.addCell(new Cell()
                .add(new Paragraph("Toledo, " + dataPagamentoFormatada)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontSize(12))
                .setBorder(Border.NO_BORDER)
                .setPadding(10)
        );

// Linha para assinar centralizado
        declarationTable.addCell(new Cell()
                .add(new Paragraph("____________________________________")
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontSize(12))
                .setBorder(Border.NO_BORDER)
                .setPaddingTop(35)
                .setPaddingBottom(5)
        );

// Nome do emitente centralizado e em negrito
        declarationTable.addCell(new Cell()
                .add(new Paragraph(recibo.getEmitente().getEmitenteNome().toUpperCase())
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBold()
                        .setFontSize(12))
                .setBorder(Border.NO_BORDER)
                .setPadding(10)
                .setPaddingTop(0)
        );


        document.add(table);
        document.add(declarationTable);

        // Feche o documento
        document.close();
        return baos.toByteArray();
    }
}