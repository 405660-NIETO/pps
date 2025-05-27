package tup.pps.services;

import org.springframework.stereotype.Service;
import tup.pps.models.Categoria;

import java.util.List;

@Service
public interface CategoriaService {
    List<Categoria> findAll();
    Categoria findById(Long id);
    Categoria save(Categoria categoria);
    void delete(String nombre);
}
