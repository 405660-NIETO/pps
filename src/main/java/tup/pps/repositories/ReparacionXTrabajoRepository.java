package tup.pps.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tup.pps.entities.ReparacionXTrabajoEntity;

@Repository
public interface ReparacionXTrabajoRepository extends JpaRepository<ReparacionXTrabajoEntity, Long> {
}
