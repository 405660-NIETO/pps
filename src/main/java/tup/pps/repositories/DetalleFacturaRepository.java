package tup.pps.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tup.pps.entities.DetalleFacturaEntity;

import java.util.List;

@Repository
public interface DetalleFacturaRepository extends JpaRepository<DetalleFacturaEntity, Long> {

    List<DetalleFacturaEntity> findByFactura_Id(Long facturaId);
}
