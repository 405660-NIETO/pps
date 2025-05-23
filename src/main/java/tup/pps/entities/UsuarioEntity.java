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
@Table(name = "Usuarios")
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    private String nombre;

    private String apellido;

    @ManyToOne
    @JoinColumn(name = "rol_id")
    private RolEntity rol;

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaLogin;

    private Boolean activo;
}
