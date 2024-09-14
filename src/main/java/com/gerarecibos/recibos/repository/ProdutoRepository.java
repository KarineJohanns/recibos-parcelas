package com.gerarecibos.recibos.repository;

import com.gerarecibos.recibos.model.Cliente;
import com.gerarecibos.recibos.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    @Query("SELECT p FROM Produto p WHERE LOWER(p.produtoNome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<Produto> searchByNome(@Param("nome") String nome);
}