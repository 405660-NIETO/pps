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
@Table(name = "Reparaciones")
public class ReparacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private UsuarioEntity usuario;

    private String detalles;

    private LocalDateTime fechaInicio;

    private LocalDateTime fechaEntrega;

    @ManyToOne
    @JoinColumn(name = "factura_id")
    private FacturaEntity factura;

    private Double precio;


    private Boolean activo;
}
