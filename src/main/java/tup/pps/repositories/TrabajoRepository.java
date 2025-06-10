package tup.pps.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import tup.pps.entities.TrabajoEntity;

import java.util.Optional;

@Repository
public interface TrabajoRepository extends JpaRepository<TrabajoEntity, Long>, JpaSpecificationExecutor<TrabajoEntity> {

    Optional<TrabajoEntity> findByNombre(String nombre);

    Page<TrabajoEntity> findAll(Specification<TrabajoEntity> filter, Pageable pageable);
}