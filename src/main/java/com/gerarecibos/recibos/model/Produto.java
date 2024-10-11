package com.gerarecibos.recibos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long produtoId;

    private String produtoNome;

    private Integer produtoValorTotal;

    @Column(length = 500)
    private String produtoDescricao; // Novo campo para descrição
}
