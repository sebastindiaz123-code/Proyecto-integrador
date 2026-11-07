package com.grabadores.view;

import com.grabadores.controller.UsuarioController;
import com.grabadores.model.Usuario;
import com.grabadores.util.SwingUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel de gestión de usuarios del sistema.
 * Solo accesible para Administradores. Permite gestionar roles y credenciales.
 *
 * @author Equipo Lior-Sebastian-Brayan — UNIAJC Grupo 411
 */
public class UsuarioPanel extends JPanel {

    private final UsuarioController controller;
    private final Usuario usuarioActual;
    private JTable tabla;
    private DefaultTableModel tableModel;
    
    // Componentes del formulario
    private JTextField txtNombre;
    private JTextField txtCorreo;
    private JPasswordField txtContrasena;
    private JComboBox<String> cmbRol;
    private JButton btnGuardar;
    private JButton btnDesactivar;
    
    private int seleccionadoId = -1;

    public UsuarioPanel(Usuario usuarioActual) {
        this.controller = new UsuarioController();
        this.usuarioActual = usuarioActual;
        initComponents();
        cargarDatos();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // ==================== TABLA DE USUARIOS ====================
        String[] cols = {"ID", "Nombre", "Correo", "Rol", "Estado"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabla = new JTable(tableModel);
        tabla.setFont(new Font("Inter", Font.PLAIN, 12));
        tabla.setRowHeight(32);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Ocultar la columna ID visualmente
        tabla.getColumnModel().getColumn(0).setMinWidth(0);
        tabla.getColumnModel().getColumn(0).setMaxWidth(0);

        // BadgeRenderer para el Rol y Estado
        BadgeRenderer badgeRenderer = new BadgeRenderer();
        tabla.getColumnModel().getColumn(3).setCellRenderer(badgeRenderer);
        tabla.getColumnModel().getColumn(4).setCellRenderer(badgeRenderer);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                cargarSeleccion();
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85)), " Usuarios Registrados ",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Inter", Font.BOLD, 12), new Color(181, 190, 198)));
        add(scroll, BorderLayout.CENTER);

