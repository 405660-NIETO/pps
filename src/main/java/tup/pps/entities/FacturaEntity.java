package tup.pps.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Facturas")
public class FacturaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fecha;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private UsuarioEntity usuario;

    @ManyToOne
    @JoinColumn(name = "forma_pago_id")
    private FormaPagoEntity formaPago;

    @ManyToOne
    @JoinColumn(name = "sede_id")
    private SedeEntity sede;

    private String clienteNombre;

    private String clienteApellido;

    private String clienteDocumento;

    private String clienteTelefono;

    private String clienteCelular;

    private String clienteEmail;
}
