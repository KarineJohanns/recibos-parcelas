package com.gerarecibos.recibos.repository;

import com.gerarecibos.recibos.model.Emitente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmitenteRepository extends JpaRepository<Emitente, Long> {
}
