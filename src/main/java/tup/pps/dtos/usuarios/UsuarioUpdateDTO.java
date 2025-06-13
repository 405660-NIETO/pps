package tup.pps.dtos.usuarios;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioUpdateDTO {
    String nombre;
    String apellido;
    String passwordActual;
    String passwordNueva;
}
