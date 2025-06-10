package tup.pps.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tup.pps.entities.CategoriaEntity;
import tup.pps.models.Categoria;

import java.util.Optional;

@Service
public interface CategoriaService {
    Page<Categoria> findAll(
            Pageable pageable,
            String name,
            Boolean activo
    );
    Categoria findById(Long id);
    Categoria save(String categoria);
    void delete(String nombre);
    Optional<CategoriaEntity> findByNombre(String nombre);
}
