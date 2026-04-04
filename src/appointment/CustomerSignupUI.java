package appointment;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class CustomerSignupUI extends JFrame {
    public CustomerSignupUI() {
        setTitle("Customer Signup");
        setSize(450, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);

        JTextField nameF = new JTextField(20);
        JTextField mobileF = new JTextField(12);
        JTextField addressF = new JTextField(20);
        JTextField gmailF = new JTextField(20);
        JPasswordField passF = new JPasswordField(20);
        JPasswordField cpassF = new JPasswordField(20);

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; p.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1; p.add(nameF, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; p.add(new JLabel("Mobile:"), gbc);
        gbc.gridx = 1; p.add(mobileF, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; p.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1; p.add(addressF, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; p.add(new JLabel("Gmail:"), gbc);
        gbc.gridx = 1; p.add(gmailF, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; p.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; p.add(passF, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; p.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1; p.add(cpassF, gbc);
        row++;

        JButton signupBtn = new JButton("Signup");
        signupBtn.setBackground(new Color(144, 238, 144));
        signupBtn.addActionListener(e -> {
            String name = nameF.getText().trim();
            String mobile = mobileF.getText().trim();
            String address = addressF.getText().trim();
            String gmail = gmailF.getText().trim();
            String pass = new String(passF.getPassword()).trim();
            String cpass = new String(cpassF.getPassword()).trim();

            if (name.isEmpty() || gmail.isEmpty() || pass.isEmpty() || cpass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill required fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!pass.equals(cpass)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                String insert = "INSERT INTO customers (id, full_name, mobile, address, gmail, password) VALUES (seq_customer_id.NEXTVAL, ?, ?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(insert);
                ps.setString(1, name);
                ps.setString(2, mobile);
                ps.setString(3, address);
                ps.setString(4, gmail);
                ps.setString(5, pass);
                ps.executeUpdate();
                // Removed conn.commit() to avoid ORA-17273 error
                JOptionPane.showMessageDialog(this, "Signup successful. Please login.");
                new CustomerLoginUI().setVisible(true);
                dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton back = new JButton("Back");
        back.addActionListener(e -> {
            new CustomerLoginUI().setVisible(true);
            dispose();
        });

        gbc.gridx = 0; gbc.gridy = row; p.add(back, gbc);
        gbc.gridx = 1; p.add(signupBtn, gbc);

        add(new JScrollPane(p));
    }
}
