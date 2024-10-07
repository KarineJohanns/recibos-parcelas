package com.gerarecibos.recibos.repository;

import com.gerarecibos.recibos.model.Recibo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReciboRepository extends JpaRepository<Recibo, Long> {
    Optional<Recibo> findById(Long parcelaId);

    @Query("SELECT r FROM Recibo r WHERE r.parcela.parcelaId = :parcelaId")
    Optional<Recibo> findByParcelaId(@Param("parcelaId") Long parcelaId);
}
