package com.grabadores.view;

import com.grabadores.dao.DatabaseHelper;
import com.grabadores.model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Ventana principal del sistema con navegación estilo Dashboard.
 * Contiene todos los paneles de gestión: Grabadores, Cámaras,
 * Inventario, Accesorios, Compatibilidad, Reportes y Comparador.
 *
 * @author Equipo Lior-Sebastian-Brayan — UNIAJC Grupo 411
 */
public class MainFrame extends JFrame {

    private final Usuario usuarioActual;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private ButtonGroup sidebarGroup;

    public MainFrame(Usuario usuario) {
        this.usuarioActual = usuario;
        initComponents();
    }

    private void initComponents() {
        setTitle("Grabadores Pro — Sistema de Gestión Premium");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1250, 780);
        setMinimumSize(new Dimension(1050, 650));
        setLocationRelativeTo(null);

        // Cerrar con confirmación
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                int opt = JOptionPane.showConfirmDialog(
                        MainFrame.this,
                        "¿Desea cerrar la aplicación?",
                        "Confirmar salida",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (opt == JOptionPane.YES_OPTION) {
                    DatabaseHelper.getInstance().closeConnection();
                    System.exit(0);
                }
            }
        });

        setLayout(new BorderLayout());

        // ==================== BARRA SUPERIOR (HEADER) ====================
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(15, 23, 42));
        topBar.setBorder(new EmptyBorder(12, 25, 12, 25));

        // Título Logo
        JLabel lblLogo = new JLabel("🎬 Grabadores Pro");
        lblLogo.setFont(new Font("Inter", Font.BOLD, 18));
        lblLogo.setForeground(new Color(241, 245, 249));
        
        // Info usuario
        JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        userInfoPanel.setOpaque(false);
        
        JLabel lblUsuario = new JLabel("Hola, " + usuarioActual.getNombre() + " (" + usuarioActual.getRol() + ")");
        lblUsuario.setFont(new Font("Inter", Font.PLAIN, 13));
        lblUsuario.setForeground(new Color(203, 213, 225));
        userInfoPanel.add(lblUsuario);

        JButton btnLogout = new JButton("Salir");
        btnLogout.setFont(new Font("Inter", Font.BOLD, 12));
        btnLogout.setForeground(new Color(248, 113, 113));
        btnLogout.setBackground(new Color(39, 52, 74));
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.putClientProperty("JButton.buttonType", "roundRect");
        btnLogout.addActionListener(e -> cerrarSesion());
        userInfoPanel.add(btnLogout);

        topBar.add(lblLogo, BorderLayout.WEST);
        topBar.add(userInfoPanel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // ==================== SIDEBAR Y CONTENIDO ====================
        JPanel mainArea = new JPanel(new BorderLayout());
        
        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(24, 34, 52));
        sidebar.setBorder(new EmptyBorder(20, 15, 20, 15));
        sidebar.setPreferredSize(new Dimension(220, 0));

        JLabel lblMenu = new JLabel("MENÚ PRINCIPAL");
        lblMenu.setFont(new Font("Inter", Font.BOLD, 11));
        lblMenu.setForeground(new Color(148, 163, 184));
        lblMenu.setBorder(new EmptyBorder(0, 10, 10, 0));
        lblMenu.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(lblMenu);

        // Panel de Contenido con CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(15, 23, 42));
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        sidebarGroup = new ButtonGroup();

        // Agregar secciones
        addSidebarItem(sidebar, "📹  Grabadores", "grabadores", new GrabadorPanel(usuarioActual));
        addSidebarItem(sidebar, "📷  Cámaras", "camaras", new CamaraPanel(usuarioActual));
        addSidebarItem(sidebar, "📦  Inventario", "inventario", new InventarioPanel(usuarioActual));
        addSidebarItem(sidebar, "🔧  Accesorios", "accesorios", new AccesorioPanel(usuarioActual));
        addSidebarItem(sidebar, "🔗  Compatibilidad", "compatibilidad", new CompatibilidadPanel(usuarioActual));
        addSidebarItem(sidebar, "⚖️  Comparador", "comparador", new ComparadorPanel());
        
        sidebar.add(Box.createVerticalStrut(20));
        JLabel lblAdmin = new JLabel("REPORTES Y SISTEMA");
        lblAdmin.setFont(new Font("Inter", Font.BOLD, 11));
        lblAdmin.setForeground(new Color(148, 163, 184));
        lblAdmin.setBorder(new EmptyBorder(0, 10, 10, 0));
        lblAdmin.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(lblAdmin);
        
        addSidebarItem(sidebar, "📊  Generar Reportes", "reportes", new ReportePanel(usuarioActual));
        
        // Control de Acceso por Roles (RF11)
        if (usuarioActual.esAdministrador()) {
            addSidebarItem(sidebar, "👥  Gestión de Usuarios", "usuarios", new UsuarioPanel(usuarioActual));
        }

        // Seleccionar el primero por defecto
        if (sidebarGroup.getElements().hasMoreElements()) {
            sidebarGroup.getElements().nextElement().setSelected(true);
        }

        mainArea.add(sidebar, BorderLayout.WEST);
        mainArea.add(contentPanel, BorderLayout.CENTER);
        add(mainArea, BorderLayout.CENTER);

        // ==================== BARRA INFERIOR ====================
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(15, 23, 42));
        statusBar.setBorder(new EmptyBorder(8, 25, 8, 25));

        JLabel lblStatus = new JLabel("Grabadores Pro v1.0  |  UNIAJC 2026");
        lblStatus.setFont(new Font("Inter", Font.PLAIN, 11));
        lblStatus.setForeground(new Color(148, 163, 184));
        statusBar.add(lblStatus, BorderLayout.WEST);

        JLabel lblDB = new JLabel("● Conectado a SQLite");
        lblDB.setFont(new Font("Inter", Font.BOLD, 11));
        lblDB.setForeground(new Color(137, 190, 164));
        statusBar.add(lblDB, BorderLayout.EAST);

        add(statusBar, BorderLayout.SOUTH);
    }

    private void addSidebarItem(JPanel sidebar, String text, String cardName, JPanel panel) {
        JToggleButton btn = new JToggleButton(text);
        btn.setFont(new Font("Inter", Font.PLAIN, 14));
        btn.setForeground(new Color(226, 232, 240));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        // Usar propiedad cliente de FlatLaf para botones de menú
        btn.putClientProperty("JButton.buttonType", "borderless");
        btn.putClientProperty("JToggleButton.buttonType", "borderless");
        btn.putClientProperty("JToggleButton.selectedBackground", new Color(42, 75, 78));
        btn.putClientProperty("JToggleButton.selectedForeground", new Color(159, 207, 194));
        
        btn.addActionListener(e -> cardLayout.show(contentPanel, cardName));
        
        sidebarGroup.add(btn);
        sidebar.add(btn);
        sidebar.add(Box.createVerticalStrut(5));
        
        contentPanel.add(panel, cardName);
    }

    private void cerrarSesion() {
        int opt = JOptionPane.showConfirmDialog(
                this, "¿Desea cerrar sesión?", "Cerrar sesión",
                JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                LoginFrame login = new LoginFrame();
                login.setVisible(true);
            });
        }
    }
}
