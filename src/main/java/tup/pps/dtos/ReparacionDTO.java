package tup.pps.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReparacionDTO {
    private Long usuarioId;  // ID del luthier/empleado
    private Long facturaId;  // asociar a factura existente
    private String detalles;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaEntrega;
    private Double precio;
    private List<String> trabajos;  // Nombres de trabajos (como categorías en Producto)
}