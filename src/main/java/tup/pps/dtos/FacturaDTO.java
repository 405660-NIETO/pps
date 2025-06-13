package tup.pps.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FacturaDTO {
    private Long usuarioId;        // Quien hizo la venta (empleado/admin)
    private Long sedeId;           // Donde se vendió
    private Long formaPagoId;      // Como se pagó

    // DATOS DEL CLIENTE
    private String clienteNombre;
    private String clienteApellido;
    private String clienteDocumento;
    private String clienteTelefono;
    private String clienteCelular;
    private String clienteEmail;

    // ORQUESTACIÓN DE SUB-ENTIDADES
    private List<Long> reparacionIds;              // IDs de reparaciones a facturar
    private List<DetalleFacturaDTO> productos;     // IDS de productos + cantidades + precios

    // NOTA: La fecha se genera automáticamente en el servicio
}