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
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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

    @Transactional
    public List<Parcela> criarParcelas(ParcelaDto parcelaDto) {
        Cliente cliente = obterOuCriarCliente(parcelaDto);
        Produto produto = obterOuCriarProduto(parcelaDto);
        Emitente emitente = obterEmitente(parcelaDto.getEmitenteId());

        List<Parcela> parcelas = new ArrayList<>();
        int valorTotalProduto = parcelaDto.getValorTotalProduto();
        int numeroParcelas = parcelaDto.getNumeroParcelas();

        // Calcula o valor da parcela, arredondando corretamente
        int valorParcela = valorTotalProduto / numeroParcelas;
        int resto = valorTotalProduto % numeroParcelas;

        // Usa a data de vencimento fornecida ou a data de criação como ponto de partida
        LocalDate dataVencimento = parcelaDto.getDataVencimento() != null
                ? parcelaDto.getDataVencimento()
                : parcelaDto.getDataCriacao();

        LocalDate dataCriacao = parcelaDto.getDataCriacao();

        for (int i = 1; i <= numeroParcelas; i++) {
            Parcela parcela = new Parcela();
            parcela.setCliente(cliente);
            parcela.setProduto(produto);
            parcela.setNumeroParcelas(numeroParcelas);
            parcela.setValorParcela(valorParcela);
            parcela.setEmitente(emitente);
            parcela.setDataCriacao(dataCriacao);
            parcela.setDataVencimento(dataVencimento);
            parcela.setNumeroParcela(i);
            parcela.setIntervalo(parcelaDto.getIntervalo());

            // Define o documento da parcela
            String documento = parcelaDto.getDocumento() != null ? parcelaDto.getDocumento() : "";
            documento += " " + i + "/" + numeroParcelas;
            parcela.setDocumento(documento);

            parcelas.add(parcela);

            // Se houver um resto, adiciona 1 à última parcela
            if (i == numeroParcelas && resto > 0) {
                parcela.setValorParcela(valorParcela + resto);
            }

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
        parcela.setValorParcela(parcelaDto.getValorParcela());
        parcela.setDataVencimento(parcelaDto.getDataVencimento());
        return parcelaRepository.save(parcela);
    }

    public void deletarParcela(Long id) {
        Parcela parcela = obterParcelaPorId(id);
        parcelaRepository.delete(parcela);
    }

    public ParcelaResponseDto pagarParcela(Long id, Integer valorPago, LocalDate dataPagamento, Boolean gerarNovasParcelas, Integer desconto) {
        Parcela parcela = parcelaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parcela não encontrada"));

        int valorTotalParcela = parcela.getValorParcela();
        int valorRestante = valorTotalParcela - valorPago;

        ParcelaResponseDto responseDto = new ParcelaResponseDto();
        responseDto.setParcelaId(parcela.getParcelaId());

        if (valorPago < valorTotalParcela) {
            parcela.setPaga(true);
            parcela.setValorPago(valorPago);
            parcela.setDataPagamento(dataPagamento);
            parcelaRepository.save(parcela);

            gerarRecibo(parcela, valorPago, dataPagamento, true, parcela.getEmitente().getEmitenteId());

            responseDto.setEscolhaNecessaria(true);
            responseDto.setMensagem("O valor pago é menor que o valor total da parcela. Escolha entre gerar novas parcelas ou aplicar um desconto.");
        } else {
            parcela.setPaga(true);
            parcela.setValorPago(valorPago);
            parcela.setDataPagamento(dataPagamento);
            parcelaRepository.save(parcela);

            gerarRecibo(parcela, valorPago, dataPagamento, false, parcela.getEmitente().getEmitenteId());

            responseDto.setPaga(true);
            responseDto.setEscolhaNecessaria(false);
            responseDto.setMensagem("Parcela paga completamente.");
        }

        return responseDto;
    }

    public ParcelaResponseDto criarNovasParcelas(Parcela parcelaOriginal, int valorRestante, int numeroParcelas, String novoIntervalo, LocalDate dataPrimeiraParcela) {
        ParcelaResponseDto responseDto = new ParcelaResponseDto();

        Cliente cliente = parcelaOriginal.getCliente();
        Produto produto = parcelaOriginal.getProduto();

        // Usa o novo intervalo se fornecido, caso contrário, usa o intervalo original
        String intervalo = (novoIntervalo != null) ? novoIntervalo : parcelaOriginal.getIntervalo();

        // Se uma data específica para a primeira parcela for fornecida, usa essa data; caso contrário, usa a data de vencimento da parcela original
        LocalDate dataVencimento = (dataPrimeiraParcela != null) ? dataPrimeiraParcela : parcelaOriginal.getDataVencimento();

        // Calcula o valor de cada nova parcela, arredondando corretamente
        int valorParcela = valorRestante / numeroParcelas;
        int resto = valorRestante % numeroParcelas;

        // Lista para armazenar as novas parcelas
        List<Parcela> novasParcelas = new ArrayList<>();

        for (int i = 1; i <= numeroParcelas; i++) {
            Parcela novaParcela = new Parcela();
            novaParcela.setCliente(cliente);
            novaParcela.setProduto(produto);
            novaParcela.setNumeroParcelas(numeroParcelas);
            novaParcela.setValorParcela(valorParcela);
            novaParcela.setIntervalo(intervalo);
            novaParcela.setDataCriacao(LocalDate.now());
            novaParcela.setDataVencimento(dataVencimento);
            novaParcela.setNumeroParcela(i);
            novaParcela.setParcelaOriginal(parcelaOriginal);
            novaParcela.setEmitente(parcelaOriginal.getEmitente());

            // Define o documento da parcela renegociada
            String documento = "RENEG" + parcelaOriginal.getParcelaId() + "-" + i + "/" + numeroParcelas;
            novaParcela.setDocumento(documento);

            // Adiciona o resto à última parcela
            if (i == numeroParcelas && resto > 0) {
                novaParcela.setValorParcela(valorParcela + resto);
            }

            // Adiciona a nova parcela à lista
            novasParcelas.add(novaParcela);

            // Calcula a próxima data de vencimento com base no intervalo
            dataVencimento = calcularProximaDataVencimento(dataVencimento, intervalo);
        }

        // Salva todas as novas parcelas no banco de dados
        parcelaRepository.saveAll(novasParcelas);

        // Preenche o responseDto com as informações adequadas
        responseDto.setMensagem("Novas parcelas criadas com sucesso.");

        return responseDto;
    }

    public ParcelaResponseDto aplicarDesconto(Parcela parcela, Integer valorRestante) {
        ParcelaResponseDto responseDto = new ParcelaResponseDto();

        // O valor do desconto é a diferença entre o valor original da parcela e o valor pago
        Integer desconto = parcela.getValorParcela() - valorRestante;

        // Atualiza o valor pago e o desconto aplicado na parcela
        parcela.setValorPago(valorRestante);
        parcela.setDescontoAplicado(desconto);

        // Marca a parcela como paga se o valor pago for igual ou superior ao valor da parcela
        if (valorRestante >= parcela.getValorParcela()) {
            parcela.setPaga(true);
        }

        parcelaRepository.save(parcela);

        // Preenche o responseDto com as informações adequadas
        responseDto.setPaga(parcela.getPaga());
        responseDto.setMensagem("Desconto aplicado com sucesso. A parcela foi paga.");

        return responseDto;
    }

    private void gerarRecibo(Parcela parcela, long valorPago, LocalDate dataPagamento, Boolean parcial, Long emitenteId) {
        Recibo recibo = new Recibo();
        // O ID do recibo será o mesmo que o ID da parcela
        recibo.setId(parcela.getParcelaId());
        recibo.setParcela(parcela);

        // Recuperar emitente do banco de dados
        Emitente emitente = emitenteRepository.findById(emitenteId)
                .orElseThrow(() -> new RuntimeException("Emitente não encontrado"));

        recibo.setEmitente(emitente);

        if (parcial) {
            recibo.setConteudo("Recibo parcial da parcela " + parcela.getParcelaId() + " no valor de " + valorPago / 100.0 + " pago na data " + dataPagamento);
        } else {
            recibo.setConteudo("Recibo completo da parcela " + parcela.getParcelaId() + " no valor total de " + parcela.getValorParcela() / 100.0 + " pago na data " + dataPagamento);
        }

        reciboRepository.save(recibo);
    }

    public ParcelaResponseDto processarEscolha(Long id, EscolhaDto escolhaDto) {
        // Recupera a parcela original a partir do ID
        Parcela parcela = parcelaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parcela não encontrada"));

        // Calcula o valor restante a ser pago como um valor inteiro (em centavos)
        int valorRestante = parcela.getValorParcela() - parcela.getValorPago();

        // Marca a parcela original como paga
        parcela.setPaga(true);
        parcela.setRenegociada(true);
        parcelaRepository.save(parcela); // Salva a alteração no banco de dados

        gerarRecibo(parcela, parcela.getValorPago(), parcela.getDataPagamento(), false, parcela.getEmitente().getEmitenteId());

        ParcelaResponseDto responseDto = new ParcelaResponseDto();

        // Verifica se novas parcelas devem ser geradas
        if (Boolean.TRUE.equals(escolhaDto.getGerarNovasParcelas())) {
            int numeroParcelas = escolhaDto.getNumeroParcelasRenegociacao() != null
                    ? escolhaDto.getNumeroParcelasRenegociacao()
                    : 1;
            String novoIntervalo = escolhaDto.getNovoIntervalo() != null ? escolhaDto.getNovoIntervalo() : parcela.getIntervalo();
            LocalDate dataPrimeiraParcela = escolhaDto.getDataPrimeiraParcela();  // Novo campo

            // Ajusta a criação de novas parcelas para tratar valores em centavos
            criarNovasParcelas(parcela, valorRestante, numeroParcelas, novoIntervalo, dataPrimeiraParcela);

            responseDto.setMensagem("Novas parcelas foram criadas com sucesso.");
        } else {
            // Aplicar desconto se novas parcelas não forem geradas
            responseDto = aplicarDesconto(parcela, valorRestante);
        }

        return responseDto;
    }


    public void desfazerPagamento(Long id) throws EntityNotFoundException {
        Parcela parcela = parcelaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Parcela não encontrada"));

        // Verifica se a parcela está marcada como paga e, se estiver, a marca como não paga
        if (parcela.isPaga()) {
            parcela.setPaga(false);
            parcela.setDataPagamento(null); // Removendo a data de pagamento, se necessário
            parcela.setValorPago(0); // Revertendo o valor pago, se necessário
            parcelaRepository.save(parcela);
        }
    }
}
