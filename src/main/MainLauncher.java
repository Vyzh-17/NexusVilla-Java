package main;
import gui.LoginFrame;
public class MainLauncher {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(LoginFrame::new);
    }
}
