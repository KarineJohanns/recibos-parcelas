package com.gerarecibos.recibos.service;

import com.gerarecibos.recibos.DTO.ClienteDto;
import com.gerarecibos.recibos.model.Cliente;
import com.gerarecibos.recibos.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCadastrarCliente() {
        ClienteDto clienteDto = new ClienteDto();
        clienteDto.setClienteNome("Cliente Test");
        clienteDto.setClienteCpf("12345678900");
        clienteDto.setClienteEndereco("Rua Teste, 123");
        clienteDto.setClienteTelefone("11987654321");

        Cliente cliente = new Cliente();
        cliente.setClienteNome("Cliente Test");
        cliente.setClienteCpf("12345678900");
        cliente.setClienteEndereco("Rua Teste, 123");
        cliente.setClienteTelefone("11987654321");

        when(clienteRepository.save(cliente)).thenReturn(cliente);

        Cliente result = clienteService.cadastrarCliente(clienteDto);
        assertEquals("Cliente Test", result.getClienteNome());
        assertEquals("12345678900", result.getClienteCpf());
        assertEquals("Rua Teste, 123", result.getClienteEndereco());
        assertEquals("11987654321", result.getClienteTelefone());
    }

    @Test
    public void testObterClientePorId() {
        Cliente cliente = new Cliente();
        cliente.setClienteNome("Cliente Test");
        cliente.setClienteCpf("12345678900");
        cliente.setClienteEndereco("Rua Teste, 123");
        cliente.setClienteTelefone("11987654321");

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        Cliente result = clienteService.obterClientePorId(1L);
        assertEquals("Cliente Test", result.getClienteNome());
        assertEquals("12345678900", result.getClienteCpf());
        assertEquals("Rua Teste, 123", result.getClienteEndereco());
        assertEquals("11987654321", result.getClienteTelefone());
    }

    @Test
    public void testObterClientePorIdNotFound() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> clienteService.obterClientePorId(1L));
        assertEquals("Cliente não encontrado", thrown.getMessage());
    }
}
