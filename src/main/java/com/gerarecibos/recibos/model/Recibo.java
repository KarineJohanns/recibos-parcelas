package com.gerarecibos.recibos.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Data
@NoArgsConstructor
public class Recibo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "parcela_id", nullable = false)
    private Parcela parcela;

    private String emitente;

    @Column(length = 2000)  // Define um tamanho máximo para o conteúdo
    private String conteudo;

    // Getters e Setters gerados automaticamente pelo Lombok
}