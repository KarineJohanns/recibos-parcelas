package com.gerarecibos.recibos.repository;

import com.gerarecibos.recibos.model.Cliente;
import com.gerarecibos.recibos.model.Parcela;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParcelaRepository extends JpaRepository<Parcela, Long> {
    // Método para verificar se existe uma parcela vinculada a um cliente
    boolean existsByCliente(Cliente cliente);
}