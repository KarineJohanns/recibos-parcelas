package com.gerarecibos.recibos.repository;

import com.gerarecibos.recibos.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByClienteCpf(String cpf);
    Optional<Cliente> findByClienteId(Long clienteId);
    @Query("SELECT c FROM Cliente c WHERE LOWER(c.clienteNome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<Cliente> buscarPorNomeParcial(@Param("nome") String nome);
    boolean existsByClienteCpf(String cpf);
}
