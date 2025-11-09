package services;
import db.DBConnection;
import javax.swing.*;
import java.sql.*;
import java.util.Stack;

public class AdminService {
    public void postNotice(String title, String message, int communityId) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("INSERT INTO notices(title,message,community_id) VALUES(?,?,?)")) {
            ps.setString(1,title);
            ps.setString(2,message);
            ps.setInt(3,communityId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null,"Notice posted.");
        } catch (SQLException e) { JOptionPane.showMessageDialog(null,"Error: "+e.getMessage()); }
    }

    public void viewNotices(int communityId) {
        Stack<String> stack = new Stack<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT title,message,created_at FROM notices WHERE community_id=? ORDER BY created_at DESC")) {
            ps.setInt(1,communityId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                stack.push("[" + rs.getString("created_at") + "] " + rs.getString("title") + " - " + rs.getString("message"));
            }
        } catch (SQLException e) { JOptionPane.showMessageDialog(null,"Error: "+e.getMessage()); return; }

        if (stack.isEmpty()) { JOptionPane.showMessageDialog(null,"No notices."); return; }
        StringBuilder sb = new StringBuilder();
        while (!stack.isEmpty()) sb.append(stack.pop()).append("\n\n");
        JTextArea ta = new JTextArea(sb.toString());
        ta.setEditable(false);
        JOptionPane.showMessageDialog(null,new JScrollPane(ta),"Notices",JOptionPane.INFORMATION_MESSAGE);
    }

    public void viewVisitors(boolean usedOnly) {
        String sql = "SELECT v.visitor_name, u.house_no, v.purpose, v.status, v.check_in FROM visitors v JOIN users u ON v.resident_id=u.id";
        if (usedOnly) sql += " WHERE v.status='used'";
        sql += " ORDER BY v.id DESC";
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append("Visitor: ").append(rs.getString("visitor_name"))
                  .append(" | House: ").append(rs.getString("house_no"))
                  .append(" | Purpose: ").append(rs.getString("purpose"))
                  .append(" | Status: ").append(rs.getString("status"))
                  .append(" | Check-in: ").append(rs.getString("check_in"))
                  .append("\n");
            }
            JTextArea ta = new JTextArea(sb.length()==0?"No visitors.":sb.toString());
            ta.setEditable(false);
            JOptionPane.showMessageDialog(null,new JScrollPane(ta),
                    usedOnly?"Verified Visitors":"All Visitors",JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) { JOptionPane.showMessageDialog(null,"Error: "+e.getMessage()); }
    }

    public void viewComplaints() {
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT c.id,u.username,c.subject,c.message,c.file_path,c.created_at FROM complaints c JOIN users u ON c.resident_id=u.id ORDER BY c.created_at DESC")) {
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append("ID: ").append(rs.getInt("id"))
                  .append("\nResident: ").append(rs.getString("username"))
                  .append("\nSubject: ").append(rs.getString("subject"))
                  .append("\nMessage: ").append(rs.getString("message"))
                  .append("\nAttachment: ").append(rs.getString("file_path"))
                  .append("\nDate: ").append(rs.getString("created_at"))
                  .append("\n\n");
            }
            JTextArea ta = new JTextArea(sb.length()==0?"No complaints.":sb.toString());
            ta.setEditable(false);
            JOptionPane.showMessageDialog(null,new JScrollPane(ta),"Complaints",JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) { JOptionPane.showMessageDialog(null,"Error: "+e.getMessage()); }
    }
}
