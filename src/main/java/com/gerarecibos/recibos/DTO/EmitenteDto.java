package com.gerarecibos.recibos.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmitenteDto {

    private String emitenteNome;
    private String emitenteCpf; // Caso precise de CPF para o emitente
    private String emitenteEndereco; // Caso precise de endereço para o emitente
    private String emitenteTelefone; // Caso precise de telefone para o emitente


}