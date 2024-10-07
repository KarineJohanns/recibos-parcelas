package com.gerarecibos.recibos.controller;

import com.gerarecibos.recibos.model.Parcela;
import com.gerarecibos.recibos.model.Recibo;
import com.gerarecibos.recibos.repository.ReciboRepository;
import com.gerarecibos.recibos.service.IOException;
import com.gerarecibos.recibos.service.ReciboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ReciboController {

    @Autowired
    private ReciboService reciboService;

    @Autowired
    private ReciboRepository reciboRepository;

    @GetMapping("/recibos/{parcelaId}/pdf")
    public ResponseEntity<byte[]> gerarReciboPdf(@PathVariable Long parcelaId) {
        try {
            // Aqui buscamos o recibo com base no parcelaId
            Recibo recibo = reciboRepository.findByParcelaId(parcelaId)
                    .orElseThrow(() -> new RuntimeException("Recibo não encontrado para a parcela."));

            // Obtenha a parcela a partir do recibo
            Parcela parcela = recibo.getParcela(); // Supondo que o recibo tem um método getParcela()

            // Obtenha o documento da parcela e substitua os espaços por sublinhados
            String documento = parcela.getDocumento().replaceAll("[ /]", "_"); // Substitui espaços e barras por _
            // ou outro caractere

            byte[] pdf = reciboService.gerarReciboPdf(parcelaId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);

            // Define o nome do arquivo com o documento da parcela
            String fileName = "Recibo_" + documento + ".pdf"; // Atualizando o nome do arquivo
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.set("Access-Control-Expose-Headers", "Content-Disposition");

            return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
