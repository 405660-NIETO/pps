package tup.pps.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tup.pps.entities.CategoriaEntity;
import tup.pps.exceptions.ConflictiveStateException;
import tup.pps.exceptions.EntryNotFoundException;
import tup.pps.exceptions.ResourceAlreadyExistsException;
import tup.pps.models.Categoria;
import tup.pps.repositories.CategoriaRepository;
import tup.pps.repositories.specs.CategoriaSpecification;
import tup.pps.services.CategoriaService;

import java.util.Optional;

/**
 * Tabla Soporte - Patrón A
 * Alto volumen, con paginado, sin update
 */
@Service
public class CategoriaServiceImpl implements CategoriaService {

    @Autowired
    private CategoriaRepository repository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CategoriaSpecification specification;

    @Override
    public Page<Categoria> findAll(
            Pageable pageable,
            String nombre,
            Boolean activo
    ) {
        Specification<CategoriaEntity> spec = specification.byNombre(nombre)
                .and(specification.byActivo(activo));

        Page<CategoriaEntity> entityPage = repository.findAll(spec, pageable);

        return entityPage.map(entity -> modelMapper.map(entity, Categoria.class));
    }

    @Override
    public Categoria findById(Long id) {
        return repository.findById(id)
                .map(entity -> modelMapper.map(entity, Categoria.class))
                .orElseThrow(() -> new EntryNotFoundException("No se encontro ninguna categoria con ese ID"));
    }

    @Override
    public Categoria save(String categoria) {
        Optional<CategoriaEntity> categoriaOpcional = repository.findByNombre(categoria);
        if(categoriaOpcional.isPresent()) {
            if(categoriaOpcional.get().getActivo()) {
                throw new ResourceAlreadyExistsException("Ya hay una categoria con ese nombre");
            }
            else {
                categoriaOpcional.get().setActivo(true);
                CategoriaEntity entity = repository.save(categoriaOpcional.get());
                return modelMapper.map(entity, Categoria.class);
            }
        }

        CategoriaEntity entity = new CategoriaEntity();
        entity.setNombre(categoria);
        entity.setActivo(true);
        entity = repository.save(entity);
        return modelMapper.map(entity, Categoria.class);
    }

    @Override
    public void delete(String nombre) {
        CategoriaEntity entity = repository.findByNombre(nombre)
                .orElseThrow(() -> new EntryNotFoundException("No se encontro una categoria con ese nombre"));

        if(!entity.getActivo()) {
            throw new ConflictiveStateException("La categoria ya esta desactivada");
        }

        entity.setActivo(false);
        repository.save(entity);
    }

    @Override
    public Optional<CategoriaEntity> findByNombre(String nombre) {
        return repository.findByNombre(nombre);
    }
}
