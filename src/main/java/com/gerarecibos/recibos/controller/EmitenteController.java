package com.gerarecibos.recibos.controller;

import com.gerarecibos.recibos.DTO.EmitenteDto;
import com.gerarecibos.recibos.model.Emitente;
import com.gerarecibos.recibos.service.EmitenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emitentes")
public class EmitenteController {

    @Autowired
    private EmitenteService emitenteService;

    @PostMapping
    public ResponseEntity<Emitente> cadastrarEmitente(@RequestBody EmitenteDto emitenteDto) {
        Emitente novoEmitente = emitenteService.cadastrarEmitente(emitenteDto);
        return new ResponseEntity<>(novoEmitente, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Emitente> obterEmitentePorId(@PathVariable Long id) {
        Emitente emitente = emitenteService.obterEmitentePorId(id);
        return ResponseEntity.ok(emitente);
    }

    @GetMapping
    public ResponseEntity<List<Emitente>> listarTodosEmitentes() {
        List<Emitente> emitentes = emitenteService.listarTodosEmitentes();
        return ResponseEntity.ok(emitentes);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Emitente> editarEmitente(@PathVariable Long id, @RequestBody EmitenteDto emitenteDto) {
        Emitente emitenteAtualizado = emitenteService.editarEmitente(id, emitenteDto);
        return ResponseEntity.ok(emitenteAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarEmitente(@PathVariable Long id) {
        emitenteService.deletarEmitente(id);
        return ResponseEntity.noContent().build();
    }
}