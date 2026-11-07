package com.grabadores.view;

import com.grabadores.controller.CamaraController;
import com.grabadores.controller.CompatibilidadController;
import com.grabadores.controller.GrabadorController;
import com.grabadores.model.*;
import com.grabadores.util.SwingUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel de gestión de compatibilidad entre grabadores y cámaras (RF08).
 * @author Equipo Lior-Sebastian-Brayan — UNIAJC Grupo 411
 */
public class CompatibilidadPanel extends JPanel {

    private final CompatibilidadController controller;
    private final GrabadorController grabadorCtrl;
    private final CamaraController camaraCtrl;
    private final Usuario usuarioActual;
    private JTable tabla;
    private DefaultTableModel tableModel;
    private JComboBox<String> cmbGrabador, cmbCamara, cmbNivel;
    private JComboBox<String> cmbFiltroGrabador, cmbFiltroCamara;
    private JTextField txtConexion;
    private JButton btnGuardar;
    private int seleccionadoId = -1;
    private List<GrabadorExterno> grabadores;
    private List<Camara> camaras;

    public CompatibilidadPanel(Usuario usuario) {
        this.controller = new CompatibilidadController();
        this.grabadorCtrl = new GrabadorController();
        this.camaraCtrl = new CamaraController();
        this.usuarioActual = usuario;
        initComponents();
        cargarDatos();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        grabadores = grabadorCtrl.listarTodos();
        camaras = camaraCtrl.listarTodas();

        String[] cols = {"ID", "Grabador", "Cámara", "Conexión", "Nivel", "GrabID", "CamID"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(tableModel);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setRowHeight(28);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        // Ocultar columnas de IDs
        for (int c : new int[]{0, 5, 6}) {
            tabla.getColumnModel().getColumn(c).setMinWidth(0);
            tabla.getColumnModel().getColumn(c).setMaxWidth(0);
        }
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) cargarSeleccion();
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85)), " Compatibilidades Registradas ",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12)));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filterPanel.add(new JLabel("Filtrar por grabador:"));
        cmbFiltroGrabador = new JComboBox<>();
        cmbFiltroGrabador.addItem("Todos");
        for (GrabadorExterno g : grabadores) cmbFiltroGrabador.addItem(g.getId() + " - " + g);
        filterPanel.add(cmbFiltroGrabador);
        filterPanel.add(new JLabel("Filtrar por cámara:"));
        cmbFiltroCamara = new JComboBox<>();
        cmbFiltroCamara.addItem("Todas");
        for (Camara c : camaras) cmbFiltroCamara.addItem(c.getId() + " - " + c);
        filterPanel.add(cmbFiltroCamara);
        JButton btnLimpiarFiltros = new JButton("Limpiar filtros");
        btnLimpiarFiltros.addActionListener(e -> {
            cmbFiltroGrabador.setSelectedIndex(0);
            cmbFiltroCamara.setSelectedIndex(0);
        });
        filterPanel.add(btnLimpiarFiltros);
        cmbFiltroGrabador.addActionListener(e -> cargarDatos());
        cmbFiltroCamara.addActionListener(e -> cargarDatos());

        JPanel tablePanel = new JPanel(new BorderLayout(0, 6));
        tablePanel.add(filterPanel, BorderLayout.NORTH);
        tablePanel.add(scroll, BorderLayout.CENTER);
        add(tablePanel, BorderLayout.CENTER);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85)), " Registrar Compatibilidad ",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12)));
        addLabel(form, "Grabador *:");
        cmbGrabador = new JComboBox<>();
        cmbGrabador.setMaximumSize(new Dimension(280, 30));
        cmbGrabador.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (GrabadorExterno g : grabadores) cmbGrabador.addItem(g.getId() + " - " + g);
        form.add(cmbGrabador);
        form.add(Box.createVerticalStrut(10));

        addLabel(form, "Cámara *:");
        cmbCamara = new JComboBox<>();
        cmbCamara.setMaximumSize(new Dimension(280, 30));
        cmbCamara.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (Camara c : camaras) cmbCamara.addItem(c.getId() + " - " + c);
        form.add(cmbCamara);
        form.add(Box.createVerticalStrut(10));

        addLabel(form, "Tipo de Conexión *:");
        txtConexion = new JTextField();
        txtConexion.setMaximumSize(new Dimension(280, 30));
        txtConexion.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtConexion.putClientProperty("JTextField.placeholderText", "Ej: HDMI 2.0, 12G-SDI");
        form.add(txtConexion);
        form.add(Box.createVerticalStrut(10));

        addLabel(form, "Nivel de Compatibilidad *:");
        cmbNivel = new JComboBox<>(new String[]{"total", "parcial", "no_compatible"});
        cmbNivel.setMaximumSize(new Dimension(280, 30));
        cmbNivel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(cmbNivel);
        form.add(Box.createVerticalStrut(15));

        JPanel btns = new JPanel(new GridLayout(3, 1, 5, 5));
        btns.setMaximumSize(new Dimension(280, 120));
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
        add(SwingUtils.createVerticalScrollPane(form, 320), BorderLayout.EAST);
        if (!usuarioActual.esAdministrador()) {
            SwingUtils.setEnabledRecursively(form, false);
        }
    }

    private void addLabel(JPanel p, String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(l);
    }

    private void cargarDatos() {
        limpiar();
        tableModel.setRowCount(0);
        int grabadorFiltro = cmbFiltroGrabador.getSelectedIndex() > 0
                ? grabadores.get(cmbFiltroGrabador.getSelectedIndex() - 1).getId() : 0;
        int camaraFiltro = cmbFiltroCamara.getSelectedIndex() > 0
                ? camaras.get(cmbFiltroCamara.getSelectedIndex() - 1).getId() : 0;
        for (Compatibilidad c : controller.listarFiltradas(grabadorFiltro, camaraFiltro)) {
            GrabadorExterno g = grabadorCtrl.buscarPorId(c.getGrabadorId());
            Camara cam = camaraCtrl.buscarPorId(c.getCamaraId());
            tableModel.addRow(new Object[]{c.getId(),
                    g != null ? g.toString() : "ID:" + c.getGrabadorId(),
                    cam != null ? cam.toString() : "ID:" + c.getCamaraId(),
                    c.getTipoConexion(), c.getNivel(), c.getGrabadorId(), c.getCamaraId()});
        }
    }

    private void cargarSeleccion() {
        int row = tabla.getSelectedRow();
        if (row < 0) return;
        seleccionadoId = (int) tableModel.getValueAt(row, 0);
        txtConexion.setText((String) tableModel.getValueAt(row, 3));
        cmbNivel.setSelectedItem(tableModel.getValueAt(row, 4));
        int gId = (int) tableModel.getValueAt(row, 5);
        int cId = (int) tableModel.getValueAt(row, 6);
        for (int i = 0; i < grabadores.size(); i++) if (grabadores.get(i).getId() == gId) { cmbGrabador.setSelectedIndex(i); break; }
        for (int i = 0; i < camaras.size(); i++) if (camaras.get(i).getId() == cId) { cmbCamara.setSelectedIndex(i); break; }
        btnGuardar.setText("✏️ Actualizar");
    }

    private void guardar() {
        int gi = cmbGrabador.getSelectedIndex(), ci = cmbCamara.getSelectedIndex();
        if (gi < 0 || ci < 0) { JOptionPane.showMessageDialog(this, "Seleccione grabador y cámara."); return; }
        Compatibilidad comp = new Compatibilidad(txtConexion.getText().trim(),
                (String) cmbNivel.getSelectedItem(), grabadores.get(gi).getId(), camaras.get(ci).getId());
        String res;
        if (seleccionadoId > 0) { comp.setId(seleccionadoId); res = controller.actualizar(comp); }
        else { res = controller.registrar(comp); }
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
        seleccionadoId = -1; txtConexion.setText(""); cmbNivel.setSelectedIndex(0);
        tabla.clearSelection(); btnGuardar.setText("💾 Guardar");
    }
}
