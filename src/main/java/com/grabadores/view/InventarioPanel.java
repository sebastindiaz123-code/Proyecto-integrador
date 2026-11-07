package com.grabadores.view;

import com.grabadores.controller.GrabadorController;
import com.grabadores.controller.InventarioController;
import com.grabadores.model.GrabadorExterno;
import com.grabadores.model.Inventario;
import com.grabadores.model.MovimientoInventario;
import com.grabadores.model.Usuario;
import com.grabadores.util.SwingUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel de gestión de inventario de grabadores (RF09, RF10).
 * @author Equipo Lior-Sebastian-Brayan — UNIAJC Grupo 411
 */
public class InventarioPanel extends JPanel {

    private final InventarioController controller;
    private final GrabadorController grabadorCtrl;
    private final Usuario usuarioActual;
    private JTable tabla;
    private JTable tablaMovimientos;
    private DefaultTableModel tableModel;
    private DefaultTableModel movimientosModel;
    private JTextField txtStock;
    private JComboBox<String> cmbEstado;
    private JComboBox<String> cmbGrabador;
    private JButton btnGuardar;
    private int seleccionadoId = -1;
    private List<GrabadorExterno> grabadores;

    public InventarioPanel(Usuario usuario) {
        this.controller = new InventarioController();
        this.grabadorCtrl = new GrabadorController();
        this.usuarioActual = usuario;
        initComponents();
        cargarDatos();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        String[] cols = {"ID", "Grabador", "Stock", "Estado", "Grabador ID"};
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
        tabla.getColumnModel().getColumn(4).setMaxWidth(0);
        
        // Renderizador de etiquetas de estado (Badges)
        tabla.getColumnModel().getColumn(3).setCellRenderer(new BadgeRenderer());
        
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) cargarSeleccion();
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85)), " Inventario ",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12)));

        movimientosModel = new DefaultTableModel(new String[]{
                "Fecha", "Grabador", "Movimiento", "Cantidad", "Stock anterior",
                "Stock nuevo", "Estado anterior", "Estado nuevo", "Usuario", "Observación"
        }, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaMovimientos = new JTable(movimientosModel);
        tablaMovimientos.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tablaMovimientos.setRowHeight(28);
        JScrollPane historialScroll = new JScrollPane(tablaMovimientos);
        historialScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85)), " Historial de movimientos ",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12)));

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Existencias", scroll);
        tabs.addTab("Historial", historialScroll);
        tabs.addChangeListener(e -> cargarMovimientos());
        add(tabs, BorderLayout.CENTER);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85)), " Datos del Inventario ",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12)));
        JLabel lblGrab = new JLabel("Grabador *:");
        lblGrab.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblGrab.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(lblGrab);
        cmbGrabador = new JComboBox<>();
        cmbGrabador.setMaximumSize(new Dimension(260, 30));
        cmbGrabador.setAlignmentX(Component.LEFT_ALIGNMENT);
        cargarGrabadores();
        form.add(cmbGrabador);
        form.add(Box.createVerticalStrut(10));

        JLabel lblStock = new JLabel("Stock *:");
        lblStock.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStock.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(lblStock);
        txtStock = new JTextField();
        txtStock.setMaximumSize(new Dimension(260, 30));
        txtStock.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(txtStock);
        form.add(Box.createVerticalStrut(10));

        JLabel lblEst = new JLabel("Estado *:");
        lblEst.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEst.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(lblEst);
        cmbEstado = new JComboBox<>(new String[]{"disponible", "en_uso", "mantenimiento", "danado", "baja"});
        cmbEstado.setMaximumSize(new Dimension(260, 30));
        cmbEstado.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(cmbEstado);
        form.add(Box.createVerticalStrut(15));

        JPanel btns = new JPanel(new GridLayout(4, 1, 5, 5));
        btns.setMaximumSize(new Dimension(260, 160));
        btns.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnGuardar = new JButton("💾 Guardar");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnGuardar.setBackground(new Color(137, 190, 164));
        btnGuardar.setForeground(new Color(30, 41, 59));
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

        JButton btnMovimiento = new JButton("↕ Registrar entrada / salida");
        btnMovimiento.addActionListener(e -> registrarMovimiento());
        btnMovimiento.setVisible(usuarioActual.esAdministrador());
        btns.add(btnMovimiento);

        form.add(btns);
        form.add(Box.createVerticalGlue());
        add(SwingUtils.createVerticalScrollPane(form, 300), BorderLayout.EAST);
        if (!usuarioActual.esAdministrador()) {
            SwingUtils.setEnabledRecursively(form, false);
        }
    }

    private void cargarGrabadores() {
        grabadores = grabadorCtrl.listarTodos();
        cmbGrabador.removeAllItems();
        for (GrabadorExterno g : grabadores) {
            cmbGrabador.addItem(g.getId() + " - " + g.getMarca() + " " + g.getModelo());
        }
    }

    private void cargarDatos() {
        tableModel.setRowCount(0);
        for (Inventario inv : controller.listarTodos()) {
            GrabadorExterno g = grabadorCtrl.buscarPorId(inv.getGrabadorId());
            String grabNombre = g != null ? g.getMarca() + " " + g.getModelo() : "ID:" + inv.getGrabadorId();
            tableModel.addRow(new Object[]{inv.getId(), grabNombre, inv.getStock(), inv.getEstado(), inv.getGrabadorId()});
        }
        cargarMovimientos();
    }

    private void cargarMovimientos() {
        movimientosModel.setRowCount(0);
        for (MovimientoInventario movimiento : controller.listarMovimientos()) {
            movimientosModel.addRow(new Object[]{
                    movimiento.getFecha(), movimiento.getGrabador(), movimiento.getTipo(),
                    movimiento.getCantidad(), movimiento.getStockAnterior(), movimiento.getStockNuevo(),
                    movimiento.getEstadoAnterior(), movimiento.getEstadoNuevo(),
                    movimiento.getUsuario(), movimiento.getObservacion()
            });
        }
    }

    private void cargarSeleccion() {
        int row = tabla.getSelectedRow();
        if (row < 0) return;
        seleccionadoId = (int) tableModel.getValueAt(row, 0);
        txtStock.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        cmbEstado.setSelectedItem(tableModel.getValueAt(row, 3));
        int grabId = (int) tableModel.getValueAt(row, 4);
        for (int i = 0; i < grabadores.size(); i++) {
            if (grabadores.get(i).getId() == grabId) { cmbGrabador.setSelectedIndex(i); break; }
        }
        cmbGrabador.setEnabled(false);
        btnGuardar.setText("✏️ Actualizar");
    }

    private void guardar() {
        int idx = cmbGrabador.getSelectedIndex();
        if (idx < 0) { JOptionPane.showMessageDialog(this, "Seleccione un grabador."); return; }
        int stock = com.grabadores.util.ValidatorUtil.parsearEntero(txtStock.getText());
        if (stock < 0) { JOptionPane.showMessageDialog(this, "Stock debe ser un número válido >= 0."); return; }

        Inventario inv = new Inventario(stock, (String) cmbEstado.getSelectedItem(), grabadores.get(idx).getId());
        String res;
        if (seleccionadoId > 0) {
            inv.setId(seleccionadoId);
            res = controller.actualizar(inv, usuarioActual.getId());
        } else {
            res = controller.registrar(inv, usuarioActual.getId());
        }
        if ("OK".equals(res)) {
            JOptionPane.showMessageDialog(this, "Operación exitosa.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiar(); cargarDatos();
        } else {
            JOptionPane.showMessageDialog(this, res, "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void registrarMovimiento() {
        int row = tabla.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un registro de inventario.");
            return;
        }
        int grabadorId = (int) tableModel.getValueAt(row, 4);

        JComboBox<String> cmbTipo = new JComboBox<>(new String[]{"ENTRADA", "SALIDA"});
        JTextField txtCantidad = new JTextField();
        JComboBox<String> cmbEstadoNuevo = new JComboBox<>(
                new String[]{"Sin cambio", "disponible", "en_uso", "mantenimiento", "danado", "baja"});
        JTextField txtObservacion = new JTextField();
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Tipo:"));
        form.add(cmbTipo);
        form.add(new JLabel("Cantidad:"));
        form.add(txtCantidad);
        form.add(new JLabel("Estado resultante:"));
        form.add(cmbEstadoNuevo);
        form.add(new JLabel("Observación:"));
        form.add(txtObservacion);

        int result = JOptionPane.showConfirmDialog(this, form, "Registrar movimiento",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        int cantidad = com.grabadores.util.ValidatorUtil.parsearEntero(txtCantidad.getText());
        if (cantidad <= 0) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser un entero mayor que cero.");
            return;
        }
        String estado = "Sin cambio".equals(cmbEstadoNuevo.getSelectedItem())
                ? null : (String) cmbEstadoNuevo.getSelectedItem();
        String respuesta = controller.registrarMovimiento(grabadorId, usuarioActual.getId(),
                (String) cmbTipo.getSelectedItem(), cantidad, estado, txtObservacion.getText());
        if ("OK".equals(respuesta)) {
            JOptionPane.showMessageDialog(this, "Movimiento registrado y agregado al historial.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarDatos();
        } else {
            JOptionPane.showMessageDialog(this, respuesta, "No se pudo registrar",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void eliminar() {
        if (seleccionadoId <= 0) return;
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            controller.eliminar(seleccionadoId); limpiar(); cargarDatos();
        }
    }

    private void limpiar() {
        seleccionadoId = -1; txtStock.setText(""); cmbEstado.setSelectedIndex(0);
        if (cmbGrabador.getItemCount() > 0) cmbGrabador.setSelectedIndex(0);
        cmbGrabador.setEnabled(true);
        tabla.clearSelection(); btnGuardar.setText("💾 Guardar");
    }

    /**
     * Renderizador personalizado para dibujar los estados como 'píldoras' (badges).
     */
    private static class BadgeRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String text = value != null ? value.toString().toUpperCase() : "";
            label.setText(text);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setOpaque(true);
            label.setFont(new Font("Inter", Font.BOLD, 10));

            // Colores basados en el estado
            if (text.equals("DISPONIBLE")) {
                label.setBackground(isSelected ? new Color(137, 190, 164).darker() : new Color(137, 190, 164));
                label.setForeground(new Color(30, 41, 59));
            } else if (text.equals("EN_USO")) {
                label.setBackground(isSelected ? new Color(125, 178, 170).darker() : new Color(125, 178, 170));
                label.setForeground(new Color(30, 41, 59));
            } else if (text.equals("DANADO") || text.equals("BAJA")) {
                label.setBackground(isSelected ? new Color(248, 113, 113).darker() : new Color(248, 113, 113));
                label.setForeground(new Color(30, 41, 59));
            } else {
                label.setBackground(isSelected ? new Color(207, 164, 126).darker() : new Color(207, 164, 126));
                label.setForeground(new Color(30, 41, 59));
            }
            return label;
        }
    }
}
