package com.gerarecibos.recibos.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clienteId;

    private String clienteNome;

    @Column(unique = true)
    private String clienteCpf;
    private String clienteEndereco;
    private String clienteTelefone;

}