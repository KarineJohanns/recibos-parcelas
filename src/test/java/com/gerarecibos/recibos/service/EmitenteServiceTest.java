package com.gerarecibos.recibos.service;

import com.gerarecibos.recibos.model.Emitente;
import com.gerarecibos.recibos.repository.EmitenteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class EmitenteServiceTest {

    @Mock
    private EmitenteRepository emitenteRepository;

    @InjectMocks
    private EmitenteService emitenteService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCadastrarEmitente() {
        // Prepare mock data
        Emitente emitente = new Emitente();
        emitente.setId(1L);
        emitente.setNome("Emitente Teste");

        // Define comportamento do mock
        when(emitenteRepository.save(emitente)).thenReturn(emitente);

        // Call the method to test
        Emitente result = emitenteService.cadastrarEmitente(emitente);

        // Assert the results
        assertEquals(emitente, result);
    }

    @Test
    public void testObterEmitentePorId() {
        // Prepare mock data
        Emitente emitente = new Emitente();
        emitente.setId(1L);
        emitente.setNome("Emitente Teste");

        // Define comportamento do mock
        when(emitenteRepository.findById(1L)).thenReturn(Optional.of(emitente));

        // Call the method to test
        Emitente result = emitenteService.obterEmitentePorId(1L);

        // Assert the results
        assertEquals(emitente, result);
    }

    @Test
    public void testObterEmitentePorId_NotFound() {
        // Define comportamento do mock
        when(emitenteRepository.findById(1L)).thenReturn(Optional.empty());

        // Call the method to test and assert exception
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            emitenteService.obterEmitentePorId(1L);
        });
        assertEquals("Emitente não encontrado", thrown.getMessage());
    }
}
