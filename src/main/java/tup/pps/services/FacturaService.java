package tup.pps.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tup.pps.dtos.FacturaDTO;
import tup.pps.entities.FacturaEntity;
import tup.pps.models.Factura;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public interface FacturaService {
    Optional<FacturaEntity> findEntityById(Long id);
    Factura save(FacturaDTO facturaDTO);
    Page<Factura> findAll(
            Pageable pageable,
            LocalDateTime fechaDesde,
            LocalDateTime fechaHasta,
            Long usuarioId,
            Long sedeId,
            Long formaPagoId,
            Double montoMin,
            Double montoMax,
            Boolean tieneReparaciones,
            Boolean tieneProductos,
            Integer cantidadItemsMin,
            Boolean activo
    );
}
