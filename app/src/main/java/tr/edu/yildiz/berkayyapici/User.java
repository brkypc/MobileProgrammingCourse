package tr.edu.yildiz.berkayyapici;

public class User {
    String username, fullName, email, phoneNo, birthDate, password;

    public User(String username, String fullName, String email, String phoneNo, String birthDate, String password) {
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.phoneNo = phoneNo;
        this.birthDate = birthDate;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getPassword() {
        return password;
    }
}
