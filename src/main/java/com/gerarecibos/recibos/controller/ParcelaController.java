package com.gerarecibos.recibos.controller;

import com.gerarecibos.recibos.DTO.EscolhaDto;
import com.gerarecibos.recibos.DTO.ParcelaDto;
import com.gerarecibos.recibos.DTO.ParcelaPagamentoDto;
import com.gerarecibos.recibos.DTO.ParcelaResponseDto;
import com.gerarecibos.recibos.model.Parcela;
import com.gerarecibos.recibos.service.ParcelaService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<List<Parcela>> criarParcelas(@RequestBody ParcelaDto parcelaDto) {
        List<Parcela> novasParcelas = parcelaService.criarParcelas(parcelaDto);
        return new ResponseEntity<>(novasParcelas, HttpStatus.CREATED);
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
    public ResponseEntity<Parcela> editarParcela(@PathVariable Long id, @RequestBody ParcelaDto parcelaDto) {
        Parcela parcelaAtualizada = parcelaService.editarParcela(id, parcelaDto);
        return ResponseEntity.ok(parcelaAtualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarParcela(@PathVariable Long id) {
        parcelaService.deletarParcela(id);
        return ResponseEntity.noContent().build();
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
    public ResponseEntity<?> desfazerPagamento(@PathVariable Long id) {
        try {
            parcelaService.desfazerPagamento(id);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parcela não encontrada");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao desfazer pagamento");
        }
    }

    @PatchMapping("/{id}/escolha")
    public ResponseEntity<ParcelaResponseDto> processarEscolha(
            @PathVariable Long id,
            @RequestBody EscolhaDto escolhaDto) {

        ParcelaResponseDto responseDto = parcelaService.processarEscolha(id, escolhaDto);
        return ResponseEntity.ok(responseDto);
    }
}