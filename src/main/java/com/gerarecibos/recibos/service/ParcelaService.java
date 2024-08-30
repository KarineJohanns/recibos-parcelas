package com.gerarecibos.recibos.service;

import com.gerarecibos.recibos.DTO.ParcelaDto;
import com.gerarecibos.recibos.model.Cliente;
import com.gerarecibos.recibos.model.Parcela;
import com.gerarecibos.recibos.model.Produto;
import com.gerarecibos.recibos.model.Recibo;
import com.gerarecibos.recibos.repository.ClienteRepository;
import com.gerarecibos.recibos.repository.ParcelaRepository;
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

    public List<Parcela> criarParcelas(ParcelaDto parcelaDto) {
        Cliente cliente;
        if (parcelaDto.getClienteId() != null) {
            cliente = clienteRepository.findById(parcelaDto.getClienteId())
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        } else {
            // Cria um novo cliente se o ID não for fornecido
            cliente = new Cliente();
            cliente.setNome(parcelaDto.getNomeCliente()); // Supondo que o DTO tenha um campo para o nome do cliente
            cliente = clienteRepository.save(cliente);
        }
        Produto produto;
        if (parcelaDto.getProdutoId() != null) {
            produto = produtoRepository.findById(parcelaDto.getProdutoId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        } else {
            produto = new Produto();
            produto.setNome(parcelaDto.getNomeProduto()); // Nome do produto fornecido no DTO
            produto.setValorTotal(parcelaDto.getValorTotalProduto()); // Valor total fornecido no DTO
            produto = produtoRepository.save(produto);
        }

        List<Parcela> parcelas = new ArrayList<>();
        double valorParcela = parcelaDto.getValorTotalProduto() / parcelaDto.getNumeroParcelas();

        for (int i = 1; i <= parcelaDto.getNumeroParcelas(); i++) {
            Parcela parcela = new Parcela();
            parcela.setCliente(cliente);
            parcela.setProduto(produto);
            parcela.setNumeroParcelas(parcelaDto.getNumeroParcelas());
            parcela.setValorParcela(valorParcela);
            parcelas.add(parcela);
        }

        return parcelaRepository.saveAll(parcelas);
    }

    public List<Parcela> listarParcelas() {
        return parcelaRepository.findAll();
    }

    public Parcela pagarParcela(Long id, Double valorPago, LocalDate dataPagamento) {
        Parcela parcela = parcelaRepository.findById(id).orElseThrow(() -> new RuntimeException("Parcela não encontrada"));
        parcela.setPaga(true);
        parcela.setValorPago(valorPago);
        parcela.setDataPagamento(dataPagamento);
        parcelaRepository.save(parcela);

        // Gera o recibo automaticamente após o pagamento
        gerarRecibo(parcela);

        return parcela;
    }

    private void gerarRecibo(Parcela parcela) {
        Recibo recibo = new Recibo();
        recibo.setParcela(parcela);
        recibo.setEmitente("Nome do Emitente");
        recibo.setConteudo("Recibo da parcela " + parcela.getId() + " no valor de " + parcela.getValorPago() + " pago na data " + parcela.getDataPagamento());

        reciboRepository.save(recibo);
    }
}