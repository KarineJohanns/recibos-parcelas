package com.gerarecibos.recibos.service;

import com.gerarecibos.recibos.DTO.ClienteDto;
import com.gerarecibos.recibos.model.Cliente;
import com.gerarecibos.recibos.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public Cliente cadastrarCliente(ClienteDto clienteDto) {
        Cliente cliente = new Cliente();
        cliente.setClienteNome(clienteDto.getClienteNome());
        cliente.setClienteCpf(clienteDto.getClienteCpf());
        cliente.setClienteEndereco(clienteDto.getClienteEndereco());
        cliente.setClienteTelefone(clienteDto.getClienteTelefone());
        return clienteRepository.save(cliente);
    }

    public Cliente obterClientePorId(Long clienteId) {
        return clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
    }
}