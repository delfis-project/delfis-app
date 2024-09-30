package goldenage.delfis.app.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UserRequest {
    private String name;
    private String username;
    private String password;
    private String email;
    private String birthDate;

    public UserRequest(String name, String username, String password, String email, String birthDate) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.email = email;

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.birthDate = LocalDate.parse(birthDate, inputFormatter).format(outputFormatter);
    }

    public UserRequest() {}

    // Getters e Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }
}
