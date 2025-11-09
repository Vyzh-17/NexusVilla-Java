package services;

import db.DBConnection;
import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class ResidentService {

    private static String ACCOUNT_SID;
    private static String AUTH_TOKEN;
    private static final String TWILIO_WHATSAPP_NUMBER = "whatsapp:+14155238886";

    public static final Map<String, Queue<String>> pendingMap = new HashMap<>();
    public static final Map<String, OTPEntry> otpMap = new HashMap<>();

    private final int residentId;
    private final int communityId;
    private final String houseNo;

    static {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream("src/config.properties"));
            ACCOUNT_SID = props.getProperty("TWILIO_ACCOUNT_SID");
            AUTH_TOKEN = props.getProperty("TWILIO_AUTH_TOKEN");
        } catch (IOException e) {
            System.out.println("⚠ Could not load Twilio credentials: " + e.getMessage());
            ACCOUNT_SID = "";
            AUTH_TOKEN = "";
        }
    }

    public ResidentService(int residentId, int communityId, String houseNo) {
        this.residentId = residentId;
        this.communityId = communityId;
        this.houseNo = houseNo;
        loadFromDB();
    }

    public static class OTPEntry {
        public final String otp;
        public final long expiresAt;
        public final String visitorName;
        public OTPEntry(String otp,long expiresAt,String visitorName){
            this.otp=otp; this.expiresAt=expiresAt; this.visitorName=visitorName;
        }
    }

    private void loadFromDB() {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "SELECT visitor_name,otp,otp_expires_at FROM visitors WHERE resident_id=? AND status='unused'"
             )) {
            ps.setInt(1,residentId);
            ResultSet rs = ps.executeQuery();
            Queue<String> q = pendingMap.computeIfAbsent(houseNo,k->new LinkedList<>());
            while (rs.next()) {
                String v = rs.getString("visitor_name");
                String otp = rs.getString("otp");
                Timestamp t = rs.getTimestamp("otp_expires_at");
                long exp = t!=null ? t.getTime() : System.currentTimeMillis()+3600_000;
                if (!q.contains(v)) q.add(v);
                otpMap.put(houseNo,new OTPEntry(otp,exp,v));
            }
        } catch (SQLException e) {
            System.out.println("load err: "+e.getMessage());
        }
    }

    public void preAuthorizeVisitor(String visitorName,String contact,String purpose,int validHours) {
        if (visitorName==null || visitorName.isBlank()){
            JOptionPane.showMessageDialog(null,"Visitor name required."); return;
        }
        if (contact==null || contact.isBlank()){
            JOptionPane.showMessageDialog(null,"Contact required."); return;
        }

        int h = Math.min(Math.max(validHours,1),24);
        String otp = String.format("%04d", new Random().nextInt(10000));
        long exp = System.currentTimeMillis() + h*3600L*1000L;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "INSERT INTO visitors(visitor_name,contact,purpose,otp,status,resident_id,community_id,otp_expires_at) VALUES(?,?,?,?, 'unused',?,?,?)"
             )) {
            ps.setString(1,visitorName);
            ps.setString(2,contact);
            ps.setString(3,purpose);
            ps.setString(4,otp);
            ps.setInt(5,residentId);
            ps.setInt(6,communityId);
            ps.setTimestamp(7,new Timestamp(exp));
            ps.executeUpdate();

            pendingMap.computeIfAbsent(houseNo,k->new LinkedList<>()).add(visitorName);
            otpMap.put(houseNo,new OTPEntry(otp,exp,visitorName));

            try {
                Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
                String msg = "🏡 NEXUSVILLA Visitor OTP\n"
                           + "House: " + houseNo
                           + "\nVisitor: " + visitorName
                           + "\nOTP: " + otp
                           + "\nValid for: " + h + " hour(s).";

                Message.creator(
                    new PhoneNumber("whatsapp:+91" + contact),
                    new PhoneNumber(TWILIO_WHATSAPP_NUMBER),
                    msg
                ).create();

                JOptionPane.showMessageDialog(null,"✅ Pre-authorized.\nOTP sent to WhatsApp: +91"+contact);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,"⚠ Twilio send failed.\nOTP: "+otp+"\nReason: "+ex.getMessage());
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,"DB error: "+e.getMessage());
        }
    }

    public void showPendingVisitors() {
        Queue<String> q = pendingMap.get(houseNo);
        if (q==null || q.isEmpty()){
            JOptionPane.showMessageDialog(null,"No pending visitors."); return;
        }
        StringBuilder sb = new StringBuilder("Pending visitors for "+houseNo+":\n\n");
        q.forEach(v->sb.append("• ").append(v).append("\n"));
        JOptionPane.showMessageDialog(null,sb.toString());
    }

    public void showMyVisitors() {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "SELECT visitor_name,purpose,otp,status,check_in FROM visitors WHERE resident_id=? ORDER BY id DESC"
             )) {
            ps.setInt(1,residentId);
            ResultSet rs = ps.executeQuery();
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append("Visitor: ").append(rs.getString("visitor_name"))
                  .append("\nPurpose: ").append(rs.getString("purpose"))
                  .append("\nOTP: ").append(rs.getString("otp"))
                  .append("\nStatus: ").append(rs.getString("status"))
                  .append("\nCheck-in: ").append(rs.getString("check_in"))
                  .append("\n\n");
            }
            JTextArea ta = new JTextArea(sb.length()==0?"No records.":sb.toString());
            ta.setEditable(false);
            JOptionPane.showMessageDialog(null,new JScrollPane(ta),"My Visitors",JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,"Error: "+e.getMessage());
        }
    }

    public void submitComplaint(String subject,String message,String filePath) {
        if (subject==null||subject.isBlank()||message==null||message.isBlank()){
            JOptionPane.showMessageDialog(null,"Fill subject and message."); return;
        }
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "INSERT INTO complaints(resident_id,community_id,subject,message,file_path) VALUES(?,?,?,?,?)"
             )) {
            ps.setInt(1,residentId);
            ps.setInt(2,communityId);
            ps.setString(3,subject);
            ps.setString(4,message);
            ps.setString(5,filePath);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null,"Complaint submitted.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,"Error: "+e.getMessage());
        }
    }

    public int getResidentId() {return residentId;}
    public int getCommunityId() {return communityId;}
    public String getHouseNo() {return houseNo;}
}
