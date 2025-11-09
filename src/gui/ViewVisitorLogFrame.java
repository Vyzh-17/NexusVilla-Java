package gui;
import db.DBConnection;
import javax.swing.*; import javax.swing.table.*;
import java.awt.*; import java.sql.*;

public class ViewVisitorLogFrame extends JFrame {
    public ViewVisitorLogFrame(String role,int userId){
        setTitle("Visitor Log"); setSize(1000,600); setLocationRelativeTo(null); setLayout(new BorderLayout());
        add(StyleUtils.header("Visitor Log"), BorderLayout.NORTH);
        JPanel top = new JPanel(new BorderLayout()); JTextField search = new JTextField(); search.setBorder(BorderFactory.createTitledBorder("Search"));
        top.add(search, BorderLayout.CENTER); add(top, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(new String[]{"Visitor","House","Purpose","Status","Check-in"},0);
        JTable table = new JTable(model); table.setRowHeight(26);
        table.getTableHeader().setBackground(StyleUtils.PRIMARY); table.getTableHeader().setForeground(Color.WHITE);
        add(new JScrollPane(table), BorderLayout.CENTER);

        String sql="";
        if ("admin".equalsIgnoreCase(role)) {
            sql = "SELECT v.visitor_name,u.house_no,v.purpose,v.status,v.check_in FROM visitors v JOIN users u ON v.resident_id=u.id ORDER BY v.id DESC";
        } else if ("adminUsed".equalsIgnoreCase(role)) {
            sql = "SELECT v.visitor_name,u.house_no,v.purpose,v.status,v.check_in FROM visitors v JOIN users u ON v.resident_id=u.id WHERE v.status='used' ORDER BY v.check_in DESC";
        } else if ("resident".equalsIgnoreCase(role)) {
            sql = "SELECT visitor_name,(SELECT house_no FROM users WHERE id=resident_id) AS house_no,purpose,status,check_in FROM visitors WHERE resident_id="+userId+" ORDER BY id DESC";
        } else {
            sql = "SELECT v.visitor_name,u.house_no,v.purpose,v.status,v.check_in FROM visitors v JOIN users u ON v.resident_id=u.id WHERE v.status='used' ORDER BY v.check_in DESC";
        }

        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) model.addRow(new Object[]{rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4), rs.getString(5)==null?"-":rs.getString(5)});
        } catch (SQLException e) { JOptionPane.showMessageDialog(this,"Load error: "+e.getMessage()); }

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        search.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
            public void insertUpdate(javax.swing.event.DocumentEvent e){filter();}
            public void removeUpdate(javax.swing.event.DocumentEvent e){filter();}
            public void changedUpdate(javax.swing.event.DocumentEvent e){filter();}
            private void filter(){ String s = search.getText().trim(); sorter.setRowFilter(s.isEmpty()?null:RowFilter.regexFilter("(?i)"+s)); }
        });

        setVisible(true);
    }
}
