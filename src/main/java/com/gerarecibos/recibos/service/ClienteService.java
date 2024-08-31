package com.gerarecibos.recibos.service;

import com.gerarecibos.recibos.DTO.ClienteDto;
import com.gerarecibos.recibos.Exceptions.ResourceNotFoundException;
import com.gerarecibos.recibos.model.Cliente;
import com.gerarecibos.recibos.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public Cliente obterClientePorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));
    }

    public List<Cliente> listarTodosClientes() {
        return clienteRepository.findAll();
    }

    public Cliente editarCliente(Long id, ClienteDto clienteDto) {
        Cliente cliente = obterClientePorId(id);
        cliente.setClienteNome(clienteDto.getClienteNome());
        cliente.setClienteCpf(clienteDto.getClienteCpf());
        cliente.setClienteEndereco(clienteDto.getClienteEndereco());
        cliente.setClienteTelefone(clienteDto.getClienteTelefone());
        return clienteRepository.save(cliente);
    }

    public void deletarCliente(Long id) {
        Cliente cliente = obterClientePorId(id);
        clienteRepository.delete(cliente);
    }
}
