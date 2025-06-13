package tup.pps.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tup.pps.entities.SubscripcionEntity;

@Repository
public interface SubscripcionRepository extends JpaRepository<SubscripcionEntity, Long> {
}
