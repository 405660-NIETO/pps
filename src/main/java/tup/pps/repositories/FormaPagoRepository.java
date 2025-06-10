package tup.pps.repositories;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import tup.pps.entities.FormaPagoEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface FormaPagoRepository extends JpaRepository<FormaPagoEntity, Long>, JpaSpecificationExecutor<FormaPagoEntity> {

    Optional<FormaPagoEntity> findByNombre(String nombre);

    List<FormaPagoEntity> findAll(Specification<FormaPagoEntity> filter);
}
