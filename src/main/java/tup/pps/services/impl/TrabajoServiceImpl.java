package tup.pps.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tup.pps.entities.TrabajoEntity;
import tup.pps.exceptions.ConflictiveStateException;
import tup.pps.exceptions.EntryNotFoundException;
import tup.pps.exceptions.ResourceAlreadyExistsException;
import tup.pps.models.Trabajo;
import tup.pps.repositories.TrabajoRepository;
import tup.pps.repositories.specs.TrabajoSpecification;
import tup.pps.services.TrabajoService;

import java.util.Optional;

/**
 * Tabla Soporte - Patrón A
 * Alto volumen, con paginado, sin update
 */
@Service
public class TrabajoServiceImpl implements TrabajoService {

    @Autowired
    private TrabajoRepository repository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TrabajoSpecification specification;

    @Override
    public Page<Trabajo> findAll(
            Pageable pageable,
            String nombre,
            Boolean activo
    ) {
        Specification<TrabajoEntity> spec = specification.byNombre(nombre)
                .and(specification.byActivo(activo));

        Page<TrabajoEntity> entityPage = repository.findAll(spec, pageable);

        return entityPage.map(entity -> modelMapper.map(entity, Trabajo.class));
    }

    @Override
    public Trabajo findById(Long id) {
        return repository.findById(id)
                .map(entity -> modelMapper.map(entity, Trabajo.class))
                .orElseThrow(() -> new EntryNotFoundException("No se encontro ningun trabajo con ese ID"));
    }

    @Override
    public Trabajo save(String trabajo) {
        Optional<TrabajoEntity> trabajoOpcional = repository.findByNombre(trabajo);
        if(trabajoOpcional.isPresent()) {
            if(trabajoOpcional.get().getActivo()) {
                throw new ResourceAlreadyExistsException("Ya hay un trabajo con ese nombre");
            }
            else {
                trabajoOpcional.get().setActivo(true);
                TrabajoEntity entity = repository.save(trabajoOpcional.get());
                return modelMapper.map(entity, Trabajo.class);
            }
        }

        TrabajoEntity entity = new TrabajoEntity();
        entity.setNombre(trabajo);
        entity.setActivo(true);
        entity = repository.save(entity);
        return modelMapper.map(entity, Trabajo.class);
    }

    @Override
    public void delete(String nombre) {
        TrabajoEntity entity = repository.findByNombre(nombre)
                .orElseThrow(() -> new EntryNotFoundException("No se encontro un trabajo con ese nombre"));

        if(!entity.getActivo()) {
            throw new ConflictiveStateException("El trabajo ya esta desactivado");
        }

        entity.setActivo(false);
        repository.save(entity);
    }

    @Override
    public Optional<TrabajoEntity> findByNombre(String nombre) {
        return repository.findByNombre(nombre);
    }
}
