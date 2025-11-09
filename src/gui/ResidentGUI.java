package gui;

import services.ResidentService;
import services.AdminService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ResidentGUI extends JFrame {
    private final ResidentService service;
    private final int residentId;
    private final int communityId;
    private final String houseNo;
    private String communityName;

    public ResidentGUI(int residentId, int communityId, String houseNo) {
        this.residentId = residentId;
        this.communityId = communityId;
        this.houseNo = houseNo;
        this.communityName = getCommunityName();
        this.service = new ResidentService(residentId, communityId, houseNo);

        initializeFrame();
        setupUI();
        setVisible(true);
    }

    private void initializeFrame() {
        setTitle("NexusVilla - Resident Dashboard");
        setSize(1300, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(249, 250, 251));
    }

    private void setupUI() {
        add(createHeader(), BorderLayout.NORTH);
        add(createSidebar(), BorderLayout.WEST);
        add(createMainContent(), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(229, 231, 235)));
        header.setPreferredSize(new Dimension(0, 70));

        JLabel title = new JLabel("Resident Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(31, 41, 55));
        title.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));

        JPanel userInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        userInfo.setBackground(Color.WHITE);
        
        JLabel residentLabel = new JLabel("Resident");
        residentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        residentLabel.setForeground(new Color(107, 114, 128));
        
        JLabel houseLabel = new JLabel("House " + houseNo);
        houseLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        houseLabel.setForeground(Color.WHITE);
        houseLabel.setBackground(new Color(16, 185, 129));
        houseLabel.setOpaque(true);
        houseLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(5, 150, 105), 1),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));

        userInfo.add(residentLabel);
        userInfo.add(houseLabel);
        
        header.add(title, BorderLayout.WEST);
        header.add(userInfo, BorderLayout.EAST);
        
        return header;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(280, 0));
        sidebar.setBackground(Color.WHITE);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(229, 231, 235)));

        // Logo
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        logoPanel.setBackground(Color.WHITE);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(30, 25, 30, 25));
        
        JLabel logoText = new JLabel("NexusVilla");
        logoText.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logoText.setForeground(new Color(59, 130, 246));
        
        logoPanel.add(logoText);
        sidebar.add(logoPanel);

        // User info in sidebar
        JPanel userPanel = new JPanel(new BorderLayout(10, 5));
        userPanel.setBackground(Color.WHITE);
        userPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235)),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        userPanel.setMaximumSize(new Dimension(240, 80));

        JLabel userIcon = new JLabel("👤");
        userIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        userIcon.setForeground(new Color(59, 130, 246));

        JLabel userInfo = new JLabel("<html><b style='color:#1e293b;font-size:12px;'>Resident</b><br>" +
                "<span style='color:#64748b;font-size:11px;'>House " + houseNo + "</span><br>" +
                "<span style='color:#64748b;font-size:10px;'>" + communityName + "</span></html>");

        userPanel.add(userIcon, BorderLayout.WEST);
        userPanel.add(userInfo, BorderLayout.CENTER);
        sidebar.add(userPanel);
        sidebar.add(Box.createVerticalStrut(20));

        // Navigation
        String[] menuItems = {
            "Pre-authorize Visitor", "View My Visitors", 
            "Pending Visitors", "Submit Complaint", "View Notices",
            "Logout"
        };

        String[] icons = {"👥", "📋", "⏳", "💬", "📢", "🚪"};

        for (int i = 0; i < menuItems.length; i++) {
            boolean isLogout = menuItems[i].equals("Logout");
            sidebar.add(createMenuButton(icons[i], menuItems[i], false, isLogout));
        }

        return sidebar;
    }

    private JPanel createMenuButton(String icon, String text, boolean active, boolean isLogout) {
        JPanel buttonPanel = new JPanel(new BorderLayout(15, 0));
        
        if (isLogout) {
            buttonPanel.setBackground(new Color(254, 242, 242)); // Light red background
        } else {
            buttonPanel.setBackground(Color.WHITE);
        }
        
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        buttonPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        if (isLogout) {
            iconLabel.setForeground(new Color(220, 38, 38)); // Red icon for logout
        } else {
            iconLabel.setForeground(new Color(107, 114, 128));
        }

        JLabel textLabel = new JLabel(text);
        if (isLogout) {
            textLabel.setForeground(new Color(220, 38, 38)); // Red text for logout
            textLabel.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Bold for logout
        } else {
            textLabel.setForeground(new Color(75, 85, 99));
            textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }

        buttonPanel.add(iconLabel, BorderLayout.WEST);
        buttonPanel.add(textLabel, BorderLayout.CENTER);

        buttonPanel.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (isLogout) {
                    buttonPanel.setBackground(new Color(254, 226, 226)); // Darker red on hover
                } else {
                    buttonPanel.setBackground(new Color(249, 250, 251));
                }
            }
            public void mouseExited(MouseEvent e) {
                if (isLogout) {
                    buttonPanel.setBackground(new Color(254, 242, 242)); // Light red background
                } else {
                    buttonPanel.setBackground(Color.WHITE);
                }
            }
            public void mouseClicked(MouseEvent e) {
                handleNavigation(text);
            }
        });

        return buttonPanel;
    }

    private JPanel createMainContent() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(249, 250, 251));
        main.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        main.add(createWelcomeSection(), BorderLayout.NORTH);
        main.add(createStatsGrid(), BorderLayout.CENTER);
        main.add(createQuickActions(), BorderLayout.SOUTH);

        return main;
    }

    private JPanel createWelcomeSection() {
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(new Color(249, 250, 251));
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        JLabel welcome = new JLabel("Welcome back, Resident");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcome.setForeground(new Color(31, 41, 55));

        JLabel subtitle = new JLabel("House " + houseNo + " • " + communityName);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(new Color(107, 114, 128));

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(new Color(249, 250, 251));
        textPanel.add(welcome, BorderLayout.NORTH);
        textPanel.add(Box.createVerticalStrut(8), BorderLayout.CENTER);
        textPanel.add(subtitle, BorderLayout.SOUTH);

        welcomePanel.add(textPanel, BorderLayout.WEST);
        return welcomePanel;
    }

    private JPanel createStatsGrid() {
        JPanel grid = new JPanel(new GridLayout(2, 2, 20, 20));
        grid.setBackground(new Color(249, 250, 251));
        grid.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        grid.add(createStatCard("Today's Visitors", "3", new Color(59, 130, 246), "👥"));
        grid.add(createStatCard("Pending Approvals", "2", new Color(245, 158, 11), "⏳"));
        grid.add(createStatCard("Active Notices", "5", new Color(16, 185, 129), "📢"));
        grid.add(createStatCard("My Complaints", "1", new Color(239, 68, 68), "💬"));

        return grid;
    }

    private JPanel createStatCard(String title, String value, Color color, String icon) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235)),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        // Top section with icon and title
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconLabel.setForeground(color);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(107, 114, 128));

        topPanel.add(iconLabel, BorderLayout.WEST);
        topPanel.add(Box.createHorizontalStrut(15), BorderLayout.CENTER);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        
        card.add(topPanel, BorderLayout.NORTH);

        // Value
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(new Color(31, 41, 55));
        valueLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        card.add(valueLabel, BorderLayout.CENTER);

        // Add hover effect
        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(color),
                    BorderFactory.createEmptyBorder(25, 25, 25, 25)
                ));
            }
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(229, 231, 235)),
                    BorderFactory.createEmptyBorder(25, 25, 25, 25)
                ));
            }
        });

        return card;
    }

    private JPanel createQuickActions() {
        JPanel actionsPanel = new JPanel(new BorderLayout());
        actionsPanel.setBackground(new Color(249, 250, 251));

        JLabel title = new JLabel("Quick Actions");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(31, 41, 55));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel actionsGrid = new JPanel(new GridLayout(1, 3, 20, 20));
        actionsGrid.setBackground(new Color(249, 250, 251));

        actionsGrid.add(createActionCard("Pre-authorize Visitor", "", "👥"));
        actionsGrid.add(createActionCard("Submit Complaint", "", "💬"));
        actionsGrid.add(createActionCard("View Notices", "", "📢"));

        actionsPanel.add(title, BorderLayout.NORTH);
        actionsPanel.add(actionsGrid, BorderLayout.CENTER);

        return actionsPanel;
    }

    private JPanel createActionCard(String title, String description, String icon) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconLabel.setForeground(new Color(59, 130, 246));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(31, 41, 55));

        JLabel descLabel = new JLabel("<html><div style='width:200px;color:#6b7280;font-size:14px;line-height:1.4;'>" + description + "</div></html>");
        descLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.add(titleLabel, BorderLayout.NORTH);
        content.add(descLabel, BorderLayout.CENTER);

        card.add(iconLabel, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(249, 250, 251));
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(59, 130, 246)),
                    BorderFactory.createEmptyBorder(20, 20, 20, 20)
                ));
            }
            public void mouseExited(MouseEvent e) {
                card.setBackground(Color.WHITE);
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(229, 231, 235)),
                    BorderFactory.createEmptyBorder(20, 20, 20, 20)
                ));
            }
            public void mouseClicked(MouseEvent e) {
                handleQuickAction(title);
            }
        });

        return card;
    }

    private void handleNavigation(String text) {
        switch (text) {
            case "Pre-authorize Visitor":
                new PreAuthorizeFrame(service);
                break;
            case "View My Visitors":
                new ViewMyVisitorsFrame(service.getResidentId());
                break;
            case "Pending Visitors":
                service.showPendingVisitors();
                break;
            case "Submit Complaint":
                new ComplaintFrame(service);
                break;
            case "View Notices":
                new ViewNoticesFrame(communityId);
                break;
            case "Logout":
                dispose();
                new LoginFrame();
                break;
        }
    }

    private void handleQuickAction(String action) {
        switch (action) {
            case "Pre-authorize Visitor":
                new PreAuthorizeFrame(service);
                break;
            case "Submit Complaint":
                new ComplaintFrame(service);
                break;
            case "View Notices":
                new ViewNoticesFrame(communityId);
                break;
        }
    }

    private String getCommunityName() {
        try {
            ResultSet rs = db.DBConnection.getConnection().createStatement()
                    .executeQuery("SELECT community_name FROM gated_communities WHERE id = " + communityId);
            if (rs.next()) {
                return rs.getString("community_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Community #" + communityId; // Fallback if name not found
    }
}