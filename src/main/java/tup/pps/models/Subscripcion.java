package tup.pps.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Subscripcion {
    private Long id;
    private String email;
    private LocalDateTime fechaAlta;
    private LocalDateTime fechaBaja;
    private Boolean activo;
}
