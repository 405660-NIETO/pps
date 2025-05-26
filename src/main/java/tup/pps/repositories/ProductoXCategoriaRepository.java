package tup.pps.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tup.pps.entities.ProductoXCategoriaEntity;

@Repository
public interface ProductoXCategoriaRepository extends JpaRepository<ProductoXCategoriaEntity, Long> {
}
