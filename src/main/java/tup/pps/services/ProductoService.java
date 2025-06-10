package tup.pps.services;

import org.springframework.stereotype.Service;
import tup.pps.dtos.ProductoDTO;
import tup.pps.models.Producto;

@Service
public interface ProductoService {
    Producto save(ProductoDTO productoDTO);

    // TODO: Agregar después
    // Page<Producto> findAll(Pageable pageable, filtros...);
    // Producto findById(Long id);
    // Producto update(Long id, ProductoDTO productoDTO);
    // void delete(Long id);
}
