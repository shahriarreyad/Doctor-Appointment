package appointment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class CustomerDashboardUI extends JFrame {
    private int customerId;
    private String customerName;
    private JTable doctorTable;
    private DefaultTableModel doctorModel;

    public CustomerDashboardUI(int customerId, String customerName) {
        this.customerId = customerId;
        this.customerName = customerName;

        setTitle("Customer Dashboard - " + customerName);
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        doctorModel = new DefaultTableModel(new String[]{"ID", "Name", "Spec", "Days", "Time"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        doctorTable = new JTable(doctorModel);
        JScrollPane scroll = new JScrollPane(doctorTable);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refresh = new JButton("Refresh Doctors");
        refresh.setBackground(new Color(144, 238, 144));
        refresh.addActionListener(e -> loadDoctors());
        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> {
            new CustomerLoginUI().setVisible(true);
            dispose();
        });
        top.add(refresh);
        top.add(logout);

        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        // bottom panel: doctor details and booking
        JPanel bottom = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);

        JTextArea docDetails = new JTextArea(6, 40);
        docDetails.setEditable(false);
        JScrollPane docScroll = new JScrollPane(docDetails);

        JLabel dateLabel = new JLabel("Select date (next 7 days, yyyy-MM-dd):");
        JTextField dateField = new JTextField(10);
        JButton bookBtn = new JButton("Book Appointment");
        bookBtn.setBackground(new Color(144, 238, 144));

        gbc.gridx = 0;
        gbc.gridy = 0;
        bottom.add(docScroll, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        bottom.add(dateLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        bottom.add(dateField, gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        bottom.add(bookBtn, gbc);

        add(bottom, BorderLayout.SOUTH);

        doctorTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int r = doctorTable.getSelectedRow();
                if (r != -1) {
                    String name = doctorModel.getValueAt(r, 1).toString();
                    String spec = doctorModel.getValueAt(r, 2).toString();
                    String days = doctorModel.getValueAt(r, 3).toString();
                    String time = doctorModel.getValueAt(r, 4).toString();
                    docDetails.setText(
                            "Doctor: " + name +
                                    "\nSpecialization: " + spec +
                                    "\nAvailable Days: " + days +
                                    "\nTime Slots: " + time
                    );
                }
            }
        });

        bookBtn.addActionListener(e -> {
            int r = doctorTable.getSelectedRow();
            if (r == -1) {
                JOptionPane.showMessageDialog(this, "Select a doctor", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String dateStr = dateField.getText().trim();
            if (dateStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter date", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // parse and validate date within next 7 days
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date date = sdf.parse(dateStr);
                java.util.Date today = sdf.parse(sdf.format(new java.util.Date()));
                long diff = (date.getTime() - today.getTime()) / (24L * 60 * 60 * 1000);
                if (diff < 0 || diff > 7) {
                    JOptionPane.showMessageDialog(this, "Date must be within next 7 days (including today).", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int docId = Integer.parseInt(doctorModel.getValueAt(r, 0).toString());
                String docName = doctorModel.getValueAt(r, 1).toString();

                // calculate next serial number for this doctor and date
                try (Connection conn = DBConnection.getConnection()) {
                    String countSql = "SELECT NVL(MAX(serial_number),0) as maxs FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
                    PreparedStatement ps = conn.prepareStatement(countSql);
                    ps.setInt(1, docId);
                    ps.setDate(2, new java.sql.Date(date.getTime()));
                    ResultSet rs = ps.executeQuery();
                    int nextSerial = 1;
                    if (rs.next()) nextSerial = rs.getInt("maxs") + 1;

                    String insert = "INSERT INTO appointments (id, doctor_id, customer_id, appointment_date, serial_number) VALUES (seq_appointment_id.NEXTVAL, ?, ?, ?, ?)";
                    PreparedStatement ins = conn.prepareStatement(insert);
                    ins.setInt(1, docId);
                    ins.setInt(2, customerId);
                    ins.setDate(3, new java.sql.Date(date.getTime()));
                    ins.setInt(4, nextSerial);
                    ins.executeUpdate(); // auto-commit handles saving

                    JOptionPane.showMessageDialog(this, customerName + ", your appointment is booked with " + docName + ". Your serial number is " + nextSerial + ".");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use yyyy-MM-dd", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        loadDoctors();
    }

    private void loadDoctors() {
        doctorModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT id, name, specialization, available_days, time_slots FROM doctors ORDER BY id";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                doctorModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("specialization"),
                        rs.getString("available_days"),
                        rs.getString("time_slots")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
