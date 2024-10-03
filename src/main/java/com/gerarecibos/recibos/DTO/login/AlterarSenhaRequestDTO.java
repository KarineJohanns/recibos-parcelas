package com.gerarecibos.recibos.DTO.login;

import lombok.Data;

@Data
public class AlterarSenhaRequestDTO {
    private Long clienteId;
    private String senhaAtual;
    private String novaSenha;
}
