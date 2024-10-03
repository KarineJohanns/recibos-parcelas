package com.gerarecibos.recibos.DTO.login;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String cpf;
    private String senha;
}
