package tup.pps.repositories.specs;

import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import tup.pps.entities.DetalleFacturaEntity;
import tup.pps.entities.FacturaEntity;
import tup.pps.entities.ReparacionEntity;

import java.time.LocalDateTime;

@Component
@NoArgsConstructor
public class FacturaSpecification {

    // 1. Filtro por rango de fechas (reutilizar patrón de Reparacion)
    public Specification<FacturaEntity> byFechaRango(LocalDateTime fechaDesde, LocalDateTime fechaHasta) {
        return (root, query, builder) -> {
            if (fechaDesde == null && fechaHasta == null) {
                return builder.conjunction();
            }

            if (fechaHasta == null) {
                return builder.greaterThanOrEqualTo(root.get("fecha"), fechaDesde);
            }

            if (fechaDesde == null) {
                return builder.lessThanOrEqualTo(root.get("fecha"), fechaHasta);
            }

            return builder.between(root.get("fecha"), fechaDesde, fechaHasta);
        };
    }

    // 2. Filtro por usuario (simple, nuevo)
    public Specification<FacturaEntity> byUsuario(Long usuarioId) {
        return (root, query, builder) -> {
            if (usuarioId == null) {
                return builder.conjunction();
            }
            return builder.equal(root.get("usuario").get("id"), usuarioId);
        };
    }

    // 3. Filtro por sede (simple, nuevo)
    public Specification<FacturaEntity> bySede(Long sedeId) {
        return (root, query, builder) -> {
            if (sedeId == null) {
                return builder.conjunction();
            }
            return builder.equal(root.get("sede").get("id"), sedeId);
        };
    }

    // 4. Filtro por forma de pago (simple, nuevo)
    public Specification<FacturaEntity> byFormaPago(Long formaPagoId) {
        return (root, query, builder) -> {
            if (formaPagoId == null) {
                return builder.conjunction();
            }
            return builder.equal(root.get("formaPago").get("id"), formaPagoId);
        };
    }

    // 5. Filtro por activo (reutilizar patrón)
    public Specification<FacturaEntity> byActivo(Boolean activo) {
        return (root, query, builder) -> {
            if (activo == null) {
                return builder.conjunction();
            }
            // TODO: ¿FacturaEntity tiene campo activo? ¿O manejamos borrado diferente?
            return builder.equal(root.get("activo"), activo);
        };
    }

    // 6. Filtro por rango de monto (SUM dinámico de detalles + reparaciones)
    public Specification<FacturaEntity> byMontoRango(Double montoMin, Double montoMax) {
        return (root, query, builder) -> {
            if (montoMin == null && montoMax == null) {
                return builder.conjunction();
            }

            // Subquery 1: SUM de detalles (precio * cantidad)
            Subquery<Double> sumDetalles = query.subquery(Double.class);
            Root<DetalleFacturaEntity> detalleRoot = sumDetalles.from(DetalleFacturaEntity.class);
            sumDetalles.select(
                    builder.coalesce(
                            builder.sum(
                                    builder.prod(detalleRoot.get("precio"), detalleRoot.get("cantidad"))
                            ), 0.0
                    )
            ).where(
                    builder.equal(detalleRoot.get("factura").get("id"), root.get("id"))
            );

            // Subquery 2: SUM de reparaciones (precio)
            Subquery<Double> sumReparaciones = query.subquery(Double.class);
            Root<ReparacionEntity> reparacionRoot = sumReparaciones.from(ReparacionEntity.class);
            sumReparaciones.select(
                    builder.coalesce(builder.sum(reparacionRoot.get("precio")), 0.0)
            ).where(
                    builder.equal(reparacionRoot.get("factura").get("id"), root.get("id"))
            );

            // Expresión: sumDetalles + sumReparaciones
            var montoTotal = builder.sum(sumDetalles, sumReparaciones);

            // Aplicar filtros de rango
            if (montoMax == null) {
                return builder.greaterThanOrEqualTo(montoTotal, montoMin);
            }

            if (montoMin == null) {
                return builder.lessThanOrEqualTo(montoTotal, montoMax);
            }

            return builder.between(montoTotal, montoMin, montoMax);
        };
    }

    // 7. Filtro por tiene reparaciones (EXISTS en tabla reparaciones)
    public Specification<FacturaEntity> byTieneReparaciones(Boolean tieneReparaciones) {
        return (root, query, builder) -> {
            if (tieneReparaciones == null) {
                return builder.conjunction();
            }

            // Subquery: EXISTS reparacion WHERE factura_id = f.id
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<ReparacionEntity> reparacionRoot = subquery.from(ReparacionEntity.class);
            subquery.select(builder.literal(1L))
                    .where(builder.equal(reparacionRoot.get("factura").get("id"), root.get("id")));

            if (tieneReparaciones) {
                return builder.exists(subquery);  // Facturas CON reparaciones
            } else {
                return builder.not(builder.exists(subquery));  // Facturas SIN reparaciones
            }
        };
    }

    // 8. Filtro por tiene productos (EXISTS en tabla detalle_factura)
    public Specification<FacturaEntity> byTieneProductos(Boolean tieneProductos) {
        return (root, query, builder) -> {
            if (tieneProductos == null) {
                return builder.conjunction();
            }

            // Subquery: EXISTS detalle_factura WHERE factura_id = f.id
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<DetalleFacturaEntity> detalleRoot = subquery.from(DetalleFacturaEntity.class);
            subquery.select(builder.literal(1L))
                    .where(builder.equal(detalleRoot.get("factura").get("id"), root.get("id")));

            if (tieneProductos) {
                return builder.exists(subquery);  // Facturas CON productos
            } else {
                return builder.not(builder.exists(subquery));  // Facturas SIN productos
            }
        };
    }

    // 9. Filtro por cantidad mínima de productos (SUM de cantidad en detalles)
    public Specification<FacturaEntity> byCantidadItemsMin(Integer cantidadItemsMin) {
        return (root, query, builder) -> {
            if (cantidadItemsMin == null) {
                return builder.conjunction();
            }

            // Subquery: SUM(cantidad) de detalle_factura WHERE factura_id = f.id
            Subquery<Integer> subquery = query.subquery(Integer.class);
            Root<DetalleFacturaEntity> detalleRoot = subquery.from(DetalleFacturaEntity.class);
            subquery.select(
                    builder.coalesce(
                            builder.sum(detalleRoot.get("cantidad")), 0
                    )
            ).where(
                    builder.equal(detalleRoot.get("factura").get("id"), root.get("id"))
            );

            // Filtrar facturas donde SUM(cantidad) >= cantidadItemsMin
            return builder.greaterThanOrEqualTo(subquery, cantidadItemsMin);
        };
    }

}