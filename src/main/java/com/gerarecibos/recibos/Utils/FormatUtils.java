package com.gerarecibos.recibos.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;
import javax.swing.text.MaskFormatter;

public class FormatUtils {

    // Método para formatar o CPF ou CNPJ
    public static String formatCpfCnpj(String cpfCnpj) {
        // Remover todos os caracteres não numéricos
        String cleanedCpfCnpj = cpfCnpj.replaceAll("\\D", "");

        if (cleanedCpfCnpj.length() == 11) {
            // Formatar como CPF
            return formatString(cleanedCpfCnpj, "###.###.###-##");
        } else if (cleanedCpfCnpj.length() == 14) {
            // Formatar como CNPJ
            return formatString(cleanedCpfCnpj, "##.###.###/####-##");
        } else {
            throw new IllegalArgumentException("Número de CPF ou CNPJ inválido");
        }
    }

    // Método utilitário para aplicar a máscara de formatação
    private static String formatString(String value, String mask) {
        try {
            MaskFormatter maskFormatter = new MaskFormatter(mask);
            maskFormatter.setValueContainsLiteralCharacters(false);
            return maskFormatter.valueToString(value);
        } catch (ParseException e) {
            throw new RuntimeException("Erro ao formatar CPF ou CNPJ", e);
        }
    }

    public static void main(String[] args) {
        // Exemplos de uso
        String cpf = "12345678901";
        String cnpj = "12345678000195";

        System.out.println("CPF Formatado: " + formatCpfCnpj(cpf));
        System.out.println("CNPJ Formatado: " + formatCpfCnpj(cnpj));
    }
}
