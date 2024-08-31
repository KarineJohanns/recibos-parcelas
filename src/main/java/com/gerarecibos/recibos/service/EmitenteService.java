package com.gerarecibos.recibos.service;

import com.gerarecibos.recibos.model.Emitente;
import com.gerarecibos.recibos.repository.EmitenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmitenteService {

    @Autowired
    private EmitenteRepository emitenteRepository;

    public Emitente cadastrarEmitente(Emitente emitente) {
        return emitenteRepository.save(emitente);
    }

    public Emitente obterEmitentePorId(Long id) {
        return emitenteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Emitente não encontrado"));
    }
}