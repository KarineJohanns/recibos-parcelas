package com.gerarecibos.recibos.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Recibo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "emitente_id") // Referência ao emitente
    private Emitente emitente;

    private String conteudo;

    @ManyToOne
    @JoinColumn(name = "parcela_id") // Referência à parcela
    private Parcela parcela;
}
