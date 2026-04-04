package appointment;

import javax.swing.*;
import java.awt.*;

public class AdminLoginUI extends JFrame {
    public AdminLoginUI() {
        setTitle("Admin Login");
        setSize(350,220);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);

        JLabel uLabel = new JLabel("Username:");
        JTextField uField = new JTextField(15);

        JLabel pLabel = new JLabel("Password:");
        JPasswordField pField = new JPasswordField(15);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(144,238,144));
        loginBtn.addActionListener(e -> {
            String user = uField.getText().trim();
            String pass = new String(pField.getPassword()).trim();
            if ("admin".equals(user) && "admin123".equals(pass)) {
                new AdminDashboardUI().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid admin credentials", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton back = new JButton("Back");
        back.addActionListener(e -> {
            new HomeUI().setVisible(true);
            dispose();
        });

        gbc.gridx = 0; gbc.gridy = 0; p.add(uLabel, gbc);
        gbc.gridx = 1; p.add(uField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; p.add(pLabel, gbc);
        gbc.gridx = 1; p.add(pField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; p.add(back, gbc);
        gbc.gridx = 1; p.add(loginBtn, gbc);

        add(p);
    }
}
