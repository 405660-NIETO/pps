package tup.pps.services;

import org.springframework.stereotype.Service;
import tup.pps.entities.FacturaEntity;

import java.util.Optional;

@Service
public interface FacturaService {

    Optional<FacturaEntity> findEntityById(Long id);
}
