package utp.edu.pe.GrupoUnion.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    @Email
    private String username;

    @NotBlank
    private String password;
}
