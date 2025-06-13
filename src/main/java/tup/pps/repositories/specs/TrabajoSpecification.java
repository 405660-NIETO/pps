package tup.pps.repositories.specs;

import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import tup.pps.entities.TrabajoEntity;

@Component
@NoArgsConstructor
public class TrabajoSpecification {

    public Specification<TrabajoEntity> byNombre(String nombre) {
        return (root, query, builder) -> {
            if (nombre == null || nombre.isBlank()) {
                return builder.conjunction();
            }
            String pattern = "%" + nombre + "%";
            return builder.like(root.get("nombre"), pattern);
        };
    }

    public Specification<TrabajoEntity> byActivo(Boolean activo) {
        return (root, query, builder) -> {
            if (activo == null) {
                return builder.conjunction();
            }
            return builder.equal(root.get("activo"), activo);
        };
    }
}
