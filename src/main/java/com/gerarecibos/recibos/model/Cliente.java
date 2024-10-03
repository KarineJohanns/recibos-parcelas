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

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false)
    private Boolean primeiroAcesso = true; // Por padrão, assume-se que é o primeiro login


    // Método isPrimeiroAcesso para conveniência
    public Boolean isPrimeiroAcesso() {
        return primeiroAcesso;
    }
}