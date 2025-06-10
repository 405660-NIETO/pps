package tup.pps.repositories;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import tup.pps.entities.SedeEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface SedeRepository extends JpaRepository<SedeEntity, Long>, JpaSpecificationExecutor<SedeEntity> {

    Optional<SedeEntity> findByDireccion(String direccion);  // direccion es unique

    List<SedeEntity> findAll(Specification<SedeEntity> filter);
}