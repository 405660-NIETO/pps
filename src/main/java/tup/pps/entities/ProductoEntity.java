package tup.pps.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Productos")
public class ProductoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String comentarios;

    private String fotoUrl;

    @ManyToOne
    @JoinColumn(name = "marca_id")
    private MarcaEntity marca;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private Double precio;

    private Boolean activo;
}
