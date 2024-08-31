package com.gerarecibos.recibos.controller;

import com.gerarecibos.recibos.model.Emitente;
import com.gerarecibos.recibos.service.EmitenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/emitentes")
public class EmitenteController {

    @Autowired
    private EmitenteService emitenteService;

    @PostMapping
    public ResponseEntity<Emitente> cadastrarEmitente(@RequestBody Emitente emitente) {
        Emitente emitenteSalvo = emitenteService.cadastrarEmitente(emitente);
        return ResponseEntity.ok(emitenteSalvo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Emitente> obterEmitente(@PathVariable Long id) {
        Emitente emitente = emitenteService.obterEmitentePorId(id);
        return ResponseEntity.ok(emitente);
    }
}