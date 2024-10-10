package com.gerarecibos.recibos.DTO.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlterarSenhaRequestDTO {
    private Long clienteId;
    private String senhaAtual; // Senha atual (temporária ou anterior)
    private String novaSenha; // Nova senha
}
