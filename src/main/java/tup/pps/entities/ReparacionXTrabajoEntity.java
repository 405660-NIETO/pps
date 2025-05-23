package tup.pps.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Reparaciones_X_Trabajos")
public class ReparacionXTrabajoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reparacion_id", nullable = false)
    private ReparacionEntity reparacion;

    @ManyToOne
    @JoinColumn(name = "trabajo_id", nullable = false)
    private TrabajoEntity trabajo;
}
