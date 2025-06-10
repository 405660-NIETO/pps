package tup.pps.repositories.specs;

import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import tup.pps.entities.FormaPagoEntity;

@Component
@NoArgsConstructor
public class FormaPagoSpecification {

    public Specification<FormaPagoEntity> byNombre(String nombre) {
        return (root, query, builder) -> {
            if (nombre == null || nombre.isBlank()) {
                return builder.conjunction();
            }
            String pattern = "%" + nombre + "%";
            return builder.like(root.get("nombre"), pattern);
        };
    }

    public Specification<FormaPagoEntity> byActivo(Boolean activo) {
        return (root, query, builder) -> {
            if (activo == null) {
                return builder.conjunction();
            }
            return builder.equal(root.get("activo"), activo);
        };
    }
}
