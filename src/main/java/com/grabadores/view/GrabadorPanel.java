package com.grabadores.view;

import com.grabadores.controller.GrabadorController;
import com.grabadores.model.GrabadorExterno;
import com.grabadores.model.Usuario;
import com.grabadores.util.SwingUtils;
import com.grabadores.util.ValidatorUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel de gestión CRUD para grabadores externos (RF03-RF06).
 * Incluye tabla con búsqueda, formulario de registro/edición y eliminación lógica.
 *
 * @author Equipo Lior-Sebastian-Brayan — UNIAJC Grupo 411
 */
public class GrabadorPanel extends JPanel {

    private final GrabadorController controller;
    private final Usuario usuarioActual;

    // Componentes de tabla
    private JTable tabla;
    private DefaultTableModel tableModel;
    private JTextField txtBuscar;
    private JTextField txtFiltroMarca, txtFiltroModelo, txtFiltroFormato, txtFiltroResolucion;
    private JComboBox<String> cmbDisponibilidad;
    private JButton btnImagenes;

    // Campos del formulario
    private JTextField txtMarca, txtModelo, txtSerie, txtAnio;
    private JTextField txtFormatos, txtResolucion, txtConexiones;
    private JTextField txtAlmacenamiento, txtPrecio;
    private JTextField txtStockInicial;
    private JComboBox<String> cmbEstadoInicial;
    private JButton btnGuardar, btnLimpiar, btnEliminar;

    // ID del grabador seleccionado (para edición)
    private int grabadorSeleccionadoId = -1;

    public GrabadorPanel(Usuario usuario) {
        this.controller = new GrabadorController();
        this.usuarioActual = usuario;
        initComponents();
        cargarDatos();
    }

