package com.gerarecibos.recibos.controller;

import com.gerarecibos.recibos.DTO.EscolhaDto;
import com.gerarecibos.recibos.DTO.parcelas.ParcelaDto;
import com.gerarecibos.recibos.DTO.parcelas.ParcelaPagamentoDto;
import com.gerarecibos.recibos.DTO.parcelas.ParcelaResponseDto;
import com.gerarecibos.recibos.model.Parcela;
import com.gerarecibos.recibos.service.ParcelaService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/parcelas", method = RequestMethod.OPTIONS)
public class ParcelaController {

    @Autowired
    private ParcelaService parcelaService;

    @PostMapping
    public ResponseEntity<ParcelaResponseDto> criarParcelas(@RequestBody ParcelaDto parcelaDto) {
        ParcelaResponseDto responseDto = parcelaService.criarParcelas(parcelaDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Parcela> obterParcelaPorId(@PathVariable Long id) {
        Parcela parcela = parcelaService.obterParcelaPorId(id);
        return ResponseEntity.ok(parcela);
    }
    @GetMapping
    public ResponseEntity<List<Parcela>> listarTodasParcelas() {
        List<Parcela> parcelas = parcelaService.listarTodasParcelas();
        return ResponseEntity.ok(parcelas);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ParcelaResponseDto> editarParcela(@PathVariable Long id, @RequestBody ParcelaDto parcelaDto) {
        ParcelaResponseDto responseDto = parcelaService.editarParcela(id, parcelaDto);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ParcelaResponseDto> deletarParcela(@PathVariable Long id) {
        ParcelaResponseDto responseDto = parcelaService.deletarParcela(id);
        return ResponseEntity.ok(responseDto);
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

    @PatchMapping("/{id}/desfazer")
    public ResponseEntity<ParcelaResponseDto> desfazerPagamento(@PathVariable Long id) {
        ParcelaResponseDto responseDto = parcelaService.desfazerPagamento(id); // Chama o serviço que retorna o DTO
        return ResponseEntity.ok(responseDto); // Retorna o DTO em um ResponseEntity
    }

    @PatchMapping("/{id}/escolha")
    public ResponseEntity<ParcelaResponseDto> processarEscolha(
            @PathVariable Long id,
            @RequestBody EscolhaDto escolhaDto) {

        ParcelaResponseDto responseDto = parcelaService.processarEscolha(id, escolhaDto);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{id}/renegociar")
    public ResponseEntity<ParcelaResponseDto> renegociarParcela(
            @PathVariable Long id,
            @RequestBody EscolhaDto escolhaDto) {

        ParcelaResponseDto responseDto = parcelaService.renegociarParcela(id, escolhaDto);
        return ResponseEntity.ok(responseDto);
    }
}