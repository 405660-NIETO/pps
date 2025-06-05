package tup.pps.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tup.pps.entities.MarcaEntity;
import tup.pps.exceptions.ConflictiveStateException;
import tup.pps.exceptions.EntryNotFoundException;
import tup.pps.exceptions.ResourceAlreadyExistsException;
import tup.pps.models.Marca;
import tup.pps.repositories.MarcaRepository;
import tup.pps.repositories.specs.MarcaSpecification;
import tup.pps.services.MarcaService;

import java.util.Optional;

@Service
public class MarcaServiceImpl implements MarcaService {

    @Autowired
    private MarcaRepository repository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private MarcaSpecification specification;

    @Override
    public Page<Marca> findAll(
            Pageable pageable,
            String nombre,
            Boolean activo
    ) {
        Specification<MarcaEntity> spec = specification.byNombre(nombre)
                .and(specification.byActivo(activo));

        Page<MarcaEntity> entityPage = repository.findAll(spec, pageable);

        return entityPage.map(entity -> modelMapper.map(entity, Marca.class));
    }

    @Override
    public Marca findById(Long id) {
        return repository.findById(id)
                .map(entity -> modelMapper.map(entity, Marca.class))
                .orElseThrow(() -> new EntryNotFoundException("No se encontro ninguna marca con ese ID"));
    }

    @Override
    public Marca save(Marca marca) {
        Optional<MarcaEntity> marcaOpcional = repository.findByNombre(marca.getNombre());
        if(marcaOpcional.isPresent()) {
            if(marcaOpcional.get().getActivo()) {
                throw new ResourceAlreadyExistsException("Ya hay una marca con ese nombre");
            }
            else {
                marcaOpcional.get().setActivo(true);
                MarcaEntity entity = repository.save(marcaOpcional.get());
                return modelMapper.map(entity, Marca.class);
            }
        }

        MarcaEntity entity = new MarcaEntity();
        entity.setNombre(marca.getNombre());
        entity.setActivo(true);
        entity = repository.save(entity);
        return modelMapper.map(entity, Marca.class);
    }

    @Override
    public void delete(String nombre) {
        MarcaEntity entity = repository.findByNombre(nombre)
                .orElseThrow(() -> new EntryNotFoundException("No se encontro una marca con ese nombre"));

        if(!entity.getActivo()) {
            throw new ConflictiveStateException("La marca ya esta desactivada");
        }

        entity.setActivo(false);
        repository.save(entity);
    }
}