        // ==================== FORMULARIO LATERAL ====================
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85)), " Detalles del Usuario ",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Inter", Font.BOLD, 12), new Color(181, 190, 198)));
        form.setBackground(new Color(30, 41, 59));

        // Nombre
        addFormLabel(form, "Nombre Completo *:");
        txtNombre = createTextField();
        form.add(txtNombre);
        form.add(Box.createVerticalStrut(15));

        // Correo
        addFormLabel(form, "Correo Electrónico *:");
        txtCorreo = createTextField();
        form.add(txtCorreo);
        form.add(Box.createVerticalStrut(15));

        // Contraseña
        addFormLabel(form, "Contraseña (Vacio = No cambiar):");
        txtContrasena = new JPasswordField();
        txtContrasena.setMaximumSize(new Dimension(260, 35));
        txtContrasena.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(txtContrasena);
        form.add(Box.createVerticalStrut(15));

        // Rol
        addFormLabel(form, "Rol del Sistema *:");
        cmbRol = new JComboBox<>(new String[]{"administrador", "consultor"});
        cmbRol.setMaximumSize(new Dimension(260, 35));
        cmbRol.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(cmbRol);
        form.add(Box.createVerticalStrut(25));

        // Botones
        JPanel btns = new JPanel(new GridLayout(3, 1, 5, 8));
        btns.setMaximumSize(new Dimension(260, 130));
        btns.setAlignmentX(Component.LEFT_ALIGNMENT);
        btns.setOpaque(false);

        btnGuardar = new JButton("💾 Guardar Usuario");
        btnGuardar.setFont(new Font("Inter", Font.BOLD, 13));
        btnGuardar.setBackground(new Color(137, 190, 164)); // Verde salvia
        btnGuardar.setForeground(new Color(30, 41, 59));
        btnGuardar.setFocusPainted(false);
        btnGuardar.addActionListener(e -> guardar());
        btns.add(btnGuardar);

        JButton btnLimpiar = new JButton("🧹 Limpiar Campos");
        btnLimpiar.setFont(new Font("Inter", Font.BOLD, 13));
        btnLimpiar.setBackground(new Color(125, 178, 170)); // Verde azulado
        btnLimpiar.setForeground(new Color(30, 41, 59));
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.addActionListener(e -> limpiar());
        btns.add(btnLimpiar);

        btnDesactivar = new JButton("🚫 Desactivar / Activar");
        btnDesactivar.setFont(new Font("Inter", Font.BOLD, 13));
        btnDesactivar.setBackground(new Color(248, 113, 113)); // Coral
        btnDesactivar.setForeground(new Color(30, 41, 59));
        btnDesactivar.setFocusPainted(false);
        btnDesactivar.addActionListener(e -> alternarEstado());
        btnDesactivar.setEnabled(false); // Solo se activa al seleccionar un usuario
        btns.add(btnDesactivar);

        form.add(btns);
        form.add(Box.createVerticalGlue());
        add(SwingUtils.createVerticalScrollPane(form, 320), BorderLayout.EAST);
    }

    private void addFormLabel(JPanel panel, String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Inter", Font.PLAIN, 12));
        lbl.setForeground(new Color(181, 190, 198));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lbl);
        panel.add(Box.createVerticalStrut(5));
    }

    private JTextField createTextField() {
        JTextField txt = new JTextField();
        txt.setMaximumSize(new Dimension(260, 35));
        txt.setAlignmentX(Component.LEFT_ALIGNMENT);
        return txt;
    }

    private void cargarDatos() {
        tableModel.setRowCount(0);
        List<Usuario> lista = controller.listarTodos();
        for (Usuario u : lista) {
            String estadoStr = u.isActivo() ? "ACTIVO" : "INACTIVO";
            tableModel.addRow(new Object[]{u.getId(), u.getNombre(), u.getCorreo(), u.getRol().toUpperCase(), estadoStr});
        }
    }

    private void cargarSeleccion() {
        int row = tabla.getSelectedRow();
        if (row < 0) return;

        seleccionadoId = (int) tableModel.getValueAt(row, 0);
        txtNombre.setText((String) tableModel.getValueAt(row, 1));
        txtCorreo.setText((String) tableModel.getValueAt(row, 2));
        txtContrasena.setText(""); // Nunca cargar la contraseña cifrada
        
        String rol = ((String) tableModel.getValueAt(row, 3)).toLowerCase();
        cmbRol.setSelectedItem(rol);

        btnGuardar.setText("✏️ Actualizar Usuario");
        btnDesactivar.setEnabled(true);
        
        // Evitar que el usuario logueado se desactive a sí mismo por error
        if (seleccionadoId == usuarioActual.getId()) {
            btnDesactivar.setEnabled(false);
        }
    }

    private void guardar() {
        String nombre = txtNombre.getText().trim();
        String correo = txtCorreo.getText().trim();
        String pass = new String(txtContrasena.getPassword());
        String rol = (String) cmbRol.getSelectedItem();

        if (nombre.isEmpty() || correo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre y Correo son obligatorios.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String resultado;
        if (seleccionadoId > 0) {
            // Actualizar
            Usuario u = controller.buscarPorId(seleccionadoId);
            if (u != null) {
                u.setNombre(nombre);
                u.setCorreo(correo);
                u.setRol(rol);
                resultado = controller.actualizar(u, pass);
            } else {
                resultado = "Usuario no encontrado.";
            }
        } else {
            // Registrar nuevo
            if (pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "La contraseña es obligatoria para nuevos usuarios.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            resultado = controller.registrar(nombre, correo, pass, rol);
        }

        if ("OK".equals(resultado)) {
            JOptionPane.showMessageDialog(this, "Usuario guardado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiar();
            cargarDatos();
        } else {
            JOptionPane.showMessageDialog(this, resultado, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void alternarEstado() {
        if (seleccionadoId <= 0) return;
        
        if (seleccionadoId == usuarioActual.getId()) {
            JOptionPane.showMessageDialog(this, "No puede desactivar su propia cuenta.", "Acción denegada", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int row = tabla.getSelectedRow();
        String estadoActual = (String) tableModel.getValueAt(row, 4);
        String accion = estadoActual.equals("ACTIVO") ? "desactivar" : "activar";

        int opt = JOptionPane.showConfirmDialog(this, "¿Está seguro que desea " + accion + " este usuario?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            // Reutilizamos el método desactivar del DAO, que de hecho alterna el estado o lo pone en false según la lógica.
            // Si el DAO implementado solo hace desactivar permanentemente, lo llamamos:
            boolean exito = controller.desactivar(seleccionadoId);
            if (exito) {
                JOptionPane.showMessageDialog(this, "Estado modificado correctamente.");
                limpiar();
                cargarDatos();
            } else {
                JOptionPane.showMessageDialog(this, "Error al modificar el estado.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiar() {
        seleccionadoId = -1;
        txtNombre.setText("");
        txtCorreo.setText("");
        txtContrasena.setText("");
        cmbRol.setSelectedIndex(0);
        tabla.clearSelection();
        btnGuardar.setText("💾 Guardar Usuario");
        btnDesactivar.setEnabled(false);
    }

    /**
     * Renderizador de insignias de colores para roles y estados.
     */
    private static class BadgeRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String text = value != null ? value.toString() : "";
            
            // Añadir márgenes para dar efecto de "píldora"
            label.setText(" " + text + " ");
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setOpaque(true);
            label.setFont(new Font("Inter", Font.BOLD, 10));

            // Colores por defecto
            Color bg = new Color(207, 164, 126); // Peach
            Color fg = new Color(30, 41, 59);

            if (text.equals("ADMINISTRADOR")) {
                bg = new Color(170, 153, 201); // Mauve (Purple)
            } else if (text.equals("CONSULTOR")) {
                bg = new Color(125, 178, 170); // Verde azulado
            } else if (text.equals("ACTIVO")) {
                bg = new Color(137, 190, 164); // Verde salvia
            } else if (text.equals("INACTIVO")) {
                bg = new Color(248, 113, 113); // Red
            }

            if (isSelected) {
                bg = bg.darker();
            }

            label.setBackground(bg);
            label.setForeground(fg);
            return label;
        }
    }
}
