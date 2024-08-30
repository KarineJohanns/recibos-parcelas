package com.gerarecibos.recibos.controller;

import com.gerarecibos.recibos.service.IOException;
import com.gerarecibos.recibos.service.ReciboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recibos")
public class ReciboController {

    @Autowired
    private ReciboService reciboService;

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> gerarReciboPdf(@PathVariable Long id) {
        try {
            byte[] pdf = reciboService.gerarReciboPdf(id);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "recibo.pdf");
            return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
