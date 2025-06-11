package tup.pps.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tup.pps.entities.ReparacionEntity;
import tup.pps.entities.ReparacionXTrabajoEntity;
import tup.pps.entities.TrabajoEntity;

import java.util.List;

@Repository
public interface ReparacionXTrabajoRepository extends JpaRepository<ReparacionXTrabajoEntity, Long> {

    List<ReparacionXTrabajoEntity> findByReparacion(ReparacionEntity reparacion);

    List<ReparacionXTrabajoEntity> findByReparacionAndActivoIsTrue(ReparacionEntity reparacion);

    List<ReparacionXTrabajoEntity> findByReparacionAndTrabajo(ReparacionEntity reparacion, TrabajoEntity trabajo);
}
