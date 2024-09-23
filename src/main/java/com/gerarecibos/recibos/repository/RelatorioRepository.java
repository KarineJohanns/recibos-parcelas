package com.gerarecibos.recibos.repository;

import com.gerarecibos.recibos.model.Parcela;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RelatorioRepository extends JpaRepository<Parcela, Long> {
    List<Parcela> findByClienteIdAndFilters(Long clienteId, LocalDate dataInicio, LocalDate dataFim, Boolean statusParcela);

    List<Object[]> findTotalByClienteIdAndFilters(Long clienteId, LocalDate dataInicio, LocalDate dataFim, Boolean statusParcela);
}
