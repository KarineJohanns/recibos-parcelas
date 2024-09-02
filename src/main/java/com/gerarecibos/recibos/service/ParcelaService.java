package com.gerarecibos.recibos.service;

import com.gerarecibos.recibos.DTO.ParcelaDto;
import com.gerarecibos.recibos.DTO.ParcelaResponseDto;
import com.gerarecibos.recibos.DTO.EscolhaDto;
import com.gerarecibos.recibos.Exceptions.ResourceNotFoundException;
import com.gerarecibos.recibos.model.*;
import com.gerarecibos.recibos.repository.ClienteRepository;
import com.gerarecibos.recibos.repository.ParcelaRepository;
import com.gerarecibos.recibos.repository.EmitenteRepository;
import com.gerarecibos.recibos.repository.ProdutoRepository;
import com.gerarecibos.recibos.repository.ReciboRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ParcelaService {

    @Autowired
    private ParcelaRepository parcelaRepository;

    @Autowired
    private ReciboRepository reciboRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EmitenteService emitenteService;

    @Autowired
    private EmitenteRepository emitenteRepository;

    public List<Parcela> criarParcelas(ParcelaDto parcelaDto) {
        Cliente cliente = obterOuCriarCliente(parcelaDto);
        Produto produto = obterOuCriarProduto(parcelaDto);
        Emitente emitente = obterEmitente(parcelaDto.getEmitenteId());

        List<Parcela> parcelas = new ArrayList<>();
        double valorParcela = parcelaDto.getValorTotalProduto() / parcelaDto.getNumeroParcelas();
        // Usa a data fornecida diretamente
        LocalDate dataVencimento = parcelaDto.getDataCriacao();
        LocalDate dataCriacao = parcelaDto.getDataCriacao();

        for (int i = 1; i <= parcelaDto.getNumeroParcelas(); i++) {
            Parcela parcela = new Parcela();
            parcela.setCliente(cliente);
            parcela.setProduto(produto);
            parcela.setNumeroParcelas(parcelaDto.getNumeroParcelas());
            parcela.setValorParcela(valorParcela);
            parcela.setEmitente(emitente);
            parcela.setDataCriacao(dataCriacao); // Define a data de criação da parcela
            parcela.setDataVencimento(dataVencimento); // Inicialmente igual à data de criação
            parcela.setNumeroParcela(i); // Define o número da parcela
            parcela.setIntervalo((parcelaDto.getIntervalo()));

            parcelas.add(parcela);

            // Calcula a próxima data de pagamento
            dataVencimento = calcularProximaDataVencimento(dataVencimento, parcelaDto.getIntervalo());
        }

        return parcelaRepository.saveAll(parcelas);
    }

    private Cliente obterOuCriarCliente(ParcelaDto parcelaDto) {
        if (parcelaDto.getClienteId() != null) {
            return clienteRepository.findById(parcelaDto.getClienteId())
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        } else {
            Cliente cliente = new Cliente();
            cliente.setClienteNome(parcelaDto.getNomeCliente());
            return clienteRepository.save(cliente);
        }
    }

    private Produto obterOuCriarProduto(ParcelaDto parcelaDto) {
        if (parcelaDto.getProdutoId() != null) {
            return produtoRepository.findById(parcelaDto.getProdutoId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        } else {
            Produto produto = new Produto();
            produto.setProdutoNome(parcelaDto.getNomeProduto());
            produto.setProdutoValorTotal(parcelaDto.getValorTotalProduto());
            return produtoRepository.save(produto);
        }
    }

    private Emitente obterEmitente(Long emitenteId) {
        if (emitenteId != null) {
            return emitenteService.obterEmitentePorId(emitenteId);
        }
        return null;
    }

    private LocalDate calcularDataInicial(Integer diaPagamento) {
        LocalDate dataAtual = LocalDate.now();
        if (diaPagamento != null) {
            return dataAtual.withDayOfMonth(diaPagamento);
        }
        return dataAtual;
    }

    private LocalDate calcularProximaDataVencimento(LocalDate dataAtual, String intervalo) {
        LocalDate proximaData;

        if (dataAtual == null) {
            dataAtual = LocalDate.now(); // Inicializa com a data atual
        }
        switch (intervalo != null ? intervalo.toUpperCase() : "MENSAL") { // Usa "MENSAL" como padrão se o intervalo for nulo
            case "SEMANAL":
                proximaData = dataAtual.plusWeeks(1);
                break;
            case "MENSAL":
                proximaData = dataAtual.plusMonths(1);
                break;
            case "BIMESTRAL":
                proximaData = dataAtual.plusMonths(2);
                break;
            case "TRIMESTRAL":
                proximaData = dataAtual.plusMonths(3);
                break;
            default:
                throw new IllegalArgumentException("Intervalo inválido: " + intervalo);
        }

        return proximaData;
    }

    public Parcela obterParcelaPorId(Long id) {
        return parcelaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parcela não encontrada"));
    }

    public List<Parcela> listarTodasParcelas() {
        return parcelaRepository.findAll();
    }

    public Parcela editarParcela(Long id, ParcelaDto parcelaDto) {
        Parcela parcela = obterParcelaPorId(id);
        parcela.setValorParcela(parcelaDto.getValorTotalProduto() / parcelaDto.getNumeroParcelas());
        return parcelaRepository.save(parcela);
    }

    public void deletarParcela(Long id) {
        Parcela parcela = obterParcelaPorId(id);
        parcelaRepository.delete(parcela);
    }

    public ParcelaResponseDto pagarParcela(Long id, Double valorPago, LocalDate dataPagamento, Boolean gerarNovasParcelas, Double desconto) {
        Parcela parcela = parcelaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parcela não encontrada"));

        double valorTotalParcela = parcela.getValorParcela();
        double valorRestante = valorTotalParcela - valorPago;

        ParcelaResponseDto responseDto = new ParcelaResponseDto();
        responseDto.setParcelaId(parcela.getParcelaId());

        if (valorPago < valorTotalParcela) {
            parcela.setPaga(true);
            parcela.setValorPago(valorPago);
            parcela.setDataPagamento(dataPagamento); // Atualiza a data de pagamento
            parcelaRepository.save(parcela);

            gerarRecibo(parcela, valorPago, dataPagamento, true, parcela.getEmitente().getEmitenteId());

            responseDto.setEscolhaNecessaria(true);
            responseDto.setMensagem("O valor pago é menor que o valor total da parcela. Escolha entre gerar novas parcelas ou aplicar um desconto.");
        } else {
            parcela.setPaga(true);
            parcela.setValorPago(valorPago);
            parcela.setDataPagamento(dataPagamento); // Atualiza a data de pagamento
            parcelaRepository.save(parcela);

            gerarRecibo(parcela, valorPago, dataPagamento, false, parcela.getEmitente().getEmitenteId());

            responseDto.setPaga(true);
            responseDto.setEscolhaNecessaria(false);
            responseDto.setMensagem("Parcela paga completamente.");
        }

        return responseDto;
    }

    private void criarNovasParcelas(Parcela parcelaOriginal, Double valorRestante, int numeroParcelas, String novoIntervalo, LocalDate dataPrimeiraParcela) {
        Cliente cliente = parcelaOriginal.getCliente();
        Produto produto = parcelaOriginal.getProduto();

        // Usa o novo intervalo se fornecido, caso contrário, usa o intervalo original
        String intervalo = (novoIntervalo != null) ? novoIntervalo : parcelaOriginal.getIntervalo();

        // Se uma data específica para a primeira parcela for fornecida, usa essa data; caso contrário, usa a data de vencimento da parcela original
        LocalDate dataVencimento = (dataPrimeiraParcela != null) ? dataPrimeiraParcela : parcelaOriginal.getDataVencimento();

        // Calcula o valor de cada nova parcela
        double valorParcela = valorRestante / numeroParcelas;

        // Lista para armazenar as novas parcelas
        List<Parcela> novasParcelas = new ArrayList<>();

        for (int i = 1; i <= numeroParcelas; i++) {
            Parcela novaParcela = new Parcela();
            novaParcela.setCliente(cliente);
            novaParcela.setProduto(produto);
            novaParcela.setNumeroParcelas(numeroParcelas); // Número total de parcelas
            novaParcela.setValorParcela(valorParcela); // Valor de cada parcela
            novaParcela.setIntervalo(intervalo); // Define o intervalo
            novaParcela.setDataCriacao(LocalDate.now()); // Define a data de criação
            novaParcela.setDataVencimento(dataVencimento); // Define a data de vencimento inicial
            novaParcela.setNumeroParcela(i); // Define o número da parcela
            novaParcela.setParcelaOriginal(parcelaOriginal);
            novaParcela.setEmitente(parcelaOriginal.getEmitente());

            // Adiciona a nova parcela à lista
            novasParcelas.add(novaParcela);

            // Calcula a próxima data de vencimento com base no intervalo
            dataVencimento = calcularProximaDataVencimento(dataVencimento, intervalo);
        }

        // Salva todas as novas parcelas no banco de dados
        parcelaRepository.saveAll(novasParcelas);
    }

    public void aplicarDesconto(Parcela parcela, Double valorRestante) {
        // Calcula o desconto como a diferença entre o valor total da parcela e o valor pago
        double desconto = valorRestante;

        // Aplica o desconto ao valor restante da parcela
        //parcela.setValorParcela(parcela.getValorParcela() - desconto);

        // Marcar como paga (ou parcialmente paga, dependendo da lógica)
        if (parcela.getValorParcela() == 0) {
            parcela.setPaga(true);
        }

        parcelaRepository.save(parcela);
    }

    private void gerarRecibo(Parcela parcela, Double valorPago, LocalDate dataPagamento, Boolean parcial, Long emitenteId) {
        Recibo recibo = new Recibo();
        // O ID do recibo será o mesmo que o ID da parcela
        recibo.setId(parcela.getParcelaId());
        recibo.setParcela(parcela);

        // Recuperar emitente do banco de dados
        Emitente emitente = emitenteRepository.findById(emitenteId)
                .orElseThrow(() -> new RuntimeException("Emitente não encontrado"));

        recibo.setEmitente(emitente);

        if (parcial) {
            recibo.setConteudo("Recibo parcial da parcela " + parcela.getParcelaId() + " no valor de " + valorPago + " pago na data " + dataPagamento);
        } else {
            recibo.setConteudo("Recibo completo da parcela " + parcela.getParcelaId() + " no valor total de " + parcela.getValorParcela() + " pago na data " + dataPagamento);
        }

        reciboRepository.save(recibo);
    }

    public Parcela processarEscolha(Long id, EscolhaDto escolhaDto) {
        // Recupera a parcela original a partir do ID
        Parcela parcela = parcelaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parcela não encontrada"));

        // Calcula o valor restante a ser pago
        double valorRestante = parcela.getValorParcela() - parcela.getValorPago();

        // Marca a parcela original como paga
        parcela.setPaga(true);
        parcelaRepository.save(parcela); // Salva a alteração no banco de dados

        gerarRecibo(parcela, parcela.getValorPago(), parcela.getDataPagamento(), false, parcela.getEmitente().getEmitenteId());

        // Verifica se novas parcelas devem ser geradas
        if (Boolean.TRUE.equals(escolhaDto.getGerarNovasParcelas())) {
            int numeroParcelas = escolhaDto.getNumeroParcelasRenegociacao() != null
                    ? escolhaDto.getNumeroParcelasRenegociacao()
                    : 1;
            String novoIntervalo = escolhaDto.getNovoIntervalo() != null ? escolhaDto.getNovoIntervalo() : parcela.getIntervalo();
            LocalDate dataPrimeiraParcela = escolhaDto.getDataPrimeiraParcela();  // Novo campo

            criarNovasParcelas(parcela, valorRestante, numeroParcelas, novoIntervalo, dataPrimeiraParcela);
        } else {
            aplicarDesconto(parcela, valorRestante);
        }

        return parcela;
    }

}
