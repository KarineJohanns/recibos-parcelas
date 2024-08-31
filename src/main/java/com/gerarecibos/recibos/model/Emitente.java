package com.gerarecibos.recibos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data // Gera getters, setters, toString, equals e hashCode
@NoArgsConstructor // Gera um construtor sem argumentos
@AllArgsConstructor // Gera um construtor com todos os argumentos
public class Emitente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long emitenteId;

    @Column(name = "nome", nullable = false)
    private String emitenteNome;

    @Column(name = "cpf", nullable = false, unique = true)
    private String emitenteCpf;

    @Column(name = "endereco")
    private String emitenteEndereco;

    @Column(name = "telefone")
    private String emitenteTelefone;
}