package tup.pps.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResultDTO {
    private String email;
    private String nombre;
    private String apellido;
    private LocalDateTime fechaLogin;
    private String rol;  // Nombre del rol para frontend
}
