package utp.edu.pe.GrupoUnion.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String status;
    private String message;
    private String role;
    private String redirectUrl;
    private String username;
}
