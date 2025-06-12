package tup.pps.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tup.pps.dtos.ProductoDTO;
import tup.pps.entities.ProductoEntity;
import tup.pps.models.Producto;

import java.util.List;
import java.util.Optional;

@Service
public interface ProductoService {
    Producto save(ProductoDTO productoDTO);
    Producto update(Long id, ProductoDTO productoDTO);

    Page<Producto> findAll(
            Pageable pageable,
            String nombre,
            String nombreMarca,
            List<String> categorias,
            Double precioMin,
            Double precioMax,
            Integer stockMin,
            Integer stockMax,
            Boolean activo
    );
    Producto findById(Long id);
    void delete(Long id);
    Optional<ProductoEntity> findEntityById(Long id);
}
