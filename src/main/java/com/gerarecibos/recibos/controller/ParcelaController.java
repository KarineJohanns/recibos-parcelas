package com.gerarecibos.recibos.controller;

import com.gerarecibos.recibos.DTO.EscolhaDto;
import com.gerarecibos.recibos.DTO.ParcelaDto;
import com.gerarecibos.recibos.DTO.ParcelaPagamentoDto;
import com.gerarecibos.recibos.DTO.ParcelaResponseDto;
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
    public ResponseEntity<ParcelaResponseDto> pagarParcela(
            @PathVariable Long id,
            @RequestBody ParcelaPagamentoDto dto) {

        ParcelaResponseDto parcelaPaga = parcelaService.pagarParcela(
                id,
                dto.getValorPago(),
                dto.getDataPagamento(),
                dto.getGerarNovasParcelas(),
                dto.getDesconto()
        );

        return ResponseEntity.ok(parcelaPaga);
    }

    @PatchMapping("/{id}/escolha")
    public ResponseEntity<Parcela> processarEscolha(
            @PathVariable Long id,
            @RequestBody EscolhaDto escolhaDto) {

        Parcela parcela = parcelaService.processarEscolha(id, escolhaDto);
        return ResponseEntity.ok(parcela);
    }
}