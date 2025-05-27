package tup.pps.services;

import org.springframework.stereotype.Service;
import tup.pps.models.Producto;

import java.util.List;

@Service
public interface ProductoService {
    List<Producto> findAll();
    Producto findById(Long id);
    Producto save(Producto producto);
    Producto update(Producto producto);
    void delete(Long id);
}
