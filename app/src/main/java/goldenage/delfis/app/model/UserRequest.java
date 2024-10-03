package goldenage.delfis.app.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
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

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.birthDate = LocalDate.parse(birthDate, inputFormatter).format(outputFormatter);
    }
}
