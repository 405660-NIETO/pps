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

    private String nombre;

    private String comentarios;

    private String fotoUrl;

    @ManyToOne
    @JoinColumn(name = "marca_id")
    private MarcaEntity marca;

    private Integer stock;

    private Double precio;

    private Boolean activo;
}
