package com.gerarecibos.recibos.Utils;
import java.util.Arrays;
import java.util.List;

public class NumeroPorExtenso {

    private static final List<String> UNIDADES = Arrays.asList("", "um", "dois", "três", "quatro", "cinco", "seis", "sete", "oito", "nove", "dez", "onze", "doze", "treze", "quatorze", "quinze", "dezesseis", "dezessete", "dezoito", "dezenove");
    private static final List<String> DEZENAS = Arrays.asList("", "", "vinte", "trinta", "quarenta", "cinquenta", "sessenta", "setenta", "oitenta", "noventa");
    private static final List<String> CENTENAS = Arrays.asList("", "cento", "duzentos", "trezentos", "quatrocentos", "quinhentos", "seiscentos", "setecentos", "oitocentos", "novecentos");

    public static String valorPorExtenso(double valor) {
        if (valor == 0) {
            return "zero reais";
        }

        long parteInteira = (long) valor;
        long parteDecimal = Math.round((valor - parteInteira) * 100);

        String extenso = converteNumero(parteInteira) + " reais";

        if (parteDecimal > 0) {
            extenso += " e " + converteNumero(parteDecimal) + " centavo" + (parteDecimal > 1 ? "s" : ""); // Ajusta para plural
        }

        return capitalize(extenso);
    }

    private static String converteNumero(long numero) {
        if (numero < 20) {
            return UNIDADES.get((int) numero);
        } else if (numero < 100) {
            return DEZENAS.get((int) (numero / 10)) + (numero % 10 != 0 ? " e " + UNIDADES.get((int) (numero % 10)) : "");
        } else if (numero < 1000) {
            return (numero == 100 ? "cem" : CENTENAS.get((int) (numero / 100))) + (numero % 100 != 0 ? " e " + converteNumero(numero % 100) : "");
        } else if (numero < 1000000) {
            // Aqui adicionamos o "e" entre milhar e centena (ou dezena)
            String milhar = converteNumero(numero / 1000);
            String resto = numero % 1000 != 0 ? " e " + converteNumero(numero % 1000) : "";
            return milhar + " mil" + resto;
        } else {
            String milhao = converteNumero(numero / 1000000);
            String resto = numero % 1000000 != 0 ? " e " + converteNumero(numero % 1000000) : "";
            return milhao + " milhão" + resto;
        }
    }

    private static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}

