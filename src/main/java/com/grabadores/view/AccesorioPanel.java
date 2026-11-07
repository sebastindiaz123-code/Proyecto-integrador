package com.grabadores.view;

import com.grabadores.controller.AccesorioController;
import com.grabadores.controller.GrabadorController;
import com.grabadores.model.Accesorio;
import com.grabadores.model.GrabadorExterno;
import com.grabadores.model.Usuario;
import com.grabadores.util.SwingUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel de gestión CRUD para accesorios de grabadores (RF13).
 * @author Equipo Lior-Sebastian-Brayan — UNIAJC Grupo 411
 */
public class AccesorioPanel extends JPanel {

    private final AccesorioController controller;
    private final GrabadorController grabadorCtrl;
    private final Usuario usuarioActual;
    private JTable tabla;
    private DefaultTableModel tableModel;
    private JTextField txtNombre, txtDescripcion, txtCantidad;
    private JComboBox<String> cmbEstado, cmbGrabador;
    private JButton btnGuardar;
    private int seleccionadoId = -1;
    private List<GrabadorExterno> grabadores;

    public AccesorioPanel(Usuario usuario) {
        this.controller = new AccesorioController();
        this.grabadorCtrl = new GrabadorController();
        this.usuarioActual = usuario;
        initComponents();
        cargarDatos();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        String[] cols = {"ID", "Nombre", "Descripción", "Cantidad", "Estado", "Grabador", "GrabID"};
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
        tabla.getColumnModel().getColumn(6).setMinWidth(0);
        tabla.getColumnModel().getColumn(6).setMaxWidth(0);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) cargarSeleccion();
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85)), " Accesorios ",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12)));
        add(scroll, BorderLayout.CENTER);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85)), " Datos del Accesorio ",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12)));
        txtNombre = addField(form, "Nombre *:");
        txtDescripcion = addField(form, "Descripción:");
        txtCantidad = addField(form, "Cantidad *:");

        addLabel(form, "Estado:");
        cmbEstado = new JComboBox<>(new String[]{"disponible", "en_uso", "dañado", "baja"});
        cmbEstado.setMaximumSize(new Dimension(260, 30));
        cmbEstado.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(cmbEstado);
        form.add(Box.createVerticalStrut(10));

        addLabel(form, "Grabador *:");
        cmbGrabador = new JComboBox<>();
        cmbGrabador.setMaximumSize(new Dimension(260, 30));
        cmbGrabador.setAlignmentX(Component.LEFT_ALIGNMENT);
        grabadores = grabadorCtrl.listarTodos();
        for (GrabadorExterno g : grabadores) cmbGrabador.addItem(g.getId() + " - " + g);
        form.add(cmbGrabador);
        form.add(Box.createVerticalStrut(15));

        JPanel btns = new JPanel(new GridLayout(3, 1, 5, 5));
        btns.setMaximumSize(new Dimension(260, 120));
        btns.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnGuardar = new JButton("💾 Guardar");
        btnGuardar.setBackground(new Color(137, 190, 164));
        btnGuardar.setForeground(new Color(30, 41, 59));
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnGuardar.addActionListener(e -> guardar());
        btns.add(btnGuardar);
        JButton btnLimpiar = new JButton("🧹 Limpiar");
        btnLimpiar.addActionListener(e -> limpiar());
        btns.add(btnLimpiar);
        JButton btnEliminar = new JButton("🗑️ Eliminar");
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

    private JTextField addField(JPanel p, String lbl) {
        addLabel(p, lbl);
        JTextField t = new JTextField();
        t.setMaximumSize(new Dimension(260, 30));
        t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        t.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(t); p.add(Box.createVerticalStrut(5));
        return t;
    }

    private void addLabel(JPanel p, String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(l);
    }

    private void cargarDatos() {
        tableModel.setRowCount(0);
        for (Accesorio a : controller.listarTodos()) {
            GrabadorExterno g = grabadorCtrl.buscarPorId(a.getGrabadorId());
            String gName = g != null ? g.toString() : "ID:" + a.getGrabadorId();
            tableModel.addRow(new Object[]{a.getId(), a.getNombre(), a.getDescripcion(),
                    a.getCantidad(), a.getEstado(), gName, a.getGrabadorId()});
        }
    }

    private void cargarSeleccion() {
        int row = tabla.getSelectedRow();
        if (row < 0) return;
        seleccionadoId = (int) tableModel.getValueAt(row, 0);
        txtNombre.setText((String) tableModel.getValueAt(row, 1));
        txtDescripcion.setText((String) tableModel.getValueAt(row, 2));
        txtCantidad.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        cmbEstado.setSelectedItem(tableModel.getValueAt(row, 4));
        int gId = (int) tableModel.getValueAt(row, 6);
        for (int i = 0; i < grabadores.size(); i++) {
            if (grabadores.get(i).getId() == gId) { cmbGrabador.setSelectedIndex(i); break; }
        }
        btnGuardar.setText("✏️ Actualizar");
    }

    private void guardar() {
        int idx = cmbGrabador.getSelectedIndex();
        if (idx < 0) return;
        int cant = com.grabadores.util.ValidatorUtil.parsearEntero(txtCantidad.getText());
        Accesorio a = new Accesorio(txtNombre.getText().trim(), txtDescripcion.getText().trim(),
                Math.max(cant, 0), (String) cmbEstado.getSelectedItem(), grabadores.get(idx).getId());
        String res;
        if (seleccionadoId > 0) { a.setId(seleccionadoId); res = controller.actualizar(a); }
        else { res = controller.registrar(a); }
        if ("OK".equals(res)) {
            JOptionPane.showMessageDialog(this, "Éxito.", "OK", JOptionPane.INFORMATION_MESSAGE);
            limpiar(); cargarDatos();
        } else JOptionPane.showMessageDialog(this, res, "Error", JOptionPane.WARNING_MESSAGE);
    }

    private void eliminar() {
        if (seleccionadoId <= 0) return;
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar?") == JOptionPane.YES_OPTION) {
            controller.eliminar(seleccionadoId); limpiar(); cargarDatos();
        }
    }

    private void limpiar() {
        seleccionadoId = -1; txtNombre.setText(""); txtDescripcion.setText(""); txtCantidad.setText("");
        cmbEstado.setSelectedIndex(0); tabla.clearSelection(); btnGuardar.setText("💾 Guardar");
    }
}
