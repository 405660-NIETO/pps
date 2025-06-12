package tup.pps.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetalleFacturaDTO {
    private Long productoId;    // ID del producto
    private Integer cantidad;   // Cuántos
    private Double precio;      // Opcional: para casos especiales (descuentos, etc)
}