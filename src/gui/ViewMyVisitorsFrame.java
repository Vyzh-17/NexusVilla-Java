package gui;

import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ViewMyVisitorsFrame extends JFrame {

    public ViewMyVisitorsFrame(int residentId) {
        setTitle("My Visitors");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        add(StyleUtils.header("👥 My Visitors"), BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Visitor Name", "Purpose", "OTP", "Status", "Check-in Time"}, 0
        );

        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(26);
        table.getTableHeader().setBackground(StyleUtils.PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        add(scroll, BorderLayout.CENTER);

       
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT visitor_name, purpose, otp, status, check_in FROM visitors WHERE resident_id=? ORDER BY id DESC")) {
            ps.setInt(1, residentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("visitor_name"),
                        rs.getString("purpose"),
                        rs.getString("otp"),
                        rs.getString("status"),
                        rs.getString("check_in")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading visitors: " + e.getMessage());
        }

        if (model.getRowCount() == 0)
            model.addRow(new Object[]{"No visitors", "-", "-", "-", "-"});

        
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton close = StyleUtils.dangerBtn("Close");
        bottom.add(close);
        add(bottom, BorderLayout.SOUTH);

        close.addActionListener(e -> dispose());

        setVisible(true);
    }
}
