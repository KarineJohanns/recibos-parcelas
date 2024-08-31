package com.gerarecibos.recibos.service;

import com.gerarecibos.recibos.DTO.ParcelaDto;
import com.gerarecibos.recibos.DTO.ParcelaResponseDto;
import com.gerarecibos.recibos.DTO.EscolhaDto;
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
        Cliente cliente;
        if (parcelaDto.getClienteId() != null) {
            cliente = clienteRepository.findById(parcelaDto.getClienteId())
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        } else {
            // Cria um novo cliente se o ID não for fornecido
            cliente = new Cliente();
            cliente.setClienteNome(parcelaDto.getNomeCliente()); // Supondo que o DTO tenha um campo para o nome do cliente
            cliente = clienteRepository.save(cliente);
        }

        Produto produto;
        if (parcelaDto.getProdutoId() != null) {
            produto = produtoRepository.findById(parcelaDto.getProdutoId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        } else {
            produto = new Produto();
            produto.setProdutoNome(parcelaDto.getNomeProduto()); // Nome do produto fornecido no DTO
            produto.setProdutoValorTotal(parcelaDto.getValorTotalProduto()); // Valor total fornecido no DTO
            produto = produtoRepository.save(produto);
        }

        Emitente emitente = null;
        if (parcelaDto.getEmitenteId() != null) {
            emitente = emitenteService.obterEmitentePorId(parcelaDto.getEmitenteId());
        }

        List<Parcela> parcelas = new ArrayList<>();
        double valorParcela = parcelaDto.getValorTotalProduto() / parcelaDto.getNumeroParcelas();

        for (int i = 1; i <= parcelaDto.getNumeroParcelas(); i++) {
            Parcela parcela = new Parcela();
            parcela.setCliente(cliente);
            parcela.setProduto(produto);
            parcela.setNumeroParcelas(parcelaDto.getNumeroParcelas());
            parcela.setValorParcela(valorParcela);
            parcela.setEmitente(emitente);
            parcelas.add(parcela);
        }

        return parcelaRepository.saveAll(parcelas);
    }

    public List<Parcela> listarParcelas() {
        return parcelaRepository.findAll();
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

    private void criarNovasParcelas(Parcela parcelaOriginal, Double valorRestante) {
        Cliente cliente = parcelaOriginal.getCliente();
        Produto produto = parcelaOriginal.getProduto();
        int numeroParcelas = parcelaOriginal.getNumeroParcelas();

        // Cria novas parcelas para o valor restante
        List<Parcela> novasParcelas = new ArrayList<>();
        double valorParcela = valorRestante / (numeroParcelas + 1); // Distribuindo o restante

        for (int i = 1; i <= numeroParcelas; i++) {
            Parcela novaParcela = new Parcela();
            novaParcela.setCliente(cliente);
            novaParcela.setProduto(produto);
            novaParcela.setNumeroParcelas(numeroParcelas + 1); // Atualiza o número de parcelas
            novaParcela.setValorParcela(valorParcela);
            novaParcela.setParcelaOriginal(parcelaOriginal);
            novasParcelas.add(novaParcela);
        }

        parcelaRepository.saveAll(novasParcelas);
        parcelaOriginal.getNovasParcelas().addAll(novasParcelas);
        parcelaRepository.save(parcelaOriginal);
    }

    private void aplicarDesconto(Parcela parcela, Double valorRestante) {
        // Calcula o desconto como a diferença entre o valor total da parcela e o valor pago
        double desconto = valorRestante;

        // Aplica o desconto ao valor restante da parcela
        parcela.setValorParcela(parcela.getValorParcela() - desconto);

        // Marcar como paga (ou parcialmente paga, dependendo da lógica)
        if (parcela.getValorParcela() == 0) {
            parcela.setPaga(true);
        }

        parcelaRepository.save(parcela);
    }


    private void gerarRecibo(Parcela parcela, Double valorPago, LocalDate dataPagamento, Boolean parcial, Long emitenteId) {
        Recibo recibo = new Recibo();
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
        Parcela parcela = parcelaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parcela não encontrada"));

        double valorRestante = parcela.getValorParcela() - parcela.getValorPago();

        if (escolhaDto.getGerarNovasParcelas() != null && escolhaDto.getGerarNovasParcelas()) {
            int numeroParcelas = escolhaDto.getNumeroParcelasRenegociacao() != null
                    ? escolhaDto.getNumeroParcelasRenegociacao()
                    : 1; // Valor padrão se o cliente não especificar

            criarNovasParcelas(parcela, valorRestante, numeroParcelas);
        } else {
            aplicarDesconto(parcela, valorRestante);
        }

        return parcela;
    }
    private void criarNovasParcelas(Parcela parcelaOriginal, Double valorRestante, int numeroParcelas) {
        Cliente cliente = parcelaOriginal.getCliente();
        Produto produto = parcelaOriginal.getProduto();

        // Cria novas parcelas para o valor restante
        List<Parcela> novasParcelas = new ArrayList<>();
        double valorParcela = valorRestante / numeroParcelas; // Distribuindo o restante

        for (int i = 1; i <= numeroParcelas; i++) {
            Parcela novaParcela = new Parcela();
            novaParcela.setCliente(cliente);
            novaParcela.setProduto(produto);
            novaParcela.setNumeroParcelas(numeroParcelas);
            novaParcela.setValorParcela(valorParcela);
            novaParcela.setParcelaOriginal(parcelaOriginal);
            novasParcelas.add(novaParcela);
        }

        parcelaRepository.saveAll(novasParcelas);
        parcelaOriginal.getNovasParcelas().addAll(novasParcelas);
        parcelaRepository.save(parcelaOriginal);
    }
}
