package praktikum.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor

public class UserRegistrationModel {

    private String email;
    private String password;
    private String name;

    public UserRegistrationModel(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }
}