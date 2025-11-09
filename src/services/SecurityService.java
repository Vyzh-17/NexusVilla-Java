package services;
import db.DBConnection;
import javax.swing.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Queue;

public class SecurityService {
    private final LinkedList<String> accessLog = new LinkedList<>();

    public void verifyVisitor(String houseNo, String otp) {
        if (houseNo==null||houseNo.isBlank()||otp==null||otp.isBlank()){ JOptionPane.showMessageDialog(null,"Enter house and otp."); return; }

        ResidentService.OTPEntry entry = ResidentService.otpMap.get(houseNo);
        if (entry!=null) {
            long now = System.currentTimeMillis();
            if (now>entry.expiresAt) { JOptionPane.showMessageDialog(null,"OTP expired."); ResidentService.otpMap.remove(houseNo); return; }
            if (entry.otp.equals(otp)) {
                markUsed(houseNo, otp);
                accessLog.addFirst("Access granted for "+houseNo+" ("+entry.visitorName+") at "+ LocalDateTime.now());
                Queue<String> q = ResidentService.pendingMap.get(houseNo); if (q!=null) q.remove(entry.visitorName);
                ResidentService.otpMap.remove(houseNo);
                JOptionPane.showMessageDialog(null,"Access granted for " + entry.visitorName);
                return;
            } else { JOptionPane.showMessageDialog(null,"Invalid OTP for this house."); return; }
        }

    
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT v.id,v.visitor_name,v.status,v.otp_expires_at FROM visitors v JOIN users u ON v.resident_id=u.id WHERE u.house_no=? AND v.otp=?")) {
            ps.setString(1,houseNo);
            ps.setString(2,otp);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) { JOptionPane.showMessageDialog(null,"Invalid OTP or house."); return; }
            String status = rs.getString("status");
            Timestamp exp = rs.getTimestamp("otp_expires_at");
            String visitor = rs.getString("visitor_name");
            if ("used".equals(status)){ JOptionPane.showMessageDialog(null,"OTP already used."); return; }
            if (exp!=null && System.currentTimeMillis()>exp.getTime()){ JOptionPane.showMessageDialog(null,"OTP expired."); return; }
            markUsed(houseNo, otp);
            accessLog.addFirst("Access granted for "+houseNo+" ("+visitor+") at "+ LocalDateTime.now());
            Queue<String> q = ResidentService.pendingMap.get(houseNo); if (q!=null) q.remove(visitor);
            JOptionPane.showMessageDialog(null,"Access granted for "+visitor);
        } catch (SQLException e) { JOptionPane.showMessageDialog(null,"DB error: "+e.getMessage()); }
    }

    private void markUsed(String houseNo, String otp) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE visitors v JOIN users u ON v.resident_id=u.id SET v.status='used', v.check_in=NOW() WHERE u.house_no=? AND v.otp=?")) {
            ps.setString(1,houseNo); ps.setString(2,otp); ps.executeUpdate();
        } catch (SQLException e) { System.out.println("mark used err: "+e.getMessage()); }
    }

    public void viewAccessLog() {
        if (accessLog.isEmpty()) { JOptionPane.showMessageDialog(null,"No access logs."); return; }
        StringBuilder sb = new StringBuilder();
        accessLog.forEach(s->sb.append(s).append("\n"));
        JTextArea ta = new JTextArea(sb.toString()); ta.setEditable(false);
        JOptionPane.showMessageDialog(null,new JScrollPane(ta),"Access Log",JOptionPane.INFORMATION_MESSAGE);
    }
}
