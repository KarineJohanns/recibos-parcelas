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

@Service
public class ReciboService {

    @Autowired
    private ReciboRepository reciboRepository;

    public byte[] gerarReciboPdf(Long reciboId) throws IOException {

        Recibo recibo = reciboRepository.findById(reciboId).orElseThrow(() -> new RuntimeException("Recibo não encontrado"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Recibo"));
        document.add(new Paragraph("Emitente: " + recibo.getEmitente()));
        document.add(new Paragraph("Detalhes: " + recibo.getConteudo()));
        document.add(new Paragraph("Cliente: " + recibo.getParcela().getCliente().getClienteNome()));
        document.add(new Paragraph("Produto: " + recibo.getParcela().getProduto().getProdutoNome()));
        document.add(new Paragraph("Valor Pago: " + recibo.getParcela().getValorPago()));
        document.add(new Paragraph("Data de Pagamento: " + recibo.getParcela().getDataPagamento()));

        document.close();

        return baos.toByteArray();
    }
}