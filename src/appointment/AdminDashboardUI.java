package appointment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AdminDashboardUI extends JFrame {
    private JTable doctorTable;
    private DefaultTableModel doctorModel;
    private JTable apptTable;
    private DefaultTableModel apptModel;

    public AdminDashboardUI() {
        setTitle("Admin Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top panel - add doctor
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField nameF = new JTextField(12);
        JTextField specF = new JTextField(12);
        JTextField daysF = new JTextField(10);
        JTextField timeF = new JTextField(10);
        JButton addBtn = new JButton("Add Doctor");
        addBtn.setBackground(new Color(144, 238, 144));
        addBtn.addActionListener(e -> {
            String name = nameF.getText().trim();
            String spec = specF.getText().trim();
            String days = daysF.getText().trim();
            String time = timeF.getText().trim();
            if (name.isEmpty() || spec.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name and specialization required", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "INSERT INTO doctors (id, name, specialization, available_days, time_slots) VALUES (seq_doctor_id.NEXTVAL, ?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, name);
                ps.setString(2, spec);
                ps.setString(3, days);
                ps.setString(4, time);
                ps.executeUpdate();
                loadDoctors();
                nameF.setText("");
                specF.setText("");
                daysF.setText("");
                timeF.setText("");
                JOptionPane.showMessageDialog(this, "Doctor added.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        top.add(new JLabel("Name:"));
        top.add(nameF);
        top.add(new JLabel("Spec:"));
        top.add(specF);
        top.add(new JLabel("Days:"));
        top.add(daysF);
        top.add(new JLabel("Time:"));
        top.add(timeF);
        top.add(addBtn);

        add(top, BorderLayout.NORTH);

        // Center split - doctors list left, appointments right
        JSplitPane split = new JSplitPane();
        // Doctors table
        doctorModel = new DefaultTableModel(new String[]{"ID", "Name", "Spec", "Days", "Time"}, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        doctorTable = new JTable(doctorModel);
        JScrollPane leftScroll = new JScrollPane(doctorTable);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(new JLabel("Doctors"), BorderLayout.NORTH);
        leftPanel.add(leftScroll, BorderLayout.CENTER);

        JButton refreshDoctors = new JButton("Refresh");
        refreshDoctors.addActionListener(e -> loadDoctors());
        JButton delDoctorBtn = new JButton("Delete Doctor");
        delDoctorBtn.setBackground(new Color(144, 238, 144));
        delDoctorBtn.addActionListener(e -> {
            int r = doctorTable.getSelectedRow();
            if (r == -1) {
                JOptionPane.showMessageDialog(this, "Select a doctor");
                return;
            }
            int docId = Integer.parseInt(doctorModel.getValueAt(r, 0).toString());
            int confirm = JOptionPane.showConfirmDialog(this, "Delete doctor and all appointments?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DBConnection.getConnection()) {
                    String sql = "DELETE FROM doctors WHERE id = ?";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setInt(1, docId);
                    ps.executeUpdate();
                    loadDoctors();
                    loadAppointmentsForDoctor(-1);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel leftButtons = new JPanel();
        leftButtons.add(refreshDoctors);
        leftButtons.add(delDoctorBtn);
        leftPanel.add(leftButtons, BorderLayout.SOUTH);

        // Appointments area
        apptModel = new DefaultTableModel(new String[]{"AppID", "Customer", "Date", "Serial"}, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        apptTable = new JTable(apptModel);
        JScrollPane rightScroll = new JScrollPane(apptTable);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(new JLabel("Appointments for selected doctor"), BorderLayout.NORTH);
        rightPanel.add(rightScroll, BorderLayout.CENTER);

        JButton delApptBtn = new JButton("Delete Appointment");
        delApptBtn.setBackground(new Color(144, 238, 144));
        delApptBtn.addActionListener(e -> {
            int r = apptTable.getSelectedRow();
            if (r == -1) {
                JOptionPane.showMessageDialog(this, "Select an appointment");
                return;
            }
            int apptId = Integer.parseInt(apptModel.getValueAt(r, 0).toString());
            int docRow = doctorTable.getSelectedRow();
            if (docRow == -1) {
                JOptionPane.showMessageDialog(this, "Select doctor");
                return;
            }
            int docId = Integer.parseInt(doctorModel.getValueAt(docRow, 0).toString());
            int confirm = JOptionPane.showConfirmDialog(this, "Delete appointment?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DBConnection.getConnection()) {
                    String del = "DELETE FROM appointments WHERE id = ?";
                    PreparedStatement ps = conn.prepareStatement(del);
                    ps.setInt(1, apptId);
                    ps.executeUpdate();
                    resequenceSerials(conn, docId);
                    loadAppointmentsForDoctor(docId);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel rightButtons = new JPanel();
        rightButtons.add(delApptBtn);
        rightPanel.add(rightButtons, BorderLayout.SOUTH);

        split.setLeftComponent(leftPanel);
        split.setRightComponent(rightPanel);
        split.setDividerLocation(450);

        add(split, BorderLayout.CENTER);

        // doctor selection listener
        doctorTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int r = doctorTable.getSelectedRow();
                if (r != -1) {
                    int docId = Integer.parseInt(doctorModel.getValueAt(r, 0).toString());
                    loadAppointmentsForDoctor(docId);
                }
            }
        });

        // initial load
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

    private void loadAppointmentsForDoctor(int doctorId) {
        apptModel.setRowCount(0);
        if (doctorId == -1) return;
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT a.id, c.full_name, a.appointment_date, a.serial_number FROM appointments a JOIN customers c ON a.customer_id = c.id WHERE a.doctor_id = ? ORDER BY a.appointment_date, a.serial_number";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            while (rs.next()) {
                apptModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        sdf.format(rs.getDate("appointment_date")),
                        rs.getInt("serial_number")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resequenceSerials(Connection conn, int doctorId) throws SQLException {
        String datesSql = "SELECT DISTINCT appointment_date FROM appointments WHERE doctor_id = ? ORDER BY appointment_date";
        PreparedStatement psDates = conn.prepareStatement(datesSql);
        psDates.setInt(1, doctorId);
        ResultSet rsDates = psDates.executeQuery();
        ArrayList<java.sql.Date> dates = new ArrayList<>();
        while (rsDates.next()) dates.add(rsDates.getDate(1));

        String fetchAppts = "SELECT id FROM appointments WHERE doctor_id = ? AND appointment_date = ? ORDER BY id";
        String updateSerial = "UPDATE appointments SET serial_number = ? WHERE id = ?";
        for (java.sql.Date d : dates) {
            PreparedStatement pFetch = conn.prepareStatement(fetchAppts);
            pFetch.setInt(1, doctorId);
            pFetch.setDate(2, d);
            ResultSet rsA = pFetch.executeQuery();
            int serial = 1;
            PreparedStatement pUpdate = conn.prepareStatement(updateSerial);
            while (rsA.next()) {
                int apptId = rsA.getInt("id");
                pUpdate.setInt(1, serial++);
                pUpdate.setInt(2, apptId);
                pUpdate.executeUpdate();
            }
        }
    }
}
