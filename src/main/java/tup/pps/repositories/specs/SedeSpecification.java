package tup.pps.repositories.specs;

import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import tup.pps.entities.SedeEntity;

@Component
@NoArgsConstructor
public class SedeSpecification {

    public Specification<SedeEntity> byNombre(String nombre) {
        return (root, query, builder) -> {
            if (nombre == null || nombre.isBlank()) {
                return builder.conjunction();
            }
            String pattern = "%" + nombre + "%";
            return builder.like(root.get("nombre"), pattern);
        };
    }

    public Specification<SedeEntity> byDireccion(String direccion) {
        return (root, query, builder) -> {
            if (direccion == null || direccion.isBlank()) {
                return builder.conjunction();
            }
            String pattern = "%" + direccion + "%";
            return builder.like(root.get("direccion"), pattern);
        };
    }

    public Specification<SedeEntity> byActivo(Boolean activo) {
        return (root, query, builder) -> {
            if (activo == null) {
                return builder.conjunction();
            }
            return builder.equal(root.get("activo"), activo);
        };
    }
}
