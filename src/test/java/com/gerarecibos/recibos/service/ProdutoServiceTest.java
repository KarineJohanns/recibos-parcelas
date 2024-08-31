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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    public void testCadastrarProduto() {
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
        Produto produtoCriado = produtoService.cadastrarProduto(produtoDto);

        // Verificações
        assertEquals("Produto Teste", produtoCriado.getProdutoNome());
        assertEquals(150.0, produtoCriado.getProdutoValorTotal());
        assertEquals("Descrição do Produto Teste", produtoCriado.getProdutoDescricao());

        // Verifica se o método save foi chamado corretamente no repositório
        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    public void testObterProdutoPorId() {
        Produto produto = new Produto(1L, "Produto Teste", 100.0, "Descrição do Produto Teste");

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        Produto resultado = produtoService.obterProdutoPorId(1L);

        assertNotNull(resultado);
        assertEquals("Produto Teste", resultado.getProdutoNome());
        verify(produtoRepository, times(1)).findById(1L);
    }

    @Test
    public void testListarTodosProdutos() {
        Produto produto1 = new Produto(1L, "Produto 1", 100.0, "Descrição 1");
        Produto produto2 = new Produto(2L, "Produto 2", 200.0, "Descrição 2");

        when(produtoRepository.findAll()).thenReturn(Arrays.asList(produto1, produto2));

        List<Produto> resultado = produtoService.listarTodosProdutos();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(produtoRepository, times(1)).findAll();
    }

    @Test
    public void testEditarProduto() {
        Produto produto = new Produto(1L, "Produto Teste", 100.0, "Descrição do Produto Teste");

        ProdutoDto produtoDto = new ProdutoDto();
        produtoDto.setProdutoNome("Produto Editado");
        produtoDto.setProdutoValorTotal(150.0);
        produtoDto.setProdutoDescricao("Descrição Editada");

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

        Produto resultado = produtoService.editarProduto(1L, produtoDto);

        assertNotNull(resultado);
        assertEquals("Produto Editado", resultado.getProdutoNome());
        verify(produtoRepository, times(1)).findById(1L);
        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    public void testDeletarProduto() {
        Produto produto = new Produto(1L, "Produto Teste", 100.0, "Descrição do Produto Teste");

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        produtoService.deletarProduto(1L);

        verify(produtoRepository, times(1)).findById(1L);
        verify(produtoRepository, times(1)).delete(produto);
    }
}
