package com.gerarecibos.recibos.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Parcela {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long parcelaId;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    private Integer numeroParcelas;
    private Double valorParcela;
    private Boolean paga = false;
    private Double valorPago;
    private Double descontoAplicado = 0.0;

    @ManyToOne
    @JoinColumn(name = "emitente_id")
    private Emitente emitente;

    @OneToMany(mappedBy = "parcelaOriginal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Parcela> novasParcelas = new ArrayList<>();

    @ToString.Exclude
    @ManyToOne
    private Parcela parcelaOriginal;
    private Integer numeroParcela; // Adicionado para armazenar o número da parcela


    private String intervalo;  // "MENSAL", "SEMANAL", etc.

    private LocalDate dataCriacao; // Data de criação da parcela
    private LocalDate dataVencimento; // Data de vencimento da parcela
    private LocalDate dataPagamento; // Data de pagamento da parcela (se houver)
}
