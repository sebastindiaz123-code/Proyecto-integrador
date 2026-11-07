package com.grabadores;

import com.formdev.flatlaf.FlatDarkLaf;
import com.grabadores.dao.DatabaseHelper;
import com.grabadores.view.LoginFrame;

import javax.swing.*;

/**
 * Punto de entrada principal de la aplicación.
 * Inicializa el Look & Feel moderno (FlatLaf Dark),
 * la base de datos SQLite y muestra la ventana de login.
 *
 * Sistema de Gestión de Grabadores Externos de Video Profesionales
 * Proyecto Integrador III — UNIAJC Grupo 328
 *
 * @author Juan Sebastián Charrupi, Alan Ledesma, Victor Manuel
 * @version 1.0.0
 */
public class App {

    public static void main(String[] args) {
        // Configurar Look & Feel moderno (FlatMacDarkLaf)
        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.themes.FlatMacDarkLaf());
            // Tipografía global Sans-Serif moderna
            java.awt.Font baseFont = new java.awt.Font("Inter", java.awt.Font.PLAIN, 13);
            if (!java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames().toString().contains("Inter")) {
                baseFont = new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13);
            }
            UIManager.put("defaultFont", baseFont);
            UIManager.put("Component.accentColor", new java.awt.Color(125, 178, 170));
            UIManager.put("Panel.background", new java.awt.Color(24, 34, 52));
            UIManager.put("Panel.foreground", new java.awt.Color(226, 232, 240));
            UIManager.put("Table.background", new java.awt.Color(24, 34, 52));
            UIManager.put("Table.foreground", new java.awt.Color(226, 232, 240));
            UIManager.put("TextField.background", new java.awt.Color(30, 41, 59));
            UIManager.put("TextField.foreground", new java.awt.Color(226, 232, 240));
            UIManager.put("ComboBox.background", new java.awt.Color(30, 41, 59));
            UIManager.put("ComboBox.foreground", new java.awt.Color(226, 232, 240));

            // Personalización de componentes
            UIManager.put("Button.arc", 12);
            UIManager.put("Component.arc", 10);
            UIManager.put("TextComponent.arc", 10);
            UIManager.put("Panel.arc", 15);
            UIManager.put("ScrollBar.thumbArc", 999);
            UIManager.put("ScrollBar.thumbInsets", new java.awt.Insets(2, 2, 2, 2));
            
            // Tablas con contraste suave y selección verde azulado.
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.showVerticalLines", false);
            UIManager.put("Table.alternateRowColor", new java.awt.Color(30, 41, 59));
            UIManager.put("Table.selectionBackground", new java.awt.Color(42, 75, 78));
            UIManager.put("Table.selectionForeground", java.awt.Color.WHITE);
            UIManager.put("Table.rowHeight", 32);
            UIManager.put("TableHeader.font", baseFont.deriveFont(java.awt.Font.BOLD, 13f));
            UIManager.put("TableHeader.background", new java.awt.Color(30, 41, 59));
            UIManager.put("TableHeader.bottomSeparatorColor", new java.awt.Color(125, 178, 170));
            
            // Textfields
            UIManager.put("TextComponent.arc", 12);
            UIManager.put("Component.focusWidth", 2);
            UIManager.put("Component.innerFocusWidth", 0);
            UIManager.put("Component.focusedBorderColor", new java.awt.Color(125, 178, 170));
        } catch (Exception e) {
            System.err.println("[App] No se pudo cargar FlatMacDarkLaf, usando tema por defecto.");
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                // Usar el Look & Feel por defecto
            }
        }

        // Inicializar la base de datos en un hilo separado
        SwingUtilities.invokeLater(() -> {
            // Splash de carga
            JDialog splash = new JDialog();
            splash.setUndecorated(true);
            splash.setSize(360, 160);
            splash.setLocationRelativeTo(null);

            JPanel splashPanel = new JPanel();
            splashPanel.setLayout(new BoxLayout(splashPanel, BoxLayout.Y_AXIS));
            splashPanel.setBackground(new java.awt.Color(15, 23, 42));
            splashPanel.setBorder(BorderFactory.createLineBorder(new java.awt.Color(125, 178, 170), 2));

            JLabel lblEmoji = new JLabel("🎬", SwingConstants.CENTER);
            lblEmoji.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.PLAIN, 40));
            lblEmoji.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
            splashPanel.add(Box.createVerticalStrut(20));
            splashPanel.add(lblEmoji);

            JLabel lblCarga = new JLabel("Iniciando Grabadores Pro...");
            lblCarga.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
            lblCarga.setForeground(new java.awt.Color(203, 213, 225));
            lblCarga.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
            splashPanel.add(Box.createVerticalStrut(10));
            splashPanel.add(lblCarga);

            JProgressBar progressBar = new JProgressBar();
            progressBar.setIndeterminate(true);
            progressBar.setMaximumSize(new java.awt.Dimension(280, 8));
            progressBar.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
            splashPanel.add(Box.createVerticalStrut(15));
            splashPanel.add(progressBar);

            splash.setContentPane(splashPanel);
            splash.setVisible(true);

            // Inicializar BD en background
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    DatabaseHelper.getInstance().initializeDatabase();
                    return null;
                }

                @Override
                protected void done() {
                    splash.dispose();
                    // Mostrar ventana de login
                    LoginFrame loginFrame = new LoginFrame();
                    loginFrame.setVisible(true);
                }
            }.execute();
        });
    }
}
