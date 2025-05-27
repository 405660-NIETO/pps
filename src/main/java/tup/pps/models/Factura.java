package tup.pps.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Factura {
    private Long id;
    private LocalDateTime fecha;
    private Usuario usuario;
    private FormaPago formaPago;
    private Sede sede;

    private List<DetalleFactura> detalles;
    private List<Reparacion> reparaciones;

    private String clienteNombre;
    private String clienteApellido;
    private String clienteDocumento;
    private String clienteTelefono;
    private String clienteCelular;
    private String clienteEmail;
}
