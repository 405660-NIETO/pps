package tup.pps.dtos.usuarios;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UsuarioDTO - Response para información de usuario sin datos sensibles
 *
 * Usado específicamente en /auth/me para enviar al frontend
 * solo los datos necesarios (sin password, sin fechas internas)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDTO {

    private String email;
    private String nombre;
    private String apellido;
    private String rol;

    @Override
    public String toString() {
        return "UsuarioDTO{" +
                "email='" + email + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", rol='" + rol + '\'' +
                '}';
    }
}