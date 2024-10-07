package com.gerarecibos.recibos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Recibo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reciboId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "emitente_id") // Referência ao emitente
    private Emitente emitente;

    private String conteudo;

    @JsonIgnore
    @OneToOne // Agora referenciando corretamente como um relacionamento um-para-um
    @JoinColumn(name = "parcela_id") // Referência à parcela
    private Parcela parcela;

    @JsonIgnore
    @Transient
    private Cliente cliente;

    // Novo campo para armazenar a URI ou caminho do arquivo
    private String uri; // URI do arquivo de recibo no servidor


    @PostLoad
    private void populateCliente() {
        if (parcela != null) {
            this.cliente = parcela.getCliente();
        }
    }
}
