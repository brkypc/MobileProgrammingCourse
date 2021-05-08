package tr.edu.yildiz.berkayyapici;

public class User {
    String fullName, username, email, phoneNo, birthDate, password;

    public User() {
    }

    public User(String fullName, String username, String email, String phoneNo, String birthDate, String password) {
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.phoneNo = phoneNo;
        this.birthDate = birthDate;
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
