package test.app;

import javax.swing.*;
import java.awt.*;

public class IntroScreen extends JPanel {
    private JTextField inputField;
    private JButton enterButton;
    private MainFrame parent;

    public IntroScreen(MainFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout());

        JLabel label = new JLabel("Enter number of values:", SwingConstants.CENTER);
        inputField = new JTextField();
        enterButton = new JButton("Enter");

        add(label, BorderLayout.NORTH);
        add(inputField, BorderLayout.CENTER);
        add(enterButton, BorderLayout.SOUTH);

        enterButton.addActionListener(e -> {
            try {
                int count = Integer.parseInt(inputField.getText());
                if (count <= 0) throw new NumberFormatException();
                parent.showSortScreen(count);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid positive number!");
            }
        });
    }
}