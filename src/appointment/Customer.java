package appointment;

public class Customer {
    private int id;
    private String fullName;
    private String mobile;
    private String address;
    private String gmail;
    private String password;

    public Customer() {}

    public Customer(int id, String fullName, String mobile, String address, String gmail, String password) {
        this.id = id;
        this.fullName = fullName;
        this.mobile = mobile;
        this.address = address;
        this.gmail = gmail;
        this.password = password;
    }

    // getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getGmail() { return gmail; }
    public void setGmail(String gmail) { this.gmail = gmail; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
