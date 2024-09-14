package com.gerarecibos.recibos.service;

import com.gerarecibos.recibos.DTO.ClienteDto;
import com.gerarecibos.recibos.Exceptions.ClienteVinculadoException;
import com.gerarecibos.recibos.Exceptions.ResourceNotFoundException;
import com.gerarecibos.recibos.model.Cliente;
import com.gerarecibos.recibos.repository.ClienteRepository;
import com.gerarecibos.recibos.repository.ParcelaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ParcelaRepository parcelaRepository;

    public ResponseEntity<String> cadastrarCliente(ClienteDto clienteDto) {
        // Verificar se já existe um cliente com o mesmo CPF
        Optional<Cliente> clienteExistente = clienteRepository.findByClienteCpf(clienteDto.getClienteCpf());

        if (clienteExistente.isPresent()) {
            // Se já existir, lança uma exceção
            throw new ClienteVinculadoException("Já existe um cliente cadastrado com esse CPF.");
        }
        // Caso contrário, cria e salva o novo cliente
        Cliente cliente = new Cliente();
        cliente.setClienteNome(clienteDto.getClienteNome());
        cliente.setClienteCpf(clienteDto.getClienteCpf());
        cliente.setClienteEndereco(clienteDto.getClienteEndereco());
        cliente.setClienteTelefone(clienteDto.getClienteTelefone());

        clienteRepository.save(cliente);

        // Retorna uma mensagem de sucesso
        return ResponseEntity.ok("Cliente cadastrado com sucesso.");
    }

    public Cliente obterClientePorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));
    }

    public List<Cliente> obterClientePorNome(String nome) {
        return clienteRepository.buscarPorNomeParcial(nome);
    }

    public List<Cliente> listarTodosClientes() {
        return clienteRepository.findAll();
    }

    public Cliente editarCliente(Long id, ClienteDto clienteDto) {

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteVinculadoException("Cliente não encontrado."));

        // Verifica se o CPF é diferente do CPF atual e já está cadastrado
        if (!cliente.getClienteCpf().equals(clienteDto.getClienteCpf()) &&
                clienteRepository.existsByClienteCpf(clienteDto.getClienteCpf())) {
            throw new ClienteVinculadoException("CPF já cadastrado.");
        }

        cliente.setClienteNome(clienteDto.getClienteNome());
        cliente.setClienteCpf(clienteDto.getClienteCpf());
        cliente.setClienteEndereco(clienteDto.getClienteEndereco());
        cliente.setClienteTelefone(clienteDto.getClienteTelefone());

        return clienteRepository.save(cliente);
    }

    public void deletarCliente(Long id) {
        Cliente cliente = obterClientePorId(id);

        // Verificar se o cliente está vinculado a alguma parcela
        boolean isClienteVinculado = parcelaRepository.existsByCliente(cliente);

        if (isClienteVinculado) {
            throw new ClienteVinculadoException("Não é possível excluir o cliente, pois ele está vinculado a uma ou mais parcelas.");
        }

        clienteRepository.delete(cliente);
    }
}
