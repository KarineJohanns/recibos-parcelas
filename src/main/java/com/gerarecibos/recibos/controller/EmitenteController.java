package com.gerarecibos.recibos.controller;

import com.gerarecibos.recibos.DTO.EmitenteDto;
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
    public ResponseEntity<Emitente> cadastrarEmitente(@RequestBody EmitenteDto emitenteDto) {
        Emitente emitenteSalvo = emitenteService.cadastrarEmitente(emitenteDto);
        return ResponseEntity.ok(emitenteSalvo);
    }

    @GetMapping("/{emitenteId}")
    public ResponseEntity<Emitente> obterEmitente(@PathVariable Long emitenteId) {
        Emitente emitente = emitenteService.obterEmitentePorId(emitenteId);
        return ResponseEntity.ok(emitente);
    }
}