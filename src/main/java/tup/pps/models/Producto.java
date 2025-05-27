package tup.pps.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Producto {
    private Long id;
    private String nombre;
    private String comentarios;
    private String fotoUrl;
    private Marca marca;

    private List<Categoria> categorias;

    private Integer stock;
    private Double precio;
    private Boolean activo;
}
