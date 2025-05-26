package tup.pps.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tup.pps.entities.DetalleFacturaEntity;

@Repository
public interface DetalleFacturaRepository extends JpaRepository<DetalleFacturaEntity, Long> {
}
