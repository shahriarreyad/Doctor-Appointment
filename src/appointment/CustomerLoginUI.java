package appointment;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CustomerLoginUI extends JFrame {
    public CustomerLoginUI() {
        setTitle("Customer Login");
        setSize(380,240);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);

        JLabel gLabel = new JLabel("Gmail:");
        JTextField gField = new JTextField(18);
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(18);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(144,238,144));
        loginBtn.addActionListener(e -> {
            String gmail = gField.getText().trim();
            String pass = new String(passField.getPassword()).trim();
            if (gmail.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // check DB
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "SELECT id, full_name FROM customers WHERE gmail = ? AND password = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, gmail);
                ps.setString(2, pass);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    int cusId = rs.getInt("id");
                    String name = rs.getString("full_name");
                    // open customer dashboard with customer info
                    new CustomerDashboardUI(cusId, name).setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid login", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "DB error: "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton signup = new JButton("Signup");
        signup.setBackground(new Color(144,238,144));
        signup.addActionListener(e -> {
            new CustomerSignupUI().setVisible(true);
            dispose();
        });

        JButton back = new JButton("Back");
        back.addActionListener(e -> {
            new HomeUI().setVisible(true);
            dispose();
        });

        gbc.gridx=0; gbc.gridy=0; p.add(gLabel, gbc);
        gbc.gridx=1; p.add(gField, gbc);
        gbc.gridx=0; gbc.gridy=1; p.add(passLabel, gbc);
        gbc.gridx=1; p.add(passField, gbc);
        gbc.gridx=0; gbc.gridy=2; p.add(back, gbc);
        gbc.gridx=1; p.add(signup, gbc);
        gbc.gridy=3; p.add(loginBtn, gbc);

        add(p);
    }
}
