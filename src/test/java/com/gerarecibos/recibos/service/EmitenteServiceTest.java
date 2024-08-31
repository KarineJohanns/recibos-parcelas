package com.gerarecibos.recibos.service;

import com.gerarecibos.recibos.DTO.EmitenteDto;
import com.gerarecibos.recibos.model.Emitente;
import com.gerarecibos.recibos.repository.EmitenteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
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
        EmitenteDto emitenteDto = new EmitenteDto();
        emitenteDto.setEmitenteNome("Emitente Teste");
        emitenteDto.setEmitenteCpf("12345678901");
        emitenteDto.setEmitenteEndereco("Avenida Teste, 456");
        emitenteDto.setEmitenteTelefone("11998765432");

        Emitente emitente = new Emitente();
        emitente.setEmitenteNome("Emitente Teste");
        emitente.setEmitenteCpf("12345678901");
        emitente.setEmitenteEndereco("Avenida Teste, 456");
        emitente.setEmitenteTelefone("11998765432");

        when(emitenteRepository.save(emitente)).thenReturn(emitente);

        Emitente result = emitenteService.cadastrarEmitente(emitenteDto);
        assertEquals("Emitente Teste", result.getEmitenteNome());
        assertEquals("12345678901", result.getEmitenteCpf());
        assertEquals("Avenida Teste, 456", result.getEmitenteEndereco());
        assertEquals("11998765432", result.getEmitenteTelefone());
    }

    @Test
    public void testObterEmitentePorId() {
        Emitente emitente = new Emitente();
        emitente.setEmitenteNome("Emitente Teste");
        emitente.setEmitenteCpf("12345678901");
        emitente.setEmitenteEndereco("Avenida Teste, 456");
        emitente.setEmitenteTelefone("11998765432");

        when(emitenteRepository.findById(1L)).thenReturn(Optional.of(emitente));

        Emitente result = emitenteService.obterEmitentePorId(1L);
        assertEquals("Emitente Teste", result.getEmitenteNome());
        assertEquals("12345678901", result.getEmitenteCpf());
        assertEquals("Avenida Teste, 456", result.getEmitenteEndereco());
        assertEquals("11998765432", result.getEmitenteTelefone());
    }

    @Test
    public void testListarTodosEmitentes() {
        Emitente emitente1 = new Emitente(1L, "Emitente 1", "12345678901", "Endereço 1", "11998765432");
        Emitente emitente2 = new Emitente(2L, "Emitente 2", "98765432109", "Endereço 2", "21987654321");

        when(emitenteRepository.findAll()).thenReturn(Arrays.asList(emitente1, emitente2));

        List<Emitente> resultado = emitenteService.listarTodosEmitentes();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(emitenteRepository, times(1)).findAll();
    }

    @Test
    public void testEditarEmitente() {
        Emitente emitente = new Emitente(1L, "Emitente Teste", "12345678901", "Endereço Teste", "11998765432");

        EmitenteDto emitenteDto = new EmitenteDto();
        emitenteDto.setEmitenteNome("Emitente Editado");
        emitenteDto.setEmitenteCpf("98765432109");
        emitenteDto.setEmitenteEndereco("Endereço Editado");
        emitenteDto.setEmitenteTelefone("21987654321");

        when(emitenteRepository.findById(1L)).thenReturn(Optional.of(emitente));
        when(emitenteRepository.save(any(Emitente.class))).thenReturn(emitente);

        Emitente resultado = emitenteService.editarEmitente(1L, emitenteDto);

        assertNotNull(resultado);
        assertEquals("Emitente Editado", resultado.getEmitenteNome());
        verify(emitenteRepository, times(1)).findById(1L);
        verify(emitenteRepository, times(1)).save(any(Emitente.class));
    }

    @Test
    public void testDeletarEmitente() {
        Emitente emitente = new Emitente(1L, "Emitente Teste", "12345678901", "Endereço Teste", "11998765432");

        when(emitenteRepository.findById(1L)).thenReturn(Optional.of(emitente));

        emitenteService.deletarEmitente(1L);

        verify(emitenteRepository, times(1)).findById(1L);
        verify(emitenteRepository, times(1)).delete(emitente);
    }

    @Test
    public void testObterEmitentePorIdNotFound() {
        when(emitenteRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> emitenteService.obterEmitentePorId(1L));
        assertEquals("Emitente não encontrado", thrown.getMessage());
    }
}
