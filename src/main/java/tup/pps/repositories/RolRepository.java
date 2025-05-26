package tup.pps.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tup.pps.entities.RolEntity;

@Repository
public interface RolRepository extends JpaRepository<RolEntity, Long> {
}
