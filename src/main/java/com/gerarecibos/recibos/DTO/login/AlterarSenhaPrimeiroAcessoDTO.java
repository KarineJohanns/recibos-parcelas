package com.gerarecibos.recibos.DTO.login;

import lombok.Data;

@Data
public class AlterarSenhaPrimeiroAcessoDTO {
    private String cpf;         // CPF do cliente
    private String senhaAtual;  // Senha temporária
    private String novaSenha;   // Nova senha desejada

}
