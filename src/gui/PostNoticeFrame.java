package gui;
import services.AdminService;
import javax.swing.*; 
import java.awt.*;

public class PostNoticeFrame extends JFrame {
    private JTextField titleField;
    private JTextArea messageArea;
    
    // Color scheme matching ComplaintFrame
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private final Color BORDER_COLOR = new Color(206, 212, 218);
    private final Color TEXT_COLOR = new Color(33, 37, 41);
    
    public PostNoticeFrame(AdminService svc, int communityId) {
        initializeFrame();
        setupUI(svc, communityId);
        setVisible(true);
    }
    
    private void initializeFrame() {
        setTitle("Post Notice - Admin Portal");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Set application icon (if available)
        try {
            setIconImage(new ImageIcon("assets/icon.png").getImage());
        } catch (Exception e) {
            // Use default icon if custom icon not available
        }
    }
    
    private void setupUI(AdminService svc, int communityId) {
        add(createHeader(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        add(createFooter(svc, communityId), BorderLayout.SOUTH);
    }
    
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        
        JLabel titleLabel = new JLabel("Post Notice");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        
        JLabel descLabel = new JLabel("Create a new notice for your community residents");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(new Color(240, 240, 240));
        descLabel.setHorizontalAlignment(SwingConstants.LEFT);
        
        header.add(titleLabel, BorderLayout.NORTH);
        header.add(descLabel, BorderLayout.SOUTH);
        
        return header;
    }
    
    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        
        // Title field
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.2;
        formPanel.add(createFormLabel("Title *"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.8;
        titleField = createStyledTextField();
        formPanel.add(titleField, gbc);
        
        // Message field
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.2;
        formPanel.add(createFormLabel("Message *"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.8;
        gbc.gridheight = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        messageArea = createStyledTextArea();
        JScrollPane scrollPane = new JScrollPane(messageArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scrollPane.setPreferredSize(new Dimension(400, 200));
        formPanel.add(scrollPane, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        return mainPanel;
    }
    
    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_COLOR);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }
    
    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        field.setBackground(new Color(252, 252, 252));
        return field;
    }
    
    private JTextArea createStyledTextArea() {
        JTextArea area = new JTextArea(8, 20);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        area.setBackground(new Color(252, 252, 252));
        return area;
    }
    
    private JPanel createFooter(AdminService svc, int communityId) {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(BACKGROUND_COLOR);
        footer.setBorder(BorderFactory.createEmptyBorder(15, 25, 25, 25));
        
        JButton cancelButton = createSecondaryButton("Cancel");
        JButton postButton = createPrimaryButton("Post Notice");
        
        cancelButton.addActionListener(e -> dispose());
        postButton.addActionListener(e -> handlePostNotice(svc, communityId));
        
        footer.add(cancelButton);
        footer.add(Box.createHorizontalStrut(10));
        footer.add(postButton);
        
        return footer;
    }
    
    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR.darker(), 1),
            BorderFactory.createEmptyBorder(12, 25, 12, 25)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(SECONDARY_COLOR);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR);
            }
        });
        
        return button;
    }
    
    private JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setBackground(Color.WHITE);
        button.setForeground(TEXT_COLOR);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private void handlePostNotice(AdminService svc, int communityId) {
        if (titleField.getText().isBlank() || messageArea.getText().isBlank()) {
            showErrorDialog("Please fill in all required fields marked with *");
            return;
        }
        
        svc.postNotice(titleField.getText().trim(), messageArea.getText().trim(), communityId);
        showSuccessDialog();
        dispose();
    }
    
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Input Required", 
            JOptionPane.WARNING_MESSAGE);
    }
    
    private void showSuccessDialog() {
        JOptionPane.showMessageDialog(this, 
            "Notice has been posted successfully to the community.", 
            "Notice Posted", 
            JOptionPane.INFORMATION_MESSAGE);
    }
}
