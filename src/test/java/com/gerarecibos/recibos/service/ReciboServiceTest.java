package com.gerarecibos.recibos.service;

import com.gerarecibos.recibos.model.Recibo;
import com.gerarecibos.recibos.model.Parcela;
import com.gerarecibos.recibos.model.Cliente;
import com.gerarecibos.recibos.model.Produto;
import com.gerarecibos.recibos.repository.ReciboRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ReciboServiceTest {

    @Mock
    private ReciboRepository reciboRepository;

    @InjectMocks
    private ReciboService reciboService;

    @Test
    public void testGerarReciboPdf() throws IOException, com.gerarecibos.recibos.service.IOException {
        // Setup mock data
        Cliente cliente = new Cliente();
        cliente.setNome("João da Silva");

        Produto produto = new Produto();
        produto.setNome("Produto Teste");

        Parcela parcela = new Parcela();
        parcela.setCliente(cliente);
        parcela.setProduto(produto);
        parcela.setValorPago(150.0);
        parcela.setDataPagamento(LocalDate.now());

        Recibo recibo = new Recibo();
        recibo.setEmitente("Emitente Teste");
        recibo.setConteudo("Detalhes do recibo");
        recibo.setParcela(parcela);

        // Mock the repository
        when(reciboRepository.findById(1L)).thenReturn(Optional.of(recibo));

        // Call the method to test
        byte[] pdfBytes = reciboService.gerarReciboPdf(1L);

        // Verify the result
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);

        // Validate the content of the PDF
        try (ByteArrayInputStream bais = new ByteArrayInputStream(pdfBytes);
             PdfDocument pdfDocument = new PdfDocument(new PdfReader(bais))) {

            assertTrue(pdfDocument.getNumberOfPages() > 0);

            // Optionally, validate content by reading text from the PDF
            // Example: Extract text from the first page and verify it
            // PdfPage page = pdfDocument.getPage(1);
            // PdfTextExtractor textExtractor = new PdfTextExtractor(page);
            // String pageText = textExtractor.getText();
            // assertTrue(pageText.contains("Recibo"));
        }
    }
}
