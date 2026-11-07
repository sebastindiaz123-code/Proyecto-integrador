package com.grabadores.view;

import com.grabadores.controller.CamaraController;
import com.grabadores.controller.ReporteController;
import com.grabadores.model.Camara;
import com.grabadores.model.GrabadorExterno;
import com.grabadores.model.MovimientoInventario;
import com.grabadores.model.Reporte;
import com.grabadores.model.Usuario;
import com.grabadores.controller.UsuarioController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Panel de generación, exportación e historial de reportes (RF14).
 * Soporta inventario general, formato, compatibilidad y movimientos por periodo.
 * @author  Equipo Lior-Sebastian-Brayan — UNIAJC Grupo 411
 */
public class ReportePanel extends JPanel {

    private final ReporteController reporteCtrl;
    private final CamaraController camaraCtrl;
    private final Usuario usuarioActual;
    private JTable tabla;
    private DefaultTableModel tableModel;
    private JTable tablaHistorial;
    private DefaultTableModel historialModel;
    private JTextArea txtDetalleHistorial;
    private JComboBox<String> cmbTipoReporte;
    private JTextField txtFiltro;
    private JComboBox<String> cmbCamara;
    private JTextField txtDesde, txtHasta;
    private JComboBox<String> cmbTipoMovimiento, cmbUsuarioMovimiento;
    private List<Usuario> usuariosReporte;
    private JPanel filtroPanel;
    private CardLayout filtroLayout;

