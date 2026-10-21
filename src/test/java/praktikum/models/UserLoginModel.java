package praktikum.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UserLoginModel {

    private String email;
    private String password;

    public UserLoginModel(String email, String password) {
        this.email = email;
        this.password = password;
    }
}