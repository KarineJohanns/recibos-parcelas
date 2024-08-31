package com.gerarecibos.recibos.service;

import com.gerarecibos.recibos.DTO.ProdutoDto;
import com.gerarecibos.recibos.model.Produto;
import com.gerarecibos.recibos.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ProdutoService produtoService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCriarProduto() {
        // Criação do DTO com dados do produto
        ProdutoDto produtoDto = new ProdutoDto();
        produtoDto.setProdutoNome("Produto Teste");
        produtoDto.setProdutoValorTotal(150.0);
        produtoDto.setProdutoDescricao("Descrição do Produto Teste");

        // Criação do produto esperado para comparação
        Produto produtoEsperado = new Produto();
        produtoEsperado.setProdutoNome("Produto Teste");
        produtoEsperado.setProdutoValorTotal(150.0);
        produtoEsperado.setProdutoDescricao("Descrição do Produto Teste");

        // Configurar o comportamento do mock do repositório
        when(produtoRepository.save(any(Produto.class))).thenReturn(produtoEsperado);

        // Chamada ao método que queremos testar
        Produto produtoCriado = produtoService.criarProduto(produtoDto);

        // Verificações
        assertEquals("Produto Teste", produtoCriado.getProdutoNome());
        assertEquals(150.0, produtoCriado.getProdutoValorTotal());
        assertEquals("Descrição do Produto Teste", produtoCriado.getProdutoDescricao());

        // Verifica se o método save foi chamado corretamente no repositório
        verify(produtoRepository, times(1)).save(any(Produto.class));
    }
}
