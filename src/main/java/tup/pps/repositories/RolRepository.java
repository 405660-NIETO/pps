package tup.pps.repositories;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import tup.pps.entities.RolEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<RolEntity, Long>, JpaSpecificationExecutor<RolEntity> {

    Optional<RolEntity> findByNombre(String nombre);

    List<RolEntity> findAll(Specification<RolEntity> filter);
}
