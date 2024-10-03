package com.gerarecibos.recibos.DTO.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private boolean primeiroAcesso; // Indica se é o primeiro acesso do usuário
    private String senhaTemporaria;
    private String token;
}