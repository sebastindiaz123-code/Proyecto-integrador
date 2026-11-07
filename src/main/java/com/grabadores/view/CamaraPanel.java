package com.grabadores.view;

import com.grabadores.controller.CamaraController;
import com.grabadores.model.Camara;
import com.grabadores.model.Usuario;
import com.grabadores.util.SwingUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Panel de gestión CRUD para cámaras de video.
 * @author Equipo Lior-Sebastian-Brayan — UNIAJC Grupo 411
 */
public class CamaraPanel extends JPanel {

    private final CamaraController controller;
    private final Usuario usuarioActual;
    private JTable tabla;
    private DefaultTableModel tableModel;
    private JTextField txtMarca, txtModelo, txtConexion;
    private JButton btnGuardar;
    private int seleccionadoId = -1;

    public CamaraPanel(Usuario usuario) {
        this.controller = new CamaraController();
        this.usuarioActual = usuario;
        initComponents();
        cargarDatos();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Tabla
        String[] cols = {"ID", "Marca", "Modelo", "Tipo de Conexión"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(tableModel);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setRowHeight(28);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabla.getColumnModel().getColumn(0).setMinWidth(0);
        tabla.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) cargarSeleccion();
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85)), " Cámaras Registradas ",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12)));
        add(scroll, BorderLayout.CENTER);

        // Formulario
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85)), " Datos de la Cámara ",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12)));
        txtMarca = addField(form, "Marca *:");
        txtModelo = addField(form, "Modelo *:");
        txtConexion = addField(form, "Tipo Conexión:");

        form.add(Box.createVerticalStrut(15));

        JPanel btns = new JPanel(new GridLayout(3, 1, 5, 5));
        btns.setMaximumSize(new Dimension(260, 120));
        btns.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnGuardar = new JButton("💾 Guardar");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnGuardar.setBackground(new Color(137, 190, 164));
        btnGuardar.setForeground(new Color(30, 41, 59));
        btnGuardar.addActionListener(e -> guardar());
        btns.add(btnGuardar);

        JButton btnLimpiar = new JButton("🧹 Limpiar / Nuevo");
        btnLimpiar.addActionListener(e -> limpiar());
        btns.add(btnLimpiar);

        JButton btnEliminar = new JButton("🗑️ Desactivar");
        btnEliminar.setBackground(new Color(248, 113, 113));
        btnEliminar.setForeground(new Color(30, 41, 59));
        btnEliminar.addActionListener(e -> eliminar());
        btnEliminar.setVisible(usuarioActual.esAdministrador());
        btns.add(btnEliminar);

        form.add(btns);
        form.add(Box.createVerticalGlue());
        add(SwingUtils.createVerticalScrollPane(form, 300), BorderLayout.EAST);
        if (!usuarioActual.esAdministrador()) {
            SwingUtils.setEnabledRecursively(form, false);
        }
    }

    private JTextField addField(JPanel panel, String label) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lbl);
        JTextField txt = new JTextField();
        txt.setMaximumSize(new Dimension(260, 30));
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txt.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(txt);
        panel.add(Box.createVerticalStrut(5));
        return txt;
    }

    private void cargarDatos() {
        tableModel.setRowCount(0);
        for (Camara c : controller.listarTodas()) {
            tableModel.addRow(new Object[]{c.getId(), c.getMarca(), c.getModelo(), c.getTipoConexion()});
        }
    }

    private void cargarSeleccion() {
        int row = tabla.getSelectedRow();
        if (row < 0) return;
        seleccionadoId = (int) tableModel.getValueAt(row, 0);
        txtMarca.setText((String) tableModel.getValueAt(row, 1));
        txtModelo.setText((String) tableModel.getValueAt(row, 2));
        txtConexion.setText((String) tableModel.getValueAt(row, 3));
        btnGuardar.setText("✏️ Actualizar");
    }

    private void guardar() {
        Camara c = new Camara(txtMarca.getText().trim(), txtModelo.getText().trim(), txtConexion.getText().trim());
        String res;
        if (seleccionadoId > 0) { c.setId(seleccionadoId); res = controller.actualizar(c); }
        else { res = controller.registrar(c); }
        if ("OK".equals(res)) {
            JOptionPane.showMessageDialog(this, "Operación exitosa.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiar(); cargarDatos();
        } else {
            JOptionPane.showMessageDialog(this, res, "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void eliminar() {
        if (seleccionadoId <= 0) { JOptionPane.showMessageDialog(this, "Seleccione una cámara."); return; }
        if (JOptionPane.showConfirmDialog(this, "¿Desactivar esta cámara? El registro se conservará.",
                "Confirmar desactivación", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (controller.eliminar(seleccionadoId)) {
                limpiar();
                cargarDatos();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo desactivar la cámara.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiar() {
        seleccionadoId = -1;
        txtMarca.setText(""); txtModelo.setText(""); txtConexion.setText("");
        tabla.clearSelection(); btnGuardar.setText("💾 Guardar");
    }
}
