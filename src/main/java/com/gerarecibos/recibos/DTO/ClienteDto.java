package com.gerarecibos.recibos.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ClienteDto {

    private String clienteNome;
    private String clienteCpf;
    private String clienteEndereco;
    private String clienteTelefone;


}