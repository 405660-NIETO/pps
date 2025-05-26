package tup.pps.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tup.pps.entities.FormaPagoEntity;

@Repository
public interface FormaPagoRepository extends JpaRepository<FormaPagoEntity, Long> {
}
