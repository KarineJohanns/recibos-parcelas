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

import java.time.format.DateTimeFormatter;

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

        // Adicionar os detalhes do emitente no formato correto
        document.add(new Paragraph("Emitente:"));
        document.add(new Paragraph("Nome: " + recibo.getEmitente().getEmitenteNome()));
        document.add(new Paragraph("CPF: " + recibo.getEmitente().getEmitenteCpf()));
        document.add(new Paragraph("Endereço: " + recibo.getEmitente().getEmitenteEndereco()));
        document.add(new Paragraph("Telefone: " + recibo.getEmitente().getEmitenteTelefone()));

        // Adicionar outros detalhes do recibo como cliente, produto, etc.
        document.add(new Paragraph("Cliente: " + recibo.getParcela().getCliente().getClienteNome()));
        document.add(new Paragraph("Produto: " + recibo.getParcela().getProduto().getProdutoNome()));
        document.add(new Paragraph("Valor Pago: " + recibo.getParcela().getValorPago()));
        document.add(new Paragraph("Data de Pagamento: " + recibo.getParcela().getDataPagamento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));

        document.close();

        return baos.toByteArray();
    }
}