package com.gerarecibos.recibos.service;

import com.gerarecibos.recibos.DTO.EmitenteDto;
import com.gerarecibos.recibos.model.Emitente;
import com.gerarecibos.recibos.repository.EmitenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmitenteService {

    @Autowired
    private EmitenteRepository emitenteRepository;

    public Emitente cadastrarEmitente(EmitenteDto emitenteDto) {
        Emitente emitente = new Emitente();
        emitente.setEmitenteNome(emitenteDto.getEmitenteNome());
        emitente.setEmitenteCpf(emitenteDto.getEmitenteCpf());
        emitente.setEmitenteEndereco(emitenteDto.getEmitenteEndereco());
        emitente.setEmitenteTelefone(emitenteDto.getEmitenteTelefone());
        return emitenteRepository.save(emitente);
    }

    public Emitente obterEmitentePorId(Long emitenteId) {
        return emitenteRepository.findById(emitenteId)
                .orElseThrow(() -> new RuntimeException("Emitente não encontrado"));
    }
}