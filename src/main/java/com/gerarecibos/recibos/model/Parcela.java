package com.gerarecibos.recibos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "parcela")
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
    private Integer valorParcela;
    private Boolean paga = false;
    private Integer valorPago;
    private Integer descontoAplicado = 0;

    @ManyToOne
    @JoinColumn(name = "emitente_id")
    private Emitente emitente;

    @OneToMany(mappedBy = "parcelaOriginal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Parcela> novasParcelas = new ArrayList<>();

    @JsonIgnore
    @ToString.Exclude
    @ManyToOne
    private Parcela parcelaOriginal;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean renegociada = false;
    private Integer numeroParcela; // Adicionado para armazenar o número da parcela


    private String intervalo;  // "MENSAL", "SEMANAL", etc.

    private LocalDate dataCriacao; // Data de criação da parcela
    private LocalDate dataVencimento; // Data de vencimento da parcela
    private LocalDate dataPagamento; // Data de pagamento da parcela (se houver)

    @Size(max = 512)
    private String documento;

    @OneToOne // Relação um-para-um com Recibo
    @JoinColumn(name = "recibo_id") // Nome da coluna na tabela parcela
    private Recibo recibo;

    public boolean isPaga() {
        return paga;
    }

    public void setPaga(boolean paga) {
        this.paga = paga;
    }
}
