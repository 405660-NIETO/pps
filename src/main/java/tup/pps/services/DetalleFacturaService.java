package tup.pps.services;

import org.springframework.stereotype.Service;
import tup.pps.dtos.DetalleFacturaDTO;
import tup.pps.entities.FacturaEntity;
import tup.pps.models.DetalleFactura;

@Service
public interface DetalleFacturaService {
    DetalleFactura save(DetalleFacturaDTO dto, FacturaEntity factura);
    DetalleFactura findById(Long id);
}
