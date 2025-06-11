package tup.pps.services;

import org.springframework.stereotype.Service;
import tup.pps.dtos.ReparacionDTO;
import tup.pps.models.Reparacion;

@Service
public interface ReparacionService {
    Reparacion save(ReparacionDTO reparacionDTO);
}
