package appointment;

import java.util.Date;

public class Appointment {
    private int id;
    private int doctorId;
    private int customerId;
    private Date appointmentDate;
    private int serialNumber;

    public Appointment() {}

    public Appointment(int id, int doctorId, int customerId, Date appointmentDate, int serialNumber) {
        this.id = id;
        this.doctorId = doctorId;
        this.customerId = customerId;
        this.appointmentDate = appointmentDate;
        this.serialNumber = serialNumber;
    }

    // getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public Date getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(Date appointmentDate) { this.appointmentDate = appointmentDate; }

    public int getSerialNumber() { return serialNumber; }
    public void setSerialNumber(int serialNumber) { this.serialNumber = serialNumber; }
}
