package tup.pps.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sede {
    private Long id;
    private String nombre;
    private String direccion;
    private Boolean activo;
}
