package gui;

import db.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class LoginFrame extends JFrame {
    private JComboBox<String> communityCombo;
    private Map<String, Integer> communityMap;

    public LoginFrame() {
        setTitle("NexusVilla - Login");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(249, 250, 251));
        
        setupUI();
        setVisible(true);
    }

    private void setupUI() {
       
        add(createMainContent(), BorderLayout.CENTER);
    }

    

    private JPanel createMainContent() {
        JPanel main = new JPanel(new GridBagLayout());
        main.setBackground(new Color(249, 250, 251));
        main.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // Left side - Illustration/Info
        JPanel leftPanel = createLeftPanel();
        
        // Right side - Login Form
        JPanel rightPanel = createLoginForm();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        main.add(leftPanel, gbc);
        
        gbc.gridx = 1;
        gbc.insets = new Insets(0, 40, 0, 0);
        main.add(rightPanel, gbc);

        return main;
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(249, 250, 251));
        panel.setBorder(BorderFactory.createEmptyBorder(80, 40, 80, 40));

        JLabel illustration = new JLabel("🏠", SwingConstants.CENTER);
        illustration.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 120));
        illustration.setForeground(new Color(59, 130, 246));

        JLabel title = new JLabel("Welcome to NexusVilla", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(new Color(31, 41, 55));
        title.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));

        JLabel subtitle = new JLabel("<html><div style='text-align: center; color: #6b7280; font-size: 16px; line-height: 1.6;'>" +
                "Your gateway to secure and connected community living", 
                SwingConstants.CENTER);

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(new Color(249, 250, 251));
        textPanel.add(title, BorderLayout.NORTH);
        textPanel.add(subtitle, BorderLayout.CENTER);

        panel.add(illustration, BorderLayout.CENTER);
        panel.add(textPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createLoginForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235)),
            BorderFactory.createEmptyBorder(60, 50, 60, 50)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Form Title
        JLabel formTitle = new JLabel("Sign In to Your Account");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        formTitle.setForeground(new Color(31, 41, 55));
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(0, 0, 30, 0);
        panel.add(formTitle, gbc);

        // Username
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 8, 0);
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userLabel.setForeground(new Color(55, 65, 81));
        panel.add(userLabel, gbc);

        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 20, 0);
        JTextField username = createFormField();
        panel.add(username, gbc);

        // Password
        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 8, 0);
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        passLabel.setForeground(new Color(55, 65, 81));
        panel.add(passLabel, gbc);

        gbc.gridy = 4; gbc.insets = new Insets(0, 0, 20, 0);
        JPasswordField password = createPasswordField();
        panel.add(password, gbc);

        // Community
        gbc.gridy = 5; gbc.insets = new Insets(0, 0, 8, 0);
        JLabel commLabel = new JLabel("Community");
        commLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        commLabel.setForeground(new Color(55, 65, 81));
        panel.add(commLabel, gbc);

        gbc.gridy = 6; gbc.insets = new Insets(0, 0, 30, 0);
        communityCombo = createComboBox();
        loadCommunities();
        panel.add(communityCombo, gbc);

        // Login Button
        gbc.gridy = 7;
        JButton loginBtn = createLoginButton();
        panel.add(loginBtn, gbc);

        // Login action
        loginBtn.addActionListener(e -> {
            String user = username.getText().trim();
            String pass = new String(password.getPassword()).trim();
            String community = (String) communityCombo.getSelectedItem();
            
            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both username and password", 
                    "Missing Information", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (!communityMap.containsKey(community)) {
                JOptionPane.showMessageDialog(this, "Please select a valid community", 
                    "Community Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            authenticate(user, pass, communityMap.get(community));
        });

        // Enter key support
        username.addActionListener(e -> password.requestFocus());
        password.addActionListener(e -> loginBtn.doClick());

        return panel;
    }

    private JTextField createFormField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(300, 45));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219)),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setPreferredSize(new Dimension(300, 45));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219)),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        return field;
    }

    private JComboBox<String> createComboBox() {
        JComboBox<String> combo = new JComboBox<>(new String[]{"Loading communities..."});
        combo.setPreferredSize(new Dimension(300, 45));
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219)),
            BorderFactory.createEmptyBorder(0, 12, 0, 12)
        ));
        return combo;
    }

    private JButton createLoginButton() {
        JButton button = new JButton("Sign In");
        button.setPreferredSize(new Dimension(300, 50));
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(59, 130, 246));
        button.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(37, 99, 235));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(59, 130, 246));
            }
        });

        return button;
    }

    private void loadCommunities() {
        communityMap = new HashMap<>();
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, community_name FROM gated_communities ORDER BY community_name")) {
            
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            while (rs.next()) {
                String name = rs.getString("community_name");
                int id = rs.getInt("id");
                model.addElement(name);
                communityMap.put(name, id);
            }
            communityCombo.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void authenticate(String username, String password, int communityId) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                 "SELECT id, role, house_no FROM users WHERE username=? AND password=MD5(?) AND community_id=?")) {
            
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setInt(3, communityId);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("id");
                String role = rs.getString("role");
                String houseNo = rs.getString("house_no");
                
                dispose();
                switch (role) {
                    case "admin" -> new AdminGUI(userId, communityId);
                    case "resident" -> new ResidentGUI(userId, communityId, houseNo);
                    case "security" -> new SecurityGUI(userId, communityId);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password", 
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), 
                "System Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}