    public ReportePanel(Usuario usuario) {
        this.reporteCtrl = new ReporteController();
        this.camaraCtrl = new CamaraController();
        this.usuarioActual = usuario;
        this.usuariosReporte = new UsuarioController().listarTodosIncluyendoInactivos();
        initComponents();
        cargarHistorial();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Panel superior: selección de tipo de reporte
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout(0, 4));
        topPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85)), " Generar Reporte ",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12)));

        JPanel tipoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        tipoPanel.add(new JLabel("Tipo de reporte:"));
        cmbTipoReporte = new JComboBox<>(new String[]{
                "Inventario General", "Por Formato de Grabación", "Compatibilidad con Cámara",
                "Movimientos por Periodo"
        });
        cmbTipoReporte.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbTipoReporte.addActionListener(e -> actualizarFiltro());
        tipoPanel.add(cmbTipoReporte);
        topPanel.add(tipoPanel, BorderLayout.NORTH);

        // Panel de filtros con CardLayout
        filtroLayout = new CardLayout();
        filtroPanel = new JPanel(filtroLayout);
        filtroPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Filtro vacío para inventario general
        filtroPanel.add(new JPanel(), "vacio");

        // Filtro por formato
        JPanel formatoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formatoPanel.add(new JLabel("Formato:"));
        txtFiltro = new JTextField(20);
        txtFiltro.putClientProperty("JTextField.placeholderText", "Ej: ProRes RAW, BRAW, H.265");
        formatoPanel.add(txtFiltro);
        filtroPanel.add(formatoPanel, "formato");

        // Filtro por cámara
        JPanel camaraPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        camaraPanel.add(new JLabel("Cámara:"));
        cmbCamara = new JComboBox<>();
        List<Camara> camaras = camaraCtrl.listarTodas();
        for (Camara c : camaras) cmbCamara.addItem(c.getId() + " - " + c);
        cmbCamara.setPreferredSize(new Dimension(250, 28));
        camaraPanel.add(cmbCamara);
        filtroPanel.add(camaraPanel, "camara");

        JPanel periodoPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        JPanel fechasPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        fechasPanel.add(new JLabel("Desde (AAAA-MM-DD):"));
        txtDesde = new JTextField(LocalDate.now().minusDays(30).toString(), 10);
        fechasPanel.add(txtDesde);
        fechasPanel.add(new JLabel("Hasta (AAAA-MM-DD):"));
        txtHasta = new JTextField(LocalDate.now().toString(), 10);
        fechasPanel.add(txtHasta);
        periodoPanel.add(fechasPanel);

        JPanel movimientoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        movimientoPanel.add(new JLabel("Movimiento:"));
        cmbTipoMovimiento = new JComboBox<>(new String[]{"Todos", "ENTRADA", "SALIDA", "AJUSTE", "CAMBIO_ESTADO"});
        movimientoPanel.add(cmbTipoMovimiento);
        movimientoPanel.add(new JLabel("Usuario:"));
        cmbUsuarioMovimiento = new JComboBox<>();
        cmbUsuarioMovimiento.addItem("Todos");
        for (Usuario usuario : usuariosReporte) {
            cmbUsuarioMovimiento.addItem(usuario.getId() + " - " + usuario.getNombre());
        }
        cmbUsuarioMovimiento.setPreferredSize(new Dimension(200, 28));
        movimientoPanel.add(cmbUsuarioMovimiento);
        periodoPanel.add(movimientoPanel);
        filtroPanel.add(periodoPanel, "periodo");

        topPanel.add(filtroPanel, BorderLayout.CENTER);

        JPanel accionesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        JButton btnGenerar = new JButton("📊 Generar Reporte");
        btnGenerar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnGenerar.setBackground(new Color(125, 178, 170));
        btnGenerar.setForeground(new Color(30, 41, 59));
        btnGenerar.addActionListener(e -> generarReporte());
        accionesPanel.add(btnGenerar);

        JButton btnExportar = new JButton("📄 Exportar PDF");
        btnExportar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnExportar.setBackground(new Color(207, 164, 126));
        btnExportar.setForeground(new Color(30, 41, 59));
        btnExportar.addActionListener(e -> exportarPDF());
        accionesPanel.add(btnExportar);
        topPanel.add(accionesPanel, BorderLayout.SOUTH);

        // Tabla de resultados
        tableModel = new DefaultTableModel();
        tabla = new JTable(tableModel);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setRowHeight(28);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85)), " Resultado del Reporte ",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12)));

        JPanel generacionPanel = new JPanel(new BorderLayout(10, 10));
        generacionPanel.add(topPanel, BorderLayout.NORTH);
        generacionPanel.add(scroll, BorderLayout.CENTER);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Generar reportes", generacionPanel);
        tabs.addTab("Historial de reportes", crearPanelHistorial());
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel crearPanelHistorial() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnActualizar = new JButton("Actualizar historial");
        btnActualizar.addActionListener(e -> cargarHistorial());
        toolbar.add(btnActualizar);
        JButton btnEliminar = new JButton("Eliminar reporte seleccionado");
        btnEliminar.addActionListener(e -> eliminarReporteSeleccionado());
        toolbar.add(btnEliminar);
        panel.add(toolbar, BorderLayout.NORTH);

        historialModel = new DefaultTableModel(
                new String[]{"ID", "Fecha", "Tipo", "Usuario", "Contenido"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaHistorial = new JTable(historialModel);
        tablaHistorial.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaHistorial.setRowHeight(28);
        tablaHistorial.getColumnModel().getColumn(0).setMinWidth(0);
        tablaHistorial.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaHistorial.getColumnModel().getColumn(4).setMinWidth(0);
        tablaHistorial.getColumnModel().getColumn(4).setMaxWidth(0);
        tablaHistorial.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) mostrarDetalleHistorial();
        });

        txtDetalleHistorial = new JTextArea();
        txtDetalleHistorial.setEditable(false);
        txtDetalleHistorial.setLineWrap(true);
        txtDetalleHistorial.setWrapStyleWord(true);
        txtDetalleHistorial.setBorder(new EmptyBorder(8, 8, 8, 8));
        txtDetalleHistorial.setText("Seleccione un reporte para consultar su contenido.");

        JScrollPane tablaScroll = new JScrollPane(tablaHistorial);
        tablaScroll.setBorder(BorderFactory.createTitledBorder("Reportes exportados"));
        JScrollPane detalleScroll = new JScrollPane(txtDetalleHistorial);
        detalleScroll.setBorder(BorderFactory.createTitledBorder("Contenido del reporte seleccionado"));
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tablaScroll, detalleScroll);
        split.setResizeWeight(0.65);
        panel.add(split, BorderLayout.CENTER);
        return panel;
    }

    private void cargarHistorial() {
        historialModel.setRowCount(0);
        for (Reporte reporte : reporteCtrl.listarHistorial()) {
            String nombreUsuario = reporte.getUsuario() != null
                    ? reporte.getUsuario().getNombre() : "Usuario no disponible";
            historialModel.addRow(new Object[]{
                    reporte.getId(), reporte.getFecha(), reporte.getTipo(),
                    nombreUsuario, reporte.getContenido()
            });
        }
        txtDetalleHistorial.setText(historialModel.getRowCount() == 0
                ? "Todavía no hay reportes exportados."
                : "Seleccione un reporte para consultar su contenido.");
    }

    private void mostrarDetalleHistorial() {
        int fila = tablaHistorial.getSelectedRow();
        if (fila >= 0) {
            Object contenido = historialModel.getValueAt(fila, 4);
            txtDetalleHistorial.setText(contenido == null ? "" : contenido.toString());
            txtDetalleHistorial.setCaretPosition(0);
        }
    }

    private void eliminarReporteSeleccionado() {
        int filaSeleccionada = tablaHistorial.getSelectedRow();
        if (filaSeleccionada < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un reporte del historial para eliminar.",
                    "Ningún reporte seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int filaModelo = tablaHistorial.convertRowIndexToModel(filaSeleccionada);
        int reporteId = (int) historialModel.getValueAt(filaModelo, 0);
        String tipo = historialModel.getValueAt(filaModelo, 2).toString();
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar el reporte \"" + tipo + "\" del historial?\n"
                        + "Esta acción no se puede deshacer.",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        if (reporteCtrl.eliminarReporte(reporteId)) {
            cargarHistorial();
            JOptionPane.showMessageDialog(this, "El reporte se eliminó del historial.",
                    "Reporte eliminado", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo eliminar el reporte.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarFiltro() {
        int idx = cmbTipoReporte.getSelectedIndex();
        switch (idx) {
            case 0 -> filtroLayout.show(filtroPanel, "vacio");
            case 1 -> filtroLayout.show(filtroPanel, "formato");
            case 2 -> filtroLayout.show(filtroPanel, "camara");
            case 3 -> filtroLayout.show(filtroPanel, "periodo");
        }
    }

    private void generarReporte() {
        int tipo = cmbTipoReporte.getSelectedIndex();

        switch (tipo) {
            case 0 -> { // Inventario General
                String[] cols = {"Marca", "Modelo", "Nº Serie", "Año", "Stock", "Estado", "Precio"};
                tableModel = new DefaultTableModel(cols, 0);
                List<String[]> datos = reporteCtrl.generarReporteInventarioGeneral();
                for (String[] row : datos) tableModel.addRow(row);
                tabla.setModel(tableModel);
            }
            case 1 -> { // Por Formato
                String formato = txtFiltro.getText().trim();
                if (formato.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Ingrese un formato de grabación para filtrar.");
                    return;
                }
                String[] cols = {"Marca", "Modelo", "Nº Serie", "Año", "Formatos", "Resolución", "Precio"};
                tableModel = new DefaultTableModel(cols, 0);
                List<GrabadorExterno> grabadores = reporteCtrl.generarReportePorFormato(formato);
                for (GrabadorExterno g : grabadores) {
                    tableModel.addRow(new Object[]{g.getMarca(), g.getModelo(), g.getNumeroSerie(),
                            g.getAnio(), g.getFormatos(), g.getResolucion(),
                            String.format("$%.2f", g.getPrecio())});
                }
                tabla.setModel(tableModel);
            }
            case 2 -> { // Compatibilidad con cámara
                int ci = cmbCamara.getSelectedIndex();
                if (ci < 0) {
                    JOptionPane.showMessageDialog(this, "Seleccione una cámara.");
                    return;
                }
                List<Camara> camaras = camaraCtrl.listarTodas();
                String[] cols = {"Marca", "Modelo", "Conexión", "Nivel", "Resolución", "Formatos"};
                tableModel = new DefaultTableModel(cols, 0);
                List<String[]> datos = reporteCtrl.generarReporteCompatibilidad(camaras.get(ci).getId());
                for (String[] row : datos) tableModel.addRow(row);
                tabla.setModel(tableModel);
            }
            case 3 -> {
                LocalDate desde;
                LocalDate hasta;
                try {
                    desde = LocalDate.parse(txtDesde.getText().trim());
                    hasta = LocalDate.parse(txtHasta.getText().trim());
                } catch (DateTimeParseException e) {
                    JOptionPane.showMessageDialog(this, "Ingrese fechas válidas con formato AAAA-MM-DD.",
                            "Fechas inválidas", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (desde.isAfter(hasta)) {
                    JOptionPane.showMessageDialog(this, "La fecha inicial no puede ser posterior a la fecha final.",
                            "Rango inválido", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String[] cols = {"Fecha", "Grabador", "Movimiento", "Cantidad", "Stock anterior",
                        "Stock nuevo", "Estado anterior", "Estado nuevo", "Usuario", "Observación"};
                tableModel = new DefaultTableModel(cols, 0);
                int usuarioIndex = cmbUsuarioMovimiento.getSelectedIndex();
                int usuarioId = usuarioIndex > 0 ? usuariosReporte.get(usuarioIndex - 1).getId() : 0;
                List<MovimientoInventario> movimientos =
                        reporteCtrl.generarReporteMovimientos(desde.toString(), hasta.toString(),
                                (String) cmbTipoMovimiento.getSelectedItem(), usuarioId);
                for (MovimientoInventario m : movimientos) {
                    tableModel.addRow(new Object[]{m.getFecha(), m.getGrabador(), m.getTipo(), m.getCantidad(),
                            m.getStockAnterior(), m.getStockNuevo(), m.getEstadoAnterior(), m.getEstadoNuevo(),
                            m.getUsuario(), m.getObservacion()});
                }
                tabla.setModel(tableModel);
            }
        }

        JOptionPane.showMessageDialog(this,
                "Reporte generado con " + tableModel.getRowCount() + " registros.",
                "Reporte listo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportarPDF() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Primero genere un reporte.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar reporte PDF");
        chooser.setSelectedFile(new java.io.File("reporte_grabadores.pdf"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            if (!path.endsWith(".pdf")) path += ".pdf";

            String titulo = "Reporte: " + cmbTipoReporte.getSelectedItem();
            String[] encabezados = new String[tableModel.getColumnCount()];
            for (int i = 0; i < encabezados.length; i++) encabezados[i] = tableModel.getColumnName(i);
            List<String[]> filas = new java.util.ArrayList<>();
            for (int row = 0; row < tableModel.getRowCount(); row++) {
                String[] valores = new String[tableModel.getColumnCount()];
                for (int column = 0; column < valores.length; column++) {
                    Object value = tableModel.getValueAt(row, column);
                    valores[column] = value == null ? "" : value.toString();
                }
                filas.add(valores);
            }
            boolean ok = reporteCtrl.exportarTablaPDF(encabezados, filas, titulo,
                    usuarioActual.getNombre(), path, usuarioActual.getId());
            if (ok) {
                cargarHistorial();
                JOptionPane.showMessageDialog(this,
                        "PDF exportado exitosamente en:\n" + path,
                        "Exportación exitosa", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al exportar PDF.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
