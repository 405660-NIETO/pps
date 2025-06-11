package tup.pps.repositories.specs;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import tup.pps.entities.CategoriaEntity;
import tup.pps.entities.MarcaEntity;
import tup.pps.entities.ProductoEntity;
import tup.pps.entities.ProductoXCategoriaEntity;

import java.util.List;

@Component
@NoArgsConstructor
public class ProductoSpecification {

    public Specification<ProductoEntity> byNombre(String nombre) {
        return (root, query, builder) -> {
            if (nombre == null || nombre.isBlank()) {
                return builder.conjunction();
            }
            String pattern = "%" + nombre + "%";
            return builder.like(root.get("nombre"), pattern);
        };
    }

    public Specification<ProductoEntity> byMarca(String nombreMarca) {
        return (root, query, builder) -> {
            if (nombreMarca == null || nombreMarca.isBlank()) {
                return builder.conjunction();
            }
            Join<ProductoEntity, MarcaEntity> marca = root.join("marca");
            String pattern = "%" + nombreMarca + "%";
            return builder.like(marca.get("nombre"), pattern);
        };
    }

    public Specification<ProductoEntity> byCategorias(List<String> nombresCategorias) {
        return (root, query, builder) -> {
            if (nombresCategorias == null || nombresCategorias.isEmpty()) {
                return builder.conjunction();
            }

            // Subquery que cuenta cuántas categorías coinciden
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<ProductoXCategoriaEntity> relacion = subquery.from(ProductoXCategoriaEntity.class);
            Join<ProductoXCategoriaEntity, CategoriaEntity> categoria = relacion.join("categoria");

            subquery.select(builder.count(relacion.get("id")))
                    .where(builder.and(
                            builder.equal(relacion.get("producto").get("id"), root.get("id")),
                            categoria.get("nombre").in(nombresCategorias),
                            builder.equal(relacion.get("activo"), true)
                    ));

            // El producto debe tener TODAS las categorías solicitadas
            return builder.equal(subquery, (long) nombresCategorias.size());
        };
    }

    public Specification<ProductoEntity> byPrecioRango(Double precioMin, Double precioMax) {
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

    public Specification<ProductoEntity> byStockRango(Integer stockMin, Integer stockMax) {
        return (root, query, builder) -> {
            if (stockMin == null && stockMax == null) {
                return builder.conjunction();
            }

            if (stockMax == null) {
                return builder.greaterThanOrEqualTo(root.get("stock"), stockMin);
            }

            if (stockMin == null) {
                return builder.lessThanOrEqualTo(root.get("stock"), stockMax);
            }

            return builder.between(root.get("stock"), stockMin, stockMax);
        };
    }

    public Specification<ProductoEntity> byActivo(Boolean activo) {
        return (root, query, builder) -> {
            if (activo == null) {
                return builder.conjunction();
            }
            return builder.equal(root.get("activo"), activo);
        };
    }
}
