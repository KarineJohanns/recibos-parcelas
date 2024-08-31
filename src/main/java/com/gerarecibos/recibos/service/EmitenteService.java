package com.gerarecibos.recibos.service;

import com.gerarecibos.recibos.DTO.EmitenteDto;
import com.gerarecibos.recibos.Exceptions.ResourceNotFoundException;
import com.gerarecibos.recibos.model.Emitente;
import com.gerarecibos.recibos.repository.EmitenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public Emitente obterEmitentePorId(Long id) {
        return emitenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Emitente não encontrado"));
    }

    public List<Emitente> listarTodosEmitentes() {
        return emitenteRepository.findAll();
    }

    public Emitente editarEmitente(Long id, EmitenteDto emitenteDto) {
        Emitente emitente = obterEmitentePorId(id);
        emitente.setEmitenteNome(emitenteDto.getEmitenteNome());
        emitente.setEmitenteCpf(emitenteDto.getEmitenteCpf());
        emitente.setEmitenteEndereco(emitenteDto.getEmitenteEndereco());
        emitente.setEmitenteTelefone(emitenteDto.getEmitenteTelefone());
        return emitenteRepository.save(emitente);
    }

    public void deletarEmitente(Long id) {
        Emitente emitente = obterEmitentePorId(id);
        emitenteRepository.delete(emitente);
    }
}
