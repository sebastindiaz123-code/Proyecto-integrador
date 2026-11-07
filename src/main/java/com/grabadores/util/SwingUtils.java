package com.grabadores.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;

public final class SwingUtils {

    private SwingUtils() {
    }

    public static void setEnabledRecursively(Component component, boolean enabled) {
        component.setEnabled(enabled);
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                setEnabledRecursively(child, enabled);
            }
        }
    }

    public static JScrollPane createVerticalScrollPane(Component content, int preferredWidth) {
        JScrollPane scrollPane = new JScrollPane(content,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(preferredWidth, 0));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }
}
