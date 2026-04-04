package appointment;

import javax.swing.*;
import java.awt.*;

public class HomeUI extends JFrame {
    public HomeUI() {
        setTitle("Doctor Appointment System - Home");
        setSize(400, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel p = new JPanel();
        p.setLayout(new GridBagLayout());

        JLabel title = new JLabel("Doctor Appointment System");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));

        JButton adminBtn = new JButton("Admin Login");
        adminBtn.setBackground(new Color(144, 238, 144)); // light green
        adminBtn.addActionListener(e -> {
            new AdminLoginUI().setVisible(true);
            dispose();
        });

        JButton custBtn = new JButton("Customer Login");
        custBtn.setBackground(new Color(144, 238, 144));
        custBtn.addActionListener(e -> {
            new CustomerLoginUI().setVisible(true);
            dispose();
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.gridx = 0; gbc.gridy = 0;
        p.add(title, gbc);
        gbc.gridy = 1;
        p.add(adminBtn, gbc);
        gbc.gridy = 2;
        p.add(custBtn, gbc);

        add(p);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HomeUI().setVisible(true));
    }
}
