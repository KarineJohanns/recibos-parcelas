package com.gerarecibos.recibos.controller;

import com.gerarecibos.recibos.DTO.ProdutoDto;
import com.gerarecibos.recibos.model.Produto;
import com.gerarecibos.recibos.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @PostMapping
    public ResponseEntity<Produto> criarProduto(@RequestBody ProdutoDto produtoDto) {
        Produto produto = produtoService.criarProduto(produtoDto);
        return ResponseEntity.ok(produto);
    }
}
