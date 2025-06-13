package tup.pps.repositories.specs;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.data.jpa.domain.Specification;
import tup.pps.entities.MarcaEntity;

@Component
@NoArgsConstructor
public class MarcaSpecification {

    public Specification<MarcaEntity> byNombre(String nombre) {
        return (root, query, builder) -> {
            if (nombre == null || nombre.isBlank()) {
                return builder.conjunction();
            }
            String pattern = "%" + nombre + "%";
            return builder.like(root.get("nombre"), pattern);
        };
    }

    public Specification<MarcaEntity> byActivo(Boolean activo) {
        return (root, query, builder) -> {
            if (activo == null) {
                return builder.conjunction();
            }
            return builder.equal(root.get("activo"), activo);
        };
    }
}
