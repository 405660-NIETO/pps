package tup.pps.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Detalle_Factura")
public class DetalleFacturaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "factura_id",  nullable = false)
    private FacturaEntity factura;

    @ManyToOne
    @JoinColumn(name = "producto_id",  nullable = false)
    private ProductoEntity producto;

    @Column(nullable = false)
    private Double precio;

    @Column(nullable = false)
    private Integer cantidad;
}
