package tup.pps.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tup.pps.models.Trabajo;

@Service
public interface TrabajoService {
    Page<Trabajo> findAll(Pageable pageable, String nombre, Boolean activo);
    Trabajo findById(Long id);
    Trabajo save(Trabajo trabajo);
    void delete(String nombre);
}