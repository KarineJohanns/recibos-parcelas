package com.gerarecibos.recibos.Utils;

import java.security.SecureRandom;

public class SenhaUtil {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PASSWORD_LENGTH = 8; // Define o comprimento da senha

    public static String gerarSenhaTemporaria() {
        SecureRandom random = new SecureRandom();
        StringBuilder senha = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            senha.append(CHARACTERS.charAt(index));
        }
        return senha.toString();
    }
}
