package tup.pps.services;

import org.springframework.stereotype.Service;
import tup.pps.dtos.FacturaDTO;
import tup.pps.entities.FacturaEntity;
import tup.pps.models.Factura;

import java.util.Optional;

@Service
public interface FacturaService {
    Optional<FacturaEntity> findEntityById(Long id);
    Factura save(FacturaDTO facturaDTO);
}
