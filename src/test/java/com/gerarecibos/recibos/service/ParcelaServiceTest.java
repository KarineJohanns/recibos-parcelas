package com.gerarecibos.recibos.service;

import com.gerarecibos.recibos.DTO.ParcelaDto;
import com.gerarecibos.recibos.DTO.EscolhaDto;
import com.gerarecibos.recibos.DTO.ParcelaResponseDto;
import com.gerarecibos.recibos.model.Cliente;
import com.gerarecibos.recibos.model.Emitente;
import com.gerarecibos.recibos.model.Parcela;
import com.gerarecibos.recibos.model.Produto;
import com.gerarecibos.recibos.repository.ClienteRepository;
import com.gerarecibos.recibos.repository.EmitenteRepository;
import com.gerarecibos.recibos.repository.ParcelaRepository;
import com.gerarecibos.recibos.repository.ProdutoRepository;
import com.gerarecibos.recibos.service.EmitenteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
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
    private EmitenteRepository emitenteRepository;

    @Mock
    private ParcelaRepository parcelaRepository;

    @InjectMocks
    private ParcelaService parcelaService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCriarParcelas() {
        ParcelaDto parcelaDto = new ParcelaDto();
        parcelaDto.setValorTotalProduto(1000.0);
        parcelaDto.setNumeroParcelas(5);
        parcelaDto.setDataCriacao(LocalDate.now());
        parcelaDto.setIntervalo("SEMANAL");
        parcelaDto.setClienteId(1L);
        parcelaDto.setProdutoId(1L);
        parcelaDto.setEmitenteId(1L);

        Cliente cliente = new Cliente();
        cliente.setClienteId(1L);

        Produto produto = new Produto();
        produto.setProdutoId(1L);

        Emitente emitente = new Emitente();
        emitente.setEmitenteId(1L);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(emitenteService.obterEmitentePorId(1L)).thenReturn(emitente);

        parcelaService.criarParcelas(parcelaDto);

        verify(parcelaRepository, times(5)).save(any(Parcela.class));
    }


}
