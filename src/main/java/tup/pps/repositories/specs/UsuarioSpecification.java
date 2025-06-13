package tup.pps.repositories.specs;

import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import tup.pps.entities.UsuarioEntity;

import java.time.LocalDateTime;

@Component
@NoArgsConstructor
public class UsuarioSpecification {

    // 1. Email - Pattern de tablas soporte (CategoriaSpecification)
    public Specification<UsuarioEntity> byEmail(String email) {
        return (root, query, builder) -> {
            if (email == null || email.isBlank()) {
                return builder.conjunction();
            }
            String pattern = "%" + email + "%";
            return builder.like(root.get("email"), pattern);
        };
    }

    // 2. Nombre/Apellido - Adaptado de ReparacionSpecification.byUsuario()
    public Specification<UsuarioEntity> byNombreApellido(String nombreApellido) {
        return (root, query, builder) -> {
            if (nombreApellido == null || nombreApellido.isBlank()) {
                return builder.conjunction();
            }

            // Buscar en nombre OR apellido (empieza por)
            String pattern = nombreApellido + "%";  // Solo al inicio

            return builder.or(
                    builder.like(root.get("nombre"), pattern),
                    builder.like(root.get("apellido"), pattern)
            );
        };
    }

    // 3. Rol ID - Simple JOIN (pattern de ProductoSpecification.byMarca)
    public Specification<UsuarioEntity> byRol(Long rolId) {
        return (root, query, builder) -> {
            if (rolId == null) {
                return builder.conjunction();
            }
            return builder.equal(root.get("rol").get("id"), rolId);
        };
    }

    // 4. Fecha rango - Copy exact de ReparacionSpecification.byFechaRango()
    public Specification<UsuarioEntity> byFechaRango(LocalDateTime fechaDesde, LocalDateTime fechaHasta) {
        return (root, query, builder) -> {
            if (fechaDesde == null && fechaHasta == null) {
                return builder.conjunction();
            }

            if (fechaHasta == null) {
                return builder.greaterThanOrEqualTo(root.get("fechaCreacion"), fechaDesde);
            }

            if (fechaDesde == null) {
                return builder.lessThanOrEqualTo(root.get("fechaCreacion"), fechaHasta);
            }

            return builder.between(root.get("fechaCreacion"), fechaDesde, fechaHasta);
        };
    }

    // 5. Activo - Pattern universal
    public Specification<UsuarioEntity> byActivo(Boolean activo) {
        return (root, query, builder) -> {
            if (activo == null) {
                return builder.conjunction();
            }
            return builder.equal(root.get("activo"), activo);
        };
    }
}
