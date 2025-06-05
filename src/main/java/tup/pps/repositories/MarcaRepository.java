package tup.pps.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import tup.pps.entities.MarcaEntity;

import java.util.Optional;

@Repository
public interface MarcaRepository extends JpaRepository<MarcaEntity, Long>, JpaSpecificationExecutor<MarcaEntity> {
    Optional<MarcaEntity> findByNombre(String nombre);
    Page<MarcaEntity> findAll(Specification<MarcaEntity> filter, Pageable pageable);
}