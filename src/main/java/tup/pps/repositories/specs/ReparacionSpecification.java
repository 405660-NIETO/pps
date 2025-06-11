package tup.pps.repositories.specs;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import tup.pps.entities.ReparacionEntity;
import tup.pps.entities.ReparacionXTrabajoEntity;
import tup.pps.entities.TrabajoEntity;
import tup.pps.entities.UsuarioEntity;

import java.time.LocalDateTime;
import java.util.List;

@Component
@NoArgsConstructor
public class ReparacionSpecification {

    public Specification<ReparacionEntity> byUsuario(String nombre) {
        return (root, query, builder) -> {
            if (nombre == null || nombre.isBlank()) {
                return builder.conjunction();
            }

            // Join con usuario
            Join<ReparacionEntity, UsuarioEntity> usuario = root.join("usuario");

            // Buscar en nombre OR apellido (empieza por)
            String pattern = nombre + "%";  // Solo al inicio

            return builder.or(
                    builder.like(usuario.get("nombre"), pattern),
                    builder.like(usuario.get("apellido"), pattern)
            );
        };
    }

    public Specification<ReparacionEntity> byTrabajos(List<String> nombresTrabajos) {
        return (root, query, builder) -> {
            if (nombresTrabajos == null || nombresTrabajos.isEmpty()) {
                return builder.conjunction();
            }

            // Subquery que cuenta cuántos trabajos coinciden
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<ReparacionXTrabajoEntity> relacion = subquery.from(ReparacionXTrabajoEntity.class);
            Join<ReparacionXTrabajoEntity, TrabajoEntity> trabajo = relacion.join("trabajo");

            subquery.select(builder.count(relacion.get("id")))
                    .where(builder.and(
                            builder.equal(relacion.get("reparacion").get("id"), root.get("id")),
                            trabajo.get("nombre").in(nombresTrabajos),
                            builder.equal(relacion.get("activo"), true)
                    ));

            // La reparación debe tener TODOS los trabajos solicitados
            return builder.equal(subquery, (long) nombresTrabajos.size());
        };
    }

    public Specification<ReparacionEntity> byPrecioRango(Double precioMin, Double precioMax) {
        return (root, query, builder) -> {
            if (precioMin == null && precioMax == null) {
                return builder.conjunction();
            }

            if (precioMax == null) {
                return builder.greaterThanOrEqualTo(root.get("precio"), precioMin);
            }

            if (precioMin == null) {
                return builder.lessThanOrEqualTo(root.get("precio"), precioMax);
            }

            return builder.between(root.get("precio"), precioMin, precioMax);
        };
    }

    public Specification<ReparacionEntity> byFechaRango(LocalDateTime fechaInicio, LocalDateTime fechaEntrega) {
        return (root, query, builder) -> {
            if (fechaInicio == null && fechaEntrega == null) {
                return builder.conjunction();
            }

            if (fechaEntrega == null) {
                return builder.greaterThanOrEqualTo(root.get("fechaInicio"), fechaInicio);
            }

            if (fechaInicio == null) {
                return builder.lessThanOrEqualTo(root.get("fechaEntrega"), fechaEntrega);
            }

            return builder.between(root.get("fechaInicio"), fechaInicio, fechaEntrega);
        };
    }

    public Specification<ReparacionEntity> byActivo(Boolean activo) {
        return (root, query, builder) -> {
            if (activo == null) {
                return builder.conjunction();
            }
            return builder.equal(root.get("activo"), activo);
        };
    }
}
