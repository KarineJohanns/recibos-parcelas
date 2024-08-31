package com.gerarecibos.recibos.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Cliente cliente; // Nome atualizado para seguir o padrão

    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto; // Nome atualizado para seguir o padrão

    private Integer numeroParcelas;
    private Double valorParcela;
    private Boolean paga = false;
    private LocalDate dataPagamento;
    private Double valorPago;
    private Double descontoAplicado = 0.0;

    @ManyToOne
    @JoinColumn(name = "emitente_id") // Verifique se a coluna existe na tabela de Parcelas
    private Emitente emitente;

    @OneToMany(mappedBy = "parcelaOriginal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Parcela> novasParcelas = new ArrayList<>(); // Lista de novas parcelas, se necessário

    @ManyToOne
    private Parcela parcelaOriginal; // Referência para parcela original para novas parcelas
}
