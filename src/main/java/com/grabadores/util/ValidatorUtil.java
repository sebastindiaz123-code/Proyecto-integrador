package com.grabadores.util;

import java.util.regex.Pattern;

/**
 * Utilidad para validar datos de entrada en formularios.
 * Previene datos inválidos antes de persistir en la base de datos.
 *
 * @author Equipo PI3 — UNIAJC Grupo 328
 */
public class ValidatorUtil {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    /**
     * Verifica que un texto no esté vacío ni sea solo espacios.
     */
    public static boolean esTextoValido(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }

    /**
     * Verifica que un correo electrónico tenga formato válido.
     */
    public static boolean esCorreoValido(String correo) {
        return correo != null && EMAIL_PATTERN.matcher(correo.trim()).matches();
    }

    /**
     * Verifica que un número sea positivo.
     */
    public static boolean esNumeroPositivo(double numero) {
        return numero > 0;
    }

    /**
     * Verifica que un año esté en un rango razonable.
     */
    public static boolean esAnioValido(int anio) {
        return anio >= 1990 && anio <= 2030;
    }

    /**
     * Verifica que una contraseña tenga al menos 4 caracteres.
     */
    public static boolean esContrasenaValida(String contrasena) {
        return contrasena != null && contrasena.length() >= 4;
    }

    /**
     * Intenta parsear un texto como entero. Retorna -1 si no es válido.
     */
    public static int parsearEntero(String texto) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Intenta parsear un texto como double. Retorna -1.0 si no es válido.
     */
    public static double parsearDouble(String texto) {
        try {
            return Double.parseDouble(texto.trim());
        } catch (NumberFormatException e) {
            return -1.0;
        }
    }
}
