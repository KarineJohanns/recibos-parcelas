package com.gerarecibos.recibos.service;

import com.gerarecibos.recibos.model.Cliente;
import com.gerarecibos.recibos.model.Emitente;
import com.gerarecibos.recibos.model.Parcela;
import com.gerarecibos.recibos.model.Produto;
import com.gerarecibos.recibos.model.Recibo;
import com.gerarecibos.recibos.repository.ReciboRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class ReciboServiceTest {

    @Mock
    private ReciboRepository reciboRepository;

    @InjectMocks
    private ReciboService reciboService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGerarReciboPdf() throws IOException, com.gerarecibos.recibos.service.IOException {
        // Configuração dos dados de teste
        Cliente cliente = new Cliente();
        cliente.setClienteNome("Cliente Teste");

        Produto produto = new Produto();
        produto.setProdutoNome("Produto Teste");

        Parcela parcela = new Parcela();
        parcela.setParcelaId(1L);
        parcela.setCliente(cliente);
        parcela.setProduto(produto);
        parcela.setValorPago(100.0);
        parcela.setDataPagamento(LocalDate.of(2024, 8, 31));

        Emitente emitente = new Emitente();
        emitente.setEmitenteNome("Emitente Teste");

        Recibo recibo = new Recibo();
        recibo.setId(1L);
        recibo.setEmitente(emitente);
        recibo.setConteudo("Detalhes do recibo");
        recibo.setParcela(parcela);

        when(reciboRepository.findById(1L)).thenReturn(Optional.of(recibo));

        byte[] pdfBytes = reciboService.gerarReciboPdf(1L);

        try (FileOutputStream fos = new FileOutputStream("recibo_test.pdf")) {
            fos.write(pdfBytes);
        }

        // Verificação básica do PDF gerado
        try (ByteArrayInputStream bais = new ByteArrayInputStream(pdfBytes);
             PdfDocument pdfDoc = new PdfDocument(new PdfReader(bais))) {

            // Verifique se o PDF contém pelo menos uma página
            assertTrue(pdfDoc.getNumberOfPages() > 0);

            // Verificar se o conteúdo esperado está presente no PDF
            String content = extractTextFromPdf(pdfDoc);
            assertTrue(content.contains("Cliente Teste"));
            assertTrue(content.contains("Produto Teste"));
            assertTrue(content.contains("Emitente Teste"));
            assertTrue(content.contains("Detalhes do recibo"));
        }
    }

    // Método para extrair o texto do PDF
    private String extractTextFromPdf(PdfDocument pdfDoc) throws IOException {
        StringBuilder text = new StringBuilder();
        for (int i = 1; i <= pdfDoc.getNumberOfPages(); i++) {
            text.append(PdfTextExtractor.getTextFromPage(pdfDoc.getPage(i))).append("\n");
        }
        return text.toString();
    }


}
