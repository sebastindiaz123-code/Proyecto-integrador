package com.grabadores.view;

import com.grabadores.controller.UsuarioController;
import com.grabadores.model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Ventana de inicio de sesión del sistema.
 * Implementa autenticación con correo y contraseña (RF02).
 * Diseño moderno con FlatLaf.
 *
 * @author Equipo Lior-Sebastian-Brayan — UNIAJC Grupo 411
 */
public class LoginFrame extends JFrame {

    private final UsuarioController controller;
    private JTextField txtCorreo;
    private JPasswordField txtContrasena;
    private JButton btnLogin;
    private JLabel lblMensaje;

    public LoginFrame() {
        this.controller = new UsuarioController();
        initComponents();
    }

    private void initComponents() {
        setTitle("Sistema de Gestión de Grabadores Externos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 520);
        setLocationRelativeTo(null);
        setResizable(false);

        // Fondo de ventana oscuro completo
        JPanel bgPanel = new JPanel(new GridBagLayout());
        bgPanel.setBackground(new Color(15, 23, 42));

        // Panel principal con fondo de tarjeta (Card)
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(24, 34, 52));
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            new com.formdev.flatlaf.ui.FlatLineBorder(new Insets(1, 1, 1, 1), new Color(51, 65, 85), 1, 20),
            new EmptyBorder(40, 50, 40, 50)
        ));
        
        bgPanel.add(mainPanel);
        setContentPane(bgPanel);

        // Icono / Logo
        JLabel lblIcon = new JLabel("🎬", SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(lblIcon);
        mainPanel.add(Box.createVerticalStrut(10));

        // Título
        JLabel lblTitle = new JLabel("Grabadores Pro");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(new Color(241, 245, 249));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(lblTitle);

        JLabel lblSubtitle = new JLabel("Sistema de Gestión de Video Profesional");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(new Color(148, 163, 184));
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(lblSubtitle);
        mainPanel.add(Box.createVerticalStrut(35));

        // Campo: Correo
        JLabel lblCorreo = new JLabel("Correo electrónico");
        lblCorreo.setForeground(new Color(203, 213, 225));
        lblCorreo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblCorreo.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(lblCorreo);
        mainPanel.add(Box.createVerticalStrut(5));

        txtCorreo = new JTextField();
        txtCorreo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtCorreo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtCorreo.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtCorreo.putClientProperty("JTextField.placeholderText", "usuario@uniajc.edu.co");
        mainPanel.add(txtCorreo);
        mainPanel.add(Box.createVerticalStrut(18));

        // Campo: Contraseña
        JLabel lblPass = new JLabel("Contraseña");
        lblPass.setForeground(new Color(203, 213, 225));
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(lblPass);
        mainPanel.add(Box.createVerticalStrut(5));

        txtContrasena = new JPasswordField();
        txtContrasena.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtContrasena.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtContrasena.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtContrasena.putClientProperty("JTextField.placeholderText", "••••••••");
        mainPanel.add(txtContrasena);
        mainPanel.add(Box.createVerticalStrut(25));

        // Botón Login
        btnLogin = new JButton("Iniciar Sesión");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.setBackground(new Color(125, 178, 170));
        btnLogin.setForeground(new Color(15, 23, 42));
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        mainPanel.add(btnLogin);
        mainPanel.add(Box.createVerticalStrut(15));

        // Mensaje de error/éxito
        lblMensaje = new JLabel(" ");
        lblMensaje.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblMensaje.setForeground(new Color(248, 113, 113));
        lblMensaje.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(lblMensaje);

        mainPanel.add(Box.createVerticalStrut(15));

        // Info de prueba
        JLabel lblInfo = new JLabel("<html><center>Usuario de prueba:<br>juan.charrupi@uniajc.edu.co / 123456</center></html>");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblInfo.setForeground(new Color(148, 163, 184));
        lblInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(lblInfo);

        // Eventos
        btnLogin.addActionListener(e -> realizarLogin());
        txtContrasena.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    realizarLogin();
                }
            }
        });
        txtCorreo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtContrasena.requestFocus();
                }
            }
        });
    }

    /**
     * Ejecuta el proceso de autenticación.
     */
    private void realizarLogin() {
        String correo = txtCorreo.getText().trim();
        String contrasena = new String(txtContrasena.getPassword());

        if (correo.isEmpty() || contrasena.isEmpty()) {
            mostrarError("Por favor complete todos los campos.");
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("Verificando...");

        // Usar SwingWorker para no bloquear la UI
        SwingWorker<Usuario, Void> worker = new SwingWorker<>() {
            @Override
            protected Usuario doInBackground() {
                return controller.login(correo, contrasena);
            }

            @Override
            protected void done() {
                try {
                    Usuario usuario = get();
                    if (usuario != null) {
                        dispose();
                        SwingUtilities.invokeLater(() -> {
                            MainFrame mainFrame = new MainFrame(usuario);
                            mainFrame.setVisible(true);
                        });
                    } else {
                        mostrarError("Correo o contraseña incorrectos.");
                        btnLogin.setEnabled(true);
                        btnLogin.setText("Iniciar Sesión");
                        txtContrasena.setText("");
                        txtContrasena.requestFocus();
                    }
                } catch (Exception ex) {
                    mostrarError("Error de conexión a la base de datos.");
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Iniciar Sesión");
                }
            }
        };
        worker.execute();
    }

    private void mostrarError(String mensaje) {
        lblMensaje.setText(mensaje);
        lblMensaje.setForeground(new Color(248, 113, 113));
    }
}
