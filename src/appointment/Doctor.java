package appointment;

public class Doctor {
    private int id;
    private String name;
    private String specialization;
    private String availableDays;
    private String timeSlots;

    public Doctor() {}

    public Doctor(int id, String name, String specialization, String availableDays, String timeSlots) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
        this.availableDays = availableDays;
        this.timeSlots = timeSlots;
    }

    // getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getAvailableDays() { return availableDays; }
    public void setAvailableDays(String availableDays) { this.availableDays = availableDays; }

    public String getTimeSlots() { return timeSlots; }
    public void setTimeSlots(String timeSlots) { this.timeSlots = timeSlots; }

    @Override
    public String toString() {
        return name + " (" + specialization + ")";
    }
}
