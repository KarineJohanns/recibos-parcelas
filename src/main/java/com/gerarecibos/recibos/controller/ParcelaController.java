package com.gerarecibos.recibos.controller;

import com.gerarecibos.recibos.DTO.ParcelaDto;
import com.gerarecibos.recibos.model.Parcela;
import com.gerarecibos.recibos.service.ParcelaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/parcelas")
public class ParcelaController {

    @Autowired
    private ParcelaService parcelaService;

    @PostMapping
    public ResponseEntity<List<Parcela>> cadastrarParcelas(@RequestBody ParcelaDto parcelaDto) {
        List<Parcela> parcelas = parcelaService.criarParcelas(parcelaDto);
        return ResponseEntity.ok(parcelas);
    }

    @GetMapping
    public ResponseEntity<List<Parcela>> listarParcelas() {
        return ResponseEntity.ok(parcelaService.listarParcelas());
    }

    @PatchMapping("/{id}/pagar")
    public ResponseEntity<Parcela> pagarParcela(@PathVariable Long id, @RequestBody PagamentoDto pagamentoDto) {
        Parcela parcelaPaga = parcelaService.pagarParcela(id, pagamentoDto.getValorPago(), pagamentoDto.getDataPagamento());
        return ResponseEntity.ok(parcelaPaga);
    }
}