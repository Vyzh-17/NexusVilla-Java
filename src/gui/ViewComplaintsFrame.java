package gui;
import db.DBConnection;
import javax.swing.*; import javax.swing.table.DefaultTableModel;
import java.awt.*; import java.io.File; import java.sql.*;

public class ViewComplaintsFrame extends JFrame {
    public ViewComplaintsFrame(){
        setTitle("View Complaints"); setSize(1000,600); setLocationRelativeTo(null); setLayout(new BorderLayout());
        add(StyleUtils.header("Complaints"), BorderLayout.NORTH);
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID","Resident","Subject","Message","Attachment","Date"},0);
        JTable table = new JTable(model); table.setRowHeight(24);
        add(new JScrollPane(table), BorderLayout.CENTER);

        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT c.id,u.username,c.subject,c.message,c.file_path,c.created_at FROM complaints c JOIN users u ON c.resident_id=u.id ORDER BY c.created_at DESC")) {
            while (rs.next()) model.addRow(new Object[]{rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6)});
        } catch (SQLException e) { JOptionPane.showMessageDialog(this,"Error: "+e.getMessage()); }

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT)); JButton open = StyleUtils.primaryBtn("Open Attachment");
        bottom.add(open); add(bottom, BorderLayout.SOUTH);

        open.addActionListener(e-> {
            int r = table.getSelectedRow(); if (r==-1){ JOptionPane.showMessageDialog(this,"Select a complaint."); return; }
            String p = (String) model.getValueAt(r,4); if (p==null || p.isBlank()){ JOptionPane.showMessageDialog(this,"No attachment."); return; }
            try { File f = new File(p); if (!f.exists()){ JOptionPane.showMessageDialog(this,"File missing: "+p); return; }
                String name = f.getName().toLowerCase();
                if (name.endsWith(".jpg")||name.endsWith(".png")||name.endsWith(".jpeg")||name.endsWith(".gif")){
                    ImageIcon ic = new ImageIcon(f.getAbsolutePath()); Image img = ic.getImage().getScaledInstance(800,600,Image.SCALE_SMOOTH);
                    JLabel lbl = new JLabel(new ImageIcon(img)); JOptionPane.showMessageDialog(this,new JScrollPane(lbl),"Preview",JOptionPane.PLAIN_MESSAGE);
                } else Desktop.getDesktop().open(f);
            } catch (Exception ex) { JOptionPane.showMessageDialog(this,"Open failed: "+ex.getMessage()); }
        });

        setVisible(true);
    }
}
