package com.grabadores.view;

import com.grabadores.dao.ImagenGrabadorDAO;
import com.grabadores.model.ImagenGrabador;
import com.grabadores.model.GrabadorExterno;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ImagenesGrabadorDialog extends JDialog {

    private static final String[] VISTAS = {"FRONTAL", "POSTERIOR", "LATERAL"};
    private final ImagenGrabadorDAO dao = new ImagenGrabadorDAO();
    private final GrabadorExterno grabador;
    private final boolean editable;
    private final Map<String, JLabel> previews = new HashMap<>();
    private final Map<String, JLabel> rutas = new HashMap<>();

    public ImagenesGrabadorDialog(Window owner, GrabadorExterno grabador, boolean editable) {
        super(owner, "Imágenes — " + grabador, ModalityType.APPLICATION_MODAL);
        this.grabador = grabador;
        this.editable = editable;
        initComponents();
        cargarImagenes();
    }

    private void initComponents() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(780, 560);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(12, 12));

        JPanel content = new JPanel(new GridLayout(1, VISTAS.length, 10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        for (String vista : VISTAS) {
            content.add(crearTarjeta(vista));
        }
        add(content, BorderLayout.CENTER);

        JLabel ayuda = new JLabel(editable
                ? "Se admite una imagen por vista. Los archivos permanecen en su ubicación original."
                : "Imágenes de referencia del equipo.");
        ayuda.setBorder(BorderFactory.createEmptyBorder(0, 16, 12, 16));
        add(ayuda, BorderLayout.SOUTH);
    }

    private JPanel crearTarjeta(String vista) {
        JPanel card = new JPanel(new BorderLayout(6, 8));
        card.setBorder(BorderFactory.createTitledBorder(vista));
        JLabel preview = new JLabel("Sin imagen", SwingConstants.CENTER);
        preview.setPreferredSize(new Dimension(220, 320));
        preview.setBorder(BorderFactory.createLineBorder(new Color(51, 65, 85)));
        previews.put(vista, preview);
        card.add(preview, BorderLayout.CENTER);

        JLabel ruta = new JLabel(" ");
        rutas.put(vista, ruta);
        card.add(ruta, BorderLayout.NORTH);

        if (editable) {
            JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            JButton seleccionar = new JButton("Seleccionar");
            seleccionar.addActionListener(e -> seleccionarImagen(vista));
            JButton quitar = new JButton("Quitar");
            quitar.addActionListener(e -> quitarImagen(vista));
            actions.add(seleccionar);
            actions.add(quitar);
            card.add(actions, BorderLayout.SOUTH);
        }
        return card;
    }

    private void cargarImagenes() {
        Map<String, ImagenGrabador> imagenPorVista = new HashMap<>();
        for (ImagenGrabador imagen : dao.listarPorGrabador(grabador.getId())) {
            imagenPorVista.put(imagen.getTipoVista(), imagen);
        }
        for (String vista : VISTAS) {
            ImagenGrabador imagen = imagenPorVista.get(vista);
            if (imagen != null) mostrarImagen(vista, new File(imagen.getRuta()));
        }
    }

    private void seleccionarImagen(String vista) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Seleccionar imagen " + vista.toLowerCase());
        chooser.setFileFilter(new FileNameExtensionFilter("Imágenes (JPG, PNG, GIF)", "jpg", "jpeg", "png", "gif"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File archivo = chooser.getSelectedFile();
        ImageIcon selectedImage = new ImageIcon(archivo.getAbsolutePath());
        if (selectedImage.getIconWidth() <= 0 || selectedImage.getIconHeight() <= 0) {
            JOptionPane.showMessageDialog(this, "El archivo seleccionado no es una imagen válida.",
                    "Imagen inválida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!dao.guardar(grabador.getId(), vista, archivo.getAbsolutePath())) {
            JOptionPane.showMessageDialog(this, "No se pudo guardar la ruta de la imagen.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        mostrarImagen(vista, archivo);
    }

    private void quitarImagen(String vista) {
        if (dao.eliminar(grabador.getId(), vista)) {
            previews.get(vista).setIcon(null);
            previews.get(vista).setText("Sin imagen");
            rutas.get(vista).setText(" ");
        }
    }

    private void mostrarImagen(String vista, File archivo) {
        if (!archivo.isFile()) {
            previews.get(vista).setIcon(null);
            previews.get(vista).setText("Archivo no encontrado");
            rutas.get(vista).setText(archivo.getName());
            return;
        }
        ImageIcon original = new ImageIcon(archivo.getAbsolutePath());
        if (original.getIconWidth() <= 0 || original.getIconHeight() <= 0) {
            previews.get(vista).setIcon(null);
            previews.get(vista).setText("Imagen no válida");
            rutas.get(vista).setText(archivo.getName());
            return;
        }
        Image scaled = original.getImage().getScaledInstance(210, 290, Image.SCALE_SMOOTH);
        previews.get(vista).setText("");
        previews.get(vista).setIcon(new ImageIcon(scaled));
        rutas.get(vista).setText(archivo.getName());
    }
}
