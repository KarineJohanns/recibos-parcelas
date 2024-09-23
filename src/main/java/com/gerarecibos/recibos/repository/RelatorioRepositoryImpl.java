package com.gerarecibos.recibos.repository;

import com.gerarecibos.recibos.model.Parcela;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.time.LocalDate;
import java.util.List;

@Repository
public class RelatorioRepositoryImpl {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    public List<Parcela> findByClienteIdAndFilters(Long clienteId, LocalDate dataInicio, LocalDate dataFim, Boolean statusParcela) {
        logger.debug("Chamando findByClienteIdAndFilters com parâmetros: clienteId={}, dataInicio={}, dataFim={}, statusParcela={}",
                clienteId, dataInicio, dataFim, statusParcela);

        String jpql = "SELECT p FROM Parcela p WHERE p.cliente.clienteId = :clienteId " +
                "AND (cast(:dataInicio as date) IS NULL OR p.dataVencimento >= cast(:dataInicio as date)) " +
                "AND (cast(:dataFim as date) IS NULL OR p.dataVencimento <= cast(:dataFim as date)) " +
                "AND (:statusParcela IS NULL OR p.paga = :statusParcela)";

        Query query = entityManager.createQuery(jpql);
        query.setParameter("clienteId", clienteId);
        query.setParameter("dataInicio", dataInicio);
        query.setParameter("dataFim", dataFim);
        query.setParameter("statusParcela", statusParcela);

        @SuppressWarnings("unchecked")
        List<Parcela> parcelas = query.getResultList();
        logger.debug("findByClienteIdAndFilters retornou {} parcelas", parcelas.size());
        return parcelas;
    }

    public List<Object[]> findTotalByClienteIdAndFilters(Long clienteId, LocalDate dataInicio, LocalDate dataFim, Boolean statusParcela) {
        logger.debug("Chamando findTotalByClienteIdAndFilters com parâmetros: clienteId={}, dataInicio={}, dataFim={}, statusParcela={}",
                clienteId, dataInicio, dataFim, statusParcela);

        String jpql = "SELECT COALESCE(SUM(p.valorPago), 0), " +
                "COALESCE(SUM(CASE WHEN p.renegociada = false THEN p.valorParcela ELSE 0 END), 0), " +
                "(COALESCE(SUM(CASE WHEN p.renegociada = false THEN p.valorParcela ELSE 0 END), 0) - COALESCE(SUM(p.valorPago), 0)) " +
                "FROM Parcela p WHERE p.cliente.clienteId = :clienteId " +
                "AND (cast(:dataInicio as date) IS NULL OR p.dataVencimento >= cast(:dataInicio as date)) " +
                "AND (cast(:dataFim as date) IS NULL OR p.dataVencimento <= cast(:dataFim as date)) " +
                "AND (:statusParcela IS NULL OR p.paga = :statusParcela)";

        Query query = entityManager.createQuery(jpql);
        query.setParameter("clienteId", clienteId);
        query.setParameter("dataInicio", dataInicio);
        query.setParameter("dataFim", dataFim);
        query.setParameter("statusParcela", statusParcela);

        List<Object[]> resultados = query.getResultList();
        logger.debug("findTotalByClienteIdAndFilters retornou {} resultados", resultados.size());
        return resultados;
    }
}