    private JTextField crearFiltro(JPanel contenedor, String etiqueta) {
        JPanel panel = new JPanel(new BorderLayout(0, 3));
        panel.add(new JLabel(etiqueta), BorderLayout.NORTH);
        JTextField campo = new JTextField();
        campo.putClientProperty("JTextField.placeholderText", "Filtrar...");
        campo.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                buscar();
            }
        });
        panel.add(campo, BorderLayout.CENTER);
        contenedor.add(panel);
        return campo;
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // ==================== PANEL SUPERIOR: Búsqueda ====================
        JPanel searchPanel = new JPanel(new BorderLayout(8, 6));
        searchPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JPanel searchRow = new JPanel(new BorderLayout(8, 0));
        JLabel lblBuscar = new JLabel("🔍 Buscar:");
        lblBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchRow.add(lblBuscar, BorderLayout.WEST);
        txtBuscar = new JTextField();
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBuscar.putClientProperty("JTextField.placeholderText", "Búsqueda general...");
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                buscar();
            }
        });
        searchRow.add(txtBuscar, BorderLayout.CENTER);

        JPanel searchActions = new JPanel(new GridLayout(1, 2, 6, 0));
        JButton btnRefresh = new JButton("↻ Limpiar filtros");
        btnRefresh.addActionListener(e -> {
            txtBuscar.setText("");
            txtFiltroMarca.setText("");
            txtFiltroModelo.setText("");
            txtFiltroFormato.setText("");
            txtFiltroResolucion.setText("");
            cmbDisponibilidad.setSelectedIndex(0);
            cargarDatos();
        });
        searchActions.add(btnRefresh);
        btnImagenes = new JButton("🖼 Imágenes");
        btnImagenes.setEnabled(false);
        btnImagenes.addActionListener(e -> abrirImagenes());
        searchActions.add(btnImagenes);
        searchRow.add(searchActions, BorderLayout.EAST);
        searchPanel.add(searchRow, BorderLayout.NORTH);

        JPanel filterRow = new JPanel(new GridLayout(1, 5, 8, 0));
        txtFiltroMarca = crearFiltro(filterRow, "Marca");
        txtFiltroModelo = crearFiltro(filterRow, "Modelo");
        txtFiltroFormato = crearFiltro(filterRow, "Formato");
        txtFiltroResolucion = crearFiltro(filterRow, "Resolución");
        JPanel disponibilidadPanel = new JPanel(new BorderLayout(0, 3));
        disponibilidadPanel.add(new JLabel("Disponibilidad"), BorderLayout.NORTH);
        cmbDisponibilidad = new JComboBox<>(new String[]{"Todas", "Disponible", "No disponible"});
        cmbDisponibilidad.addActionListener(e -> buscar());
        disponibilidadPanel.add(cmbDisponibilidad, BorderLayout.CENTER);
        filterRow.add(disponibilidadPanel);
        searchPanel.add(filterRow, BorderLayout.SOUTH);

        add(searchPanel, BorderLayout.NORTH);

        // ==================== PANEL CENTRAL: Tabla ====================
        String[] columnas = {"ID", "Marca", "Modelo", "Nº Serie", "Año", "Formatos", "Resolución", "Conexiones", "Almacenamiento", "Precio"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabla = new JTable(tableModel);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setRowHeight(28);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Ocultar columna ID
        tabla.getColumnModel().getColumn(0).setMinWidth(0);
        tabla.getColumnModel().getColumn(0).setMaxWidth(0);

        // Selección de fila carga datos en el formulario
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                cargarSeleccion();
            }
        });

        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85)), " Grabadores Registrados ",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12)));
        add(scrollPane, BorderLayout.CENTER);

        // ==================== PANEL DERECHO: Formulario ====================
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85)), " Datos del Grabador ",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12)));
        txtMarca = addFormField(formPanel, "Marca *:");
        txtModelo = addFormField(formPanel, "Modelo *:");
        txtSerie = addFormField(formPanel, "Nº Serie *:");
        txtAnio = addFormField(formPanel, "Año:");
        txtFormatos = addFormField(formPanel, "Formatos:");
        txtResolucion = addFormField(formPanel, "Resolución:");
        txtConexiones = addFormField(formPanel, "Conexiones:");
        txtAlmacenamiento = addFormField(formPanel, "Almacenamiento:");
        txtPrecio = addFormField(formPanel, "Precio (USD):");
        txtStockInicial = addFormField(formPanel, "Stock inicial:");
        txtStockInicial.setText("0");
        JLabel lblEstadoInicial = new JLabel("Estado inicial:");
        lblEstadoInicial.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEstadoInicial.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(lblEstadoInicial);
        cmbEstadoInicial = new JComboBox<>(new String[]{
                "disponible", "en_uso", "mantenimiento", "danado", "baja"
        });
        cmbEstadoInicial.setMaximumSize(new Dimension(260, 30));
        cmbEstadoInicial.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(cmbEstadoInicial);
        formPanel.add(Box.createVerticalStrut(5));

        // Botones de acción
        JPanel btnPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        btnPanel.setBorder(new EmptyBorder(10, 4, 10, 4));

        btnGuardar = new JButton("💾 Guardar");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnGuardar.setBackground(new Color(137, 190, 164));
        btnGuardar.setForeground(new Color(30, 41, 59));
        btnGuardar.putClientProperty("JButton.buttonType", "roundRect");
        btnGuardar.addActionListener(e -> guardar());
        btnPanel.add(btnGuardar);

        btnLimpiar = new JButton("🧹 Limpiar / Nuevo");
        btnLimpiar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnLimpiar.setBackground(new Color(51, 65, 85));
        btnLimpiar.setForeground(new Color(226, 232, 240));
        btnLimpiar.putClientProperty("JButton.buttonType", "roundRect");
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnPanel.add(btnLimpiar);

        btnEliminar = new JButton("🗑️ Desactivar");
        btnEliminar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnEliminar.setBackground(new Color(248, 113, 113));
        btnEliminar.setForeground(new Color(30, 41, 59));
        btnEliminar.putClientProperty("JButton.buttonType", "roundRect");
        btnEliminar.addActionListener(e -> eliminar());
        // Solo administradores pueden eliminar
        btnEliminar.setVisible(usuarioActual.esAdministrador());
        btnPanel.add(btnEliminar);

        JPanel formContainer = new JPanel(new BorderLayout());
        formContainer.setPreferredSize(new Dimension(300, 0));
        formContainer.add(SwingUtils.createVerticalScrollPane(formPanel, 300), BorderLayout.CENTER);
        formContainer.add(btnPanel, BorderLayout.SOUTH);
        add(formContainer, BorderLayout.EAST);
        if (!usuarioActual.esAdministrador()) {
            SwingUtils.setEnabledRecursively(formPanel, false);
        }
    }

    /**
     * Agrega un campo de texto etiquetado al formulario.
     */
    private JTextField addFormField(JPanel panel, String label) {
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

    /**
     * Carga todos los grabadores activos en la tabla.
     */
    private void cargarDatos() {
        limpiarFormulario();
        tableModel.setRowCount(0);
        List<GrabadorExterno> lista = controller.listarTodos();
        for (GrabadorExterno g : lista) {
            tableModel.addRow(new Object[]{
                g.getId(), g.getMarca(), g.getModelo(), g.getNumeroSerie(),
                g.getAnio(), g.getFormatos(), g.getResolucion(),
                g.getConexiones(), g.getAlmacenamiento(),
                String.format("$%.2f", g.getPrecio())
            });
        }
    }

    /**
     * Filtra grabadores según el término de búsqueda.
     */
    private void buscar() {
        limpiarFormulario();
        List<GrabadorExterno> lista = controller.buscarFiltrado(
                txtBuscar.getText(), txtFiltroMarca.getText(), txtFiltroModelo.getText(),
                txtFiltroFormato.getText(), txtFiltroResolucion.getText(),
                (String) cmbDisponibilidad.getSelectedItem());
        tableModel.setRowCount(0);
        for (GrabadorExterno g : lista) {
            tableModel.addRow(new Object[]{
                g.getId(), g.getMarca(), g.getModelo(), g.getNumeroSerie(),
                g.getAnio(), g.getFormatos(), g.getResolucion(),
                g.getConexiones(), g.getAlmacenamiento(),
                String.format("$%.2f", g.getPrecio())
            });
        }
    }

    /**
     * Carga los datos de la fila seleccionada en el formulario.
     */
    private void cargarSeleccion() {
        int row = tabla.getSelectedRow();
        if (row < 0) return;

        grabadorSeleccionadoId = (int) tableModel.getValueAt(row, 0);
        btnImagenes.setEnabled(true);
        txtMarca.setText((String) tableModel.getValueAt(row, 1));
        txtModelo.setText((String) tableModel.getValueAt(row, 2));
        txtSerie.setText((String) tableModel.getValueAt(row, 3));
        txtAnio.setText(String.valueOf(tableModel.getValueAt(row, 4)));
        txtFormatos.setText((String) tableModel.getValueAt(row, 5));
        txtResolucion.setText((String) tableModel.getValueAt(row, 6));
        txtConexiones.setText((String) tableModel.getValueAt(row, 7));
        txtAlmacenamiento.setText((String) tableModel.getValueAt(row, 8));
        String precioStr = ((String) tableModel.getValueAt(row, 9)).replace("$", "").replace(",", "");
        txtPrecio.setText(precioStr);
        txtStockInicial.setEnabled(false);
        cmbEstadoInicial.setEnabled(false);

        btnGuardar.setText("✏️ Actualizar");
    }

    private void abrirImagenes() {
        if (grabadorSeleccionadoId <= 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un grabador para consultar sus imágenes.");
            return;
        }
        GrabadorExterno grabador = controller.buscarPorId(grabadorSeleccionadoId);
        if (grabador == null) {
            JOptionPane.showMessageDialog(this, "No se encontró el grabador seleccionado.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        new ImagenesGrabadorDialog(SwingUtilities.getWindowAncestor(this), grabador,
                usuarioActual.esAdministrador()).setVisible(true);
    }

    /**
     * Guarda o actualiza un grabador según el estado del formulario.
     */
    private void guardar() {
        GrabadorExterno g = new GrabadorExterno();
        g.setMarca(txtMarca.getText().trim());
        g.setModelo(txtModelo.getText().trim());
        g.setNumeroSerie(txtSerie.getText().trim());
        g.setAnio(ValidatorUtil.parsearEntero(txtAnio.getText()));
        g.setFormatos(txtFormatos.getText().trim());
        g.setResolucion(txtResolucion.getText().trim());
        g.setConexiones(txtConexiones.getText().trim());
        g.setAlmacenamiento(txtAlmacenamiento.getText().trim());
        g.setPrecio(ValidatorUtil.parsearDouble(txtPrecio.getText()));

        String resultado;
        if (grabadorSeleccionadoId > 0) {
            g.setId(grabadorSeleccionadoId);
            resultado = controller.actualizar(g);
        } else {
            int stockInicial = ValidatorUtil.parsearEntero(txtStockInicial.getText());
            if (stockInicial < 0) {
                JOptionPane.showMessageDialog(this, "El stock inicial debe ser un entero mayor o igual a cero.",
                        "Stock inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            resultado = controller.registrar(g, stockInicial,
                    (String) cmbEstadoInicial.getSelectedItem(), usuarioActual.getId());
        }

        if ("OK".equals(resultado)) {
            JOptionPane.showMessageDialog(this,
                    grabadorSeleccionadoId > 0
                            ? "Grabador actualizado exitosamente."
                            : "Grabador e inventario inicial registrados exitosamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarDatos();
        } else {
            JOptionPane.showMessageDialog(this, resultado, "Error de validación", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Desactiva el grabador seleccionado (eliminación lógica).
     */
    private void eliminar() {
        if (grabadorSeleccionadoId <= 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un grabador de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int opt = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de desactivar este grabador?\nEl registro se conservará en el historial.",
                "Confirmar desactivación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (opt == JOptionPane.YES_OPTION) {
            if (controller.desactivar(grabadorSeleccionadoId)) {
                JOptionPane.showMessageDialog(this, "Grabador desactivado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario();
                cargarDatos();
            }
        }
    }

    /**
     * Limpia todos los campos del formulario.
     */
    private void limpiarFormulario() {
        grabadorSeleccionadoId = -1;
        txtMarca.setText(""); txtModelo.setText(""); txtSerie.setText("");
        txtAnio.setText(""); txtFormatos.setText(""); txtResolucion.setText("");
        txtConexiones.setText(""); txtAlmacenamiento.setText(""); txtPrecio.setText("");
        txtStockInicial.setText("0");
        txtStockInicial.setEnabled(true);
        cmbEstadoInicial.setSelectedItem("disponible");
        cmbEstadoInicial.setEnabled(true);
        tabla.clearSelection();
        btnImagenes.setEnabled(false);
        btnGuardar.setText("💾 Guardar");
    }
}
