package tup.pps.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tup.pps.entities.CategoriaEntity;
import tup.pps.entities.ProductoEntity;
import tup.pps.entities.ProductoXCategoriaEntity;

import java.util.List;

@Repository
public interface ProductoXCategoriaRepository extends JpaRepository<ProductoXCategoriaEntity, Long> {

    List<ProductoXCategoriaEntity> findByProducto(ProductoEntity producto);

    List<ProductoXCategoriaEntity> findByProductoAndActivoIsTrue(ProductoEntity producto);

    List<ProductoXCategoriaEntity> findByProductoAndCategoria(ProductoEntity producto, CategoriaEntity categoria);
}
