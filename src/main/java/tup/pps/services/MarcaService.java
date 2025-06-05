package tup.pps.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tup.pps.models.Marca;

@Service
public interface MarcaService {
    Page<Marca> findAll(Pageable pageable, String nombre, Boolean activo);
    Marca findById(Long id);
    Marca save(Marca marca);
    void delete(String nombre);
}
