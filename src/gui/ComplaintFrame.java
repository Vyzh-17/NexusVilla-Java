package gui;
import services.ResidentService;
import javax.swing.*; 
import java.awt.*; 
import java.io.File; 
import java.nio.file.*;

public class ComplaintFrame extends JFrame {
    private File selectedFile;
    private JTextField subjectField;
    private JTextArea messageArea;
    private JLabel fileLabel;
    
    // Color scheme
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private final Color BORDER_COLOR = new Color(206, 212, 218);
    private final Color TEXT_COLOR = new Color(33, 37, 41);
    
    public ComplaintFrame(ResidentService svc) {
        initializeFrame();
        setupUI(svc);
        setVisible(true);
    }
    
    private void initializeFrame() {
        setTitle("Submit Complaint - Resident Portal");
        setSize(800, 650);
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
    
    private void setupUI(ResidentService svc) {
        add(createHeader(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        add(createFooter(svc), BorderLayout.SOUTH);
    }
    
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        
        JLabel titleLabel = new JLabel("Submit Complaint");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        
        JLabel descLabel = new JLabel("Please provide details about your concern");
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
        
        // Subject field
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        formPanel.add(createFormLabel("Subject *"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.7;
        subjectField = createStyledTextField();
        formPanel.add(subjectField, gbc);
        
        // Message field
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        formPanel.add(createFormLabel("Message *"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.7;
        gbc.gridheight = 2;
        messageArea = createStyledTextArea();
        formPanel.add(new JScrollPane(messageArea), gbc);
        
        // File attachment
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridheight = 1; gbc.weightx = 0.3;
        formPanel.add(createFormLabel("Attachment"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.7;
        JPanel filePanel = createFileAttachmentPanel();
        formPanel.add(filePanel, gbc);
        
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
        JTextArea area = new JTextArea(6, 20);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        area.setBackground(new Color(252, 252, 252));
        return area;
    }
    
    private JPanel createFileAttachmentPanel() {
        JPanel filePanel = new JPanel(new BorderLayout(10, 0));
        filePanel.setBackground(Color.WHITE);
        
        JButton attachButton = createSecondaryButton("📎 Choose File");
        fileLabel = new JLabel("No file selected");
        fileLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fileLabel.setForeground(Color.GRAY);
        
        attachButton.addActionListener(e -> handleFileSelection());
        
        filePanel.add(attachButton, BorderLayout.WEST);
        filePanel.add(fileLabel, BorderLayout.CENTER);
        
        return filePanel;
    }
    
    private JPanel createFooter(ResidentService svc) {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(BACKGROUND_COLOR);
        footer.setBorder(BorderFactory.createEmptyBorder(15, 25, 25, 25));
        
        JButton cancelButton = createSecondaryButton("Cancel");
        JButton submitButton = createPrimaryButton("Submit Complaint");
        
        cancelButton.addActionListener(e -> dispose());
        submitButton.addActionListener(e -> handleSubmission(svc));
        
        footer.add(cancelButton);
        footer.add(Box.createHorizontalStrut(10));
        footer.add(submitButton);
        
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
    
    private void handleFileSelection() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select file to attach");
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            fileLabel.setText(selectedFile.getName());
            fileLabel.setForeground(TEXT_COLOR);
            fileLabel.setIcon(UIManager.getIcon("FileView.fileIcon"));
        }
    }
    
    private void handleSubmission(ResidentService svc) {
        if (subjectField.getText().isBlank() || messageArea.getText().isBlank()) {
            showErrorDialog("Please fill in all required fields marked with *");
            return;
        }
        
        String filePath = null;
        if (selectedFile != null) {
            try {
                File uploadsDir = new File("uploads");
                if (!uploadsDir.exists()) uploadsDir.mkdir();
                
                String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                filePath = new File(uploadsDir, fileName).getAbsolutePath();
                Files.copy(selectedFile.toPath(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception ex) {
                showErrorDialog("File upload failed: " + ex.getMessage());
                return;
            }
        }
        
        svc.submitComplaint(subjectField.getText().trim(), messageArea.getText().trim(), filePath);
        
        dispose();
    }
    
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Input Required", 
            JOptionPane.WARNING_MESSAGE);
    }
    
   
}
