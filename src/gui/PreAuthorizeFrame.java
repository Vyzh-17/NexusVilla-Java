package gui;
import services.ResidentService;
import javax.swing.*; import java.awt.*;

public class PreAuthorizeFrame extends JFrame {
    public PreAuthorizeFrame(ResidentService svc){
        setTitle("Pre-authorize Visitor"); setSize(480,420); setLocationRelativeTo(null); setLayout(new BorderLayout());
        add(StyleUtils.header("Pre-authorize Visitor"), BorderLayout.NORTH);
        JPanel p = new JPanel(new GridLayout(8,1,6,6)); p.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        JTextField name = new JTextField(); JTextField contact = new JTextField(); JTextField purpose = new JTextField(); JTextField hours = new JTextField("1");
        p.add(new JLabel("Visitor Name:")); p.add(name); p.add(new JLabel("Contact (10 digits):")); p.add(contact);
        p.add(new JLabel("Purpose:")); p.add(purpose); p.add(new JLabel("Valid hours (1-24):")); p.add(hours);
        add(p, BorderLayout.CENTER);
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT)); JButton ok = StyleUtils.primaryBtn("Pre-authorize");
        bottom.add(ok); add(bottom, BorderLayout.SOUTH);
        ok.addActionListener(e-> {
            try { int h = Integer.parseInt(hours.getText().trim()); svc.preAuthorizeVisitor(name.getText().trim(), contact.getText().trim(), purpose.getText().trim(), h); dispose(); }
            catch (NumberFormatException ex){ JOptionPane.showMessageDialog(this,"Enter numeric hours."); }
        });
        setVisible(true);
    }
}
