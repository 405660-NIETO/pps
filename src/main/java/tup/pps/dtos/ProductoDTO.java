package tup.pps.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoDTO {
    private String nombre;
    private String comentarios;
    private String fotoUrl;
    private String marca;
    private List<String> categorias;
    private Integer stock;
    private Double precio;
}
