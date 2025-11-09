package gui;

import javax.swing.*;
import java.awt.*;

public class StyleUtils {
  
    public static final Color PRIMARY = new Color(33, 82, 142);
    public static final Color ACCENT = new Color(46, 204, 113);
    public static final Color DANGER = new Color(220, 53, 69);
    public static final Color LIGHT_BG = new Color(245, 247, 250);


    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.PLAIN, 14);


    public static JPanel header(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        JLabel label = new JLabel(title);
        label.setForeground(Color.WHITE);
        label.setFont(TITLE_FONT);

        panel.add(label, BorderLayout.WEST);
        return panel;
    }

    
    public static JButton primaryBtn(String text) {
        JButton button = new JButton(text);
        button.setBackground(PRIMARY);
        button.setForeground(Color.WHITE);
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        return button;
    }

  
    public static JButton successBtn(String text) {
        JButton button = new JButton(text);
        button.setBackground(ACCENT);
        button.setForeground(Color.WHITE);
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        return button;
    }

    
    public static JButton dangerBtn(String text) {
        JButton button = new JButton(text);
        button.setBackground(DANGER);
        button.setForeground(Color.WHITE);
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        return button;
    }
}
