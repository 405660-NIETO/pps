package tup.pps.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tup.pps.entities.TrabajoEntity;

@Repository
public interface TrabajoRepository extends JpaRepository<TrabajoEntity, Long> {
}
