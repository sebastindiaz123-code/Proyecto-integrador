package com.grabadores.controller;

import com.grabadores.dao.UsuarioDAO;
import com.grabadores.model.Usuario;
import com.grabadores.util.PasswordUtil;
import com.grabadores.util.ValidatorUtil;

import java.util.List;

/**
 * Controlador para la lógica de negocio de usuarios.
 * Gestiona autenticación, registro y validaciones (RF01, RF02).
 *
 * @author Equipo PI3 — UNIAJC Grupo 328
 */
public class UsuarioController {

    private final UsuarioDAO dao;

    public UsuarioController() {
        this.dao = new UsuarioDAO();
    }

    /**
     * Autentica un usuario mediante correo y contraseña.
     *
     * @param correo     correo electrónico del usuario
     * @param contrasena contraseña en texto plano (se hashea antes de comparar)
     * @return objeto Usuario si las credenciales son correctas, null si no
     */
    public Usuario login(String correo, String contrasena) {
        if (!ValidatorUtil.esTextoValido(correo) || !ValidatorUtil.esTextoValido(contrasena)) {
            return null;
        }
        Usuario usuario = dao.buscarPorCorreo(correo.trim());
        if (usuario != null && PasswordUtil.verifyPassword(contrasena, usuario.getContrasena())) {
            return usuario;
        }
        return null;
    }

    /**
     * Registra un nuevo usuario con validaciones completas.
     *
     * @return mensaje de resultado ("OK" si fue exitoso, o descripción del error)
     */
    public String registrar(String nombre, String correo, String contrasena, String rol) {
        if (!ValidatorUtil.esTextoValido(nombre)) return "El nombre es obligatorio.";
        if (!ValidatorUtil.esCorreoValido(correo)) return "El correo electrónico no es válido.";
        if (!ValidatorUtil.esContrasenaValida(contrasena)) return "La contraseña debe tener al menos 4 caracteres.";
        if (!ValidatorUtil.esTextoValido(rol)) return "Debe seleccionar un rol.";

        // Verificar que el correo no esté registrado
        if (dao.buscarPorCorreo(correo.trim()) != null) {
            return "Ya existe un usuario con ese correo electrónico.";
        }

        Usuario usuario = new Usuario(nombre.trim(), correo.trim(),
                PasswordUtil.hashPassword(contrasena), rol);

        if (dao.insertar(usuario)) {
            return "OK";
        }
        return "Error al registrar el usuario. Intente nuevamente.";
    }

    /**
     * Actualiza los datos de un usuario existente.
     */
    public String actualizar(Usuario usuario, String nuevaContrasena) {
        if (!ValidatorUtil.esTextoValido(usuario.getNombre())) return "El nombre es obligatorio.";
        if (!ValidatorUtil.esCorreoValido(usuario.getCorreo())) return "El correo no es válido.";

        // Si se proporciona nueva contraseña, hashearla
        if (nuevaContrasena != null && !nuevaContrasena.isEmpty()) {
            if (!ValidatorUtil.esContrasenaValida(nuevaContrasena)) {
                return "La nueva contraseña debe tener al menos 4 caracteres.";
            }
            usuario.setContrasena(PasswordUtil.hashPassword(nuevaContrasena));
        }

        if (dao.actualizar(usuario)) {
            return "OK";
        }
        return "Error al actualizar el usuario.";
    }

    public List<Usuario> listarTodos() {
        return dao.listarTodos();
    }

    public List<Usuario> listarTodosIncluyendoInactivos() {
        return dao.listarTodosIncluyendoInactivos();
    }

    public boolean desactivar(int id) {
        return dao.desactivar(id);
    }

    public Usuario buscarPorId(int id) {
        return dao.buscarPorId(id);
    }
}
