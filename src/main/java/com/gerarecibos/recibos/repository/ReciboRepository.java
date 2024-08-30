package com.gerarecibos.recibos.repository;

import com.gerarecibos.recibos.model.Recibo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReciboRepository extends JpaRepository<Recibo, Long> {
}
