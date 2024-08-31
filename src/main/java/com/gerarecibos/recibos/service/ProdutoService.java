package com.gerarecibos.recibos.service;

import com.gerarecibos.recibos.DTO.ProdutoDto;
import com.gerarecibos.recibos.model.Produto;
import com.gerarecibos.recibos.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    public Produto criarProduto(ProdutoDto produtoDto) {
        Produto produto = new Produto();
        produto.setProdutoNome(produtoDto.getProdutoNome());
        produto.setProdutoValorTotal(produtoDto.getProdutoValorTotal());
        produto.setProdutoDescricao(produtoDto.getProdutoDescricao()); // Lidar com a descrição
        return produtoRepository.save(produto);
    }
}
