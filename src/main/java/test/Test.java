package test;

import test.app.MainFrame;
import javax.swing.*;

public class Test extends JFrame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
