package com.grabadores.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utilidad para el manejo seguro de contraseñas mediante hash SHA-256.
 * Cumple con el requerimiento RNF03 de seguridad.
 *
 * @author Equipo PI3 — UNIAJC Grupo 328
 */
public class PasswordUtil {

    /**
     * Genera el hash SHA-256 de una contraseña en texto plano.
     *
     * @param password contraseña en texto plano
     * @return cadena hexadecimal del hash SHA-256
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error: SHA-256 no disponible en este sistema.", e);
        }
    }

    /**
     * Verifica si una contraseña en texto plano coincide con un hash almacenado.
     *
     * @param inputPassword  contraseña ingresada por el usuario
     * @param storedHash     hash almacenado en la base de datos
     * @return true si la contraseña coincide con el hash
     */
    public static boolean verifyPassword(String inputPassword, String storedHash) {
        String inputHash = hashPassword(inputPassword);
        return inputHash.equals(storedHash);
    }
}
