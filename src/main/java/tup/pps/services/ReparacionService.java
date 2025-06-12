package tup.pps.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tup.pps.dtos.ReparacionDTO;
import tup.pps.entities.FacturaEntity;
import tup.pps.models.Reparacion;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface ReparacionService {
    Reparacion save(ReparacionDTO reparacionDTO);
    Reparacion save(ReparacionDTO reparacionDTO, FacturaEntity factura);
    Reparacion update(Long id, ReparacionDTO reparacionDTO);
    Page<Reparacion> findAll(
            Pageable pageable,
            String usuario,
            List<String> trabajos,
            LocalDateTime fechaInicio,
            LocalDateTime fechaEntrega,
            Double precioMin,
            Double precioMax,
            Boolean activo
    );
    Reparacion findById(Long id);
    void delete(Long id);
}
