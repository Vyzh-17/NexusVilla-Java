package gui;

import db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ViewNoticesFrame extends JFrame {

    public ViewNoticesFrame(int communityId) {
        setTitle("Community Notices");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(StyleUtils.LIGHT_BG);

        
        add(StyleUtils.header(" Community Notice Board"), BorderLayout.NORTH);

        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(StyleUtils.LIGHT_BG);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(StyleUtils.LIGHT_BG);
        add(scrollPane, BorderLayout.CENTER);

        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT title, message, created_at FROM notices WHERE community_id = ? ORDER BY created_at DESC"
             )) {
            ps.setInt(1, communityId);
            ResultSet rs = ps.executeQuery();
            boolean hasData = false;

            while (rs.next()) {
                hasData = true;
                String title = rs.getString("title");
                String message = rs.getString("message");
                String date = rs.getString("created_at");

                JPanel card = createNoticeCard(title, message, date);
                contentPanel.add(card);
                contentPanel.add(Box.createVerticalStrut(15)); 
            }

            if (!hasData) {
                JLabel emptyLabel = new JLabel("No notices have been posted yet.", SwingConstants.CENTER);
                emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
                emptyLabel.setForeground(Color.GRAY);
                emptyLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
                contentPanel.add(emptyLabel);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading notices: " + e.getMessage());
        }

        
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(StyleUtils.LIGHT_BG);
        JButton close = StyleUtils.dangerBtn("Close");
        bottom.add(close);
        add(bottom, BorderLayout.SOUTH);

        close.addActionListener(e -> dispose());

        setVisible(true);
    }

    
    private JPanel createNoticeCard(String title, String message, String date) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        
        JLabel lblTitle = new JLabel(" " + title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(StyleUtils.PRIMARY);

       
        JTextArea txtMsg = new JTextArea(message);
        txtMsg.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtMsg.setLineWrap(true);
        txtMsg.setWrapStyleWord(true);
        txtMsg.setEditable(false);
        txtMsg.setOpaque(false);

       
        JLabel lblDate = new JLabel(" Posted on: " + date);
        lblDate.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblDate.setForeground(new Color(120, 120, 120));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(txtMsg, BorderLayout.CENTER);
        card.add(lblDate, BorderLayout.SOUTH);

       
        

        return card;
    }
}
