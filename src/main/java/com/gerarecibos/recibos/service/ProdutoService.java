package com.gerarecibos.recibos.service;

import com.gerarecibos.recibos.DTO.ProdutoDto;
import com.gerarecibos.recibos.Exceptions.ResourceNotFoundException;
import com.gerarecibos.recibos.model.Produto;
import com.gerarecibos.recibos.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    public Produto cadastrarProduto(ProdutoDto produtoDto) {
        Produto produto = new Produto();
        produto.setProdutoNome(produtoDto.getProdutoNome());
        produto.setProdutoValorTotal(produtoDto.getProdutoValorTotal());
        produto.setProdutoDescricao(produtoDto.getProdutoDescricao());
        return produtoRepository.save(produto);
    }

    public Produto obterProdutoPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));
    }

    public List<Produto> buscarPorNome(String nome) {
        return produtoRepository.searchByNome(nome);
    }

    public List<Produto> listarTodosProdutos() {
        return produtoRepository.findAll();
    }

    public Produto editarProduto(Long id, ProdutoDto produtoDto) {
        Produto produto = obterProdutoPorId(id);
        produto.setProdutoNome(produtoDto.getProdutoNome());
        produto.setProdutoValorTotal(produtoDto.getProdutoValorTotal());
        produto.setProdutoDescricao(produtoDto.getProdutoDescricao());
        return produtoRepository.save(produto);
    }

    public void deletarProduto(Long id) {
        Produto produto = obterProdutoPorId(id);
        produtoRepository.delete(produto);
    }
}

