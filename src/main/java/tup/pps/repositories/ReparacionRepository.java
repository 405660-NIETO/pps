package tup.pps.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import tup.pps.entities.ReparacionEntity;

@Repository
public interface ReparacionRepository extends JpaRepository<ReparacionEntity, Long>, JpaSpecificationExecutor<ReparacionEntity> {
}
