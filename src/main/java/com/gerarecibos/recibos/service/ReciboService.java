package com.gerarecibos.recibos.service;

import com.gerarecibos.recibos.model.Recibo;
import com.gerarecibos.recibos.repository.ReciboRepository;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;

@Service
public class ReciboService {

    @Autowired
    private ReciboRepository reciboRepository;

    public byte[] gerarReciboPdf(Long parcelaId) throws IOException {
        // Obtenha o recibo do banco de dados
        Recibo recibo = reciboRepository.findById(parcelaId)
                .orElseThrow(() -> new RuntimeException("Recibo não encontrado"));

        // Crie um documento PDF
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

        // Adicione o conteúdo ao PDF
        document.add(new Paragraph("Emitente: " + recibo.getEmitente().getEmitenteNome()));
        document.add(new Paragraph("Cliente: " + recibo.getParcela().getCliente().getClienteNome()));
        document.add(new Paragraph("Produto: " + recibo.getParcela().getProduto().getProdutoNome()));
        document.add(new Paragraph("Valor Pago: " + recibo.getParcela().getValorPago()));
        document.add(new Paragraph("Data do Vencimento: " + recibo.getParcela().getDataVencimento()));
        document.add(new Paragraph("Conteúdo: " + recibo.getConteudo()));

        // Feche o documento
        document.close();
        return baos.toByteArray();
    }
}