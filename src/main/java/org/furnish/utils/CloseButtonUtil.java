package org.furnish.utils;

import javax.swing.*;
import java.awt.*;

public class CloseButtonUtil {
    public static JButton createCloseButton() {
        JButton closeButton = new JButton();
        closeButton.setContentAreaFilled(false);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        try {
            ImageIcon closeIcon = new ImageIcon(CloseButtonUtil.class.getResource("../images/close.png"));
            if (closeIcon.getImage() != null) {
                closeButton.setIcon(closeIcon);
            } else {
                closeButton.setText("✕");
                closeButton.setFont(new Font("Arial", Font.PLAIN, 18));
            }
        } catch (Exception e) {
            closeButton.setText("✕");
            closeButton.setFont(new Font("Arial", Font.PLAIN, 18));
        }

        closeButton.setForeground(Color.WHITE);
        closeButton.setRolloverEnabled(true);

        // Hover effect
        closeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                closeButton.setForeground(new Color(255, 100, 100));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                closeButton.setForeground(Color.WHITE);
            }
        });

        closeButton.addActionListener(e -> System.exit(0));
        return closeButton;
    }
}
