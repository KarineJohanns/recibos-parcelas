package com.gerarecibos.recibos.service;

import com.gerarecibos.recibos.DTO.ParcelaDto;
import com.gerarecibos.recibos.model.Cliente;
import com.gerarecibos.recibos.model.Emitente;
import com.gerarecibos.recibos.model.Parcela;
import com.gerarecibos.recibos.model.Produto;
import com.gerarecibos.recibos.repository.ClienteRepository;
import com.gerarecibos.recibos.repository.ParcelaRepository;
import com.gerarecibos.recibos.repository.ProdutoRepository;
import com.gerarecibos.recibos.service.EmitenteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ParcelaServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private EmitenteService emitenteService;

    @Mock
    private ParcelaRepository parcelaRepository;

    @InjectMocks
    private ParcelaService parcelaService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCriarParcelas() {
        // Prepare mock data
        ParcelaDto parcelaDto = new ParcelaDto();
        parcelaDto.setClienteId(1L);
        parcelaDto.setProdutoId(1L);
        parcelaDto.setValorTotalProduto(100.0);
        parcelaDto.setNumeroParcelas(2);
        parcelaDto.setEmitenteId(1L);

        Cliente cliente = new Cliente();
        cliente.setClienteId(1L);

        Produto produto = new Produto();
        produto.setProdutoId(1L);

        Emitente emitente = new Emitente();
        emitente.setEmitenteId(1L);

        // Configurar os mocks
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(emitenteService.obterEmitentePorId(1L)).thenReturn(emitente);

        // Prepare the expected parcels
        Parcela parcela1 = new Parcela();
        parcela1.setCliente(cliente);
        parcela1.setProduto(produto);
        parcela1.setNumeroParcelas(2);
        parcela1.setValorParcela(50.0);
        parcela1.setEmitente(emitente);

        Parcela parcela2 = new Parcela();
        parcela2.setCliente(cliente);
        parcela2.setProduto(produto);
        parcela2.setNumeroParcelas(2);
        parcela2.setValorParcela(50.0);
        parcela2.setEmitente(emitente);

        List<Parcela> parcelasEsperadas = new ArrayList<>();
        parcelasEsperadas.add(parcela1);
        parcelasEsperadas.add(parcela2);

        // Configurar o mock para salvar todas as parcelas
        when(parcelaRepository.saveAll(parcelasEsperadas)).thenReturn(parcelasEsperadas);

        // Call the method to test
        List<Parcela> parcelas = parcelaService.criarParcelas(parcelaDto);

        // Assert the results
        assertNotNull(parcelas);
        assertEquals(2, parcelas.size());

        // Verifique outras propriedades conforme necessário
        for (Parcela parcela : parcelas) {
            assertNotNull(parcela.getCliente());
            assertNotNull(parcela.getProduto());
            assertEquals(50.0, parcela.getValorParcela());
            assertNotNull(parcela.getEmitente());
        }

        // Verifique se saveAll foi chamado com as parcelas esperadas
        verify(parcelaRepository).saveAll(parcelasEsperadas);
    }

    @Test
    public void testCriarParcelasClienteNotFound() {
        ParcelaDto parcelaDto = new ParcelaDto();
        parcelaDto.setClienteId(1L);
        parcelaDto.setProdutoId(1L);
        parcelaDto.setValorTotalProduto(100.0);
        parcelaDto.setNumeroParcelas(2);
        parcelaDto.setEmitenteId(1L);

        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            parcelaService.criarParcelas(parcelaDto);
        });

        assertEquals("Cliente não encontrado", exception.getMessage());
    }

    @Test
    public void testCriarParcelasProdutoNotFound() {
        ParcelaDto parcelaDto = new ParcelaDto();
        parcelaDto.setClienteId(1L);
        parcelaDto.setProdutoId(1L);
        parcelaDto.setValorTotalProduto(100.0);
        parcelaDto.setNumeroParcelas(2);
        parcelaDto.setEmitenteId(1L);

        Cliente cliente = new Cliente();
        cliente.setClienteId(1L);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(produtoRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            parcelaService.criarParcelas(parcelaDto);
        });

        assertEquals("Produto não encontrado", exception.getMessage());
    }

    @Test
    public void testCriarParcelasEmitenteNotFound() {
        ParcelaDto parcelaDto = new ParcelaDto();
        parcelaDto.setClienteId(1L);
        parcelaDto.setProdutoId(1L);
        parcelaDto.setValorTotalProduto(100.0);
        parcelaDto.setNumeroParcelas(2);
        parcelaDto.setEmitenteId(1L);

        Cliente cliente = new Cliente();
        cliente.setClienteId(1L);

        Produto produto = new Produto();
        produto.setProdutoId(1L);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(emitenteService.obterEmitentePorId(1L)).thenThrow(new RuntimeException("Emitente não encontrado"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            parcelaService.criarParcelas(parcelaDto);
        });

        assertEquals("Emitente não encontrado", exception.getMessage());
    }
}
