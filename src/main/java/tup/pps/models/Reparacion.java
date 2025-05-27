package tup.pps.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reparacion {
    private Long id;
    private Usuario usuario;

    private List<Trabajo> trabajos;

    private String detalles;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaEntrega;
    private Double precio;
    private Boolean activo;
}
