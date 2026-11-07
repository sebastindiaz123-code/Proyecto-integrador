package com.grabadores.view;

import com.grabadores.controller.GrabadorController;
import com.grabadores.model.GrabadorExterno;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel comparador de grabadores lado a lado (RF16).
 * Permite seleccionar 2 o más grabadores y comparar sus especificaciones.
 * @author Equipo Lior-Sebastian-Brayan — UNIAJC Grupo 411
 */
public class ComparadorPanel extends JPanel {

    private final GrabadorController controller;
    private JList<String> listaGrabadores;
    private DefaultListModel<String> listModel;
    private JTable tablaComparativa;
    private DefaultTableModel compareModel;
    private List<GrabadorExterno> todosGrabadores;

    public ComparadorPanel() {
        this.controller = new GrabadorController();
        initComponents();
        cargarGrabadores();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Panel izquierdo: lista de selección
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setPreferredSize(new Dimension(280, 0));
        leftPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85)),
                " Seleccione Grabadores (Ctrl+Click) ",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 11)));

        listModel = new DefaultListModel<>();
        listaGrabadores = new JList<>(listModel);
        listaGrabadores.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listaGrabadores.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        leftPanel.add(new JScrollPane(listaGrabadores), BorderLayout.CENTER);

        JButton btnComparar = new JButton("⚖️ Comparar Seleccionados");
        btnComparar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnComparar.setBackground(new Color(125, 178, 170));
        btnComparar.setForeground(new Color(30, 41, 59));
        btnComparar.addActionListener(e -> comparar());
        leftPanel.add(btnComparar, BorderLayout.SOUTH);

        add(leftPanel, BorderLayout.WEST);

        // Panel central: tabla comparativa
        compareModel = new DefaultTableModel();
        tablaComparativa = new JTable(compareModel);
        tablaComparativa.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaComparativa.setRowHeight(30);
        tablaComparativa.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scroll = new JScrollPane(tablaComparativa);
        scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85)),
                " Tabla Comparativa ",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12)));
        add(scroll, BorderLayout.CENTER);
    }

    private void cargarGrabadores() {
        todosGrabadores = controller.listarTodos();
        listModel.clear();
        for (GrabadorExterno g : todosGrabadores) {
            listModel.addElement(g.getMarca() + " " + g.getModelo() + " (" + g.getAnio() + ")");
        }
    }

    private void comparar() {
        int[] indices = listaGrabadores.getSelectedIndices();
        if (indices.length < 2) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione al menos 2 grabadores para comparar.\n(Use Ctrl+Click para selección múltiple)",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Construir columnas: Especificación + un grabador por columna
        List<GrabadorExterno> seleccionados = new ArrayList<>();
        String[] columnas = new String[indices.length + 1];
        columnas[0] = "Especificación";
        for (int i = 0; i < indices.length; i++) {
            GrabadorExterno g = todosGrabadores.get(indices[i]);
            seleccionados.add(g);
            columnas[i + 1] = g.getMarca() + " " + g.getModelo();
        }

        // Filas de comparación
        String[][] filas = {
                buildRow("Marca", seleccionados, GrabadorExterno::getMarca),
                buildRow("Modelo", seleccionados, GrabadorExterno::getModelo),
                buildRow("Nº Serie", seleccionados, GrabadorExterno::getNumeroSerie),
                buildRow("Año", seleccionados, g -> String.valueOf(g.getAnio())),
                buildRow("Formatos", seleccionados, GrabadorExterno::getFormatos),
                buildRow("Resolución", seleccionados, GrabadorExterno::getResolucion),
                buildRow("Conexiones", seleccionados, GrabadorExterno::getConexiones),
                buildRow("Almacenamiento", seleccionados, GrabadorExterno::getAlmacenamiento),
                buildRow("Precio", seleccionados, g -> String.format("$%.2f", g.getPrecio())),
        };

        compareModel = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (String[] fila : filas) compareModel.addRow(fila);
        tablaComparativa.setModel(compareModel);
    }

    /**
     * Construye una fila de la tabla comparativa con un atributo.
     */
    private String[] buildRow(String label, List<GrabadorExterno> grabadores,
                               java.util.function.Function<GrabadorExterno, String> getter) {
        String[] row = new String[grabadores.size() + 1];
        row[0] = label;
        for (int i = 0; i < grabadores.size(); i++) {
            String val = getter.apply(grabadores.get(i));
            row[i + 1] = val != null ? val : "—";
        }
        return row;
    }
}
