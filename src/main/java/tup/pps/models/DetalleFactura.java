package tup.pps.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetalleFactura {
    private Long id;
    private Producto producto;
    private Double precio;
    private Integer cantidad;
}
