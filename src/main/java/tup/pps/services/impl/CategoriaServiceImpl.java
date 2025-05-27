package tup.pps.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tup.pps.entities.CategoriaEntity;
import tup.pps.models.Categoria;
import tup.pps.repositories.CategoriaRepository;
import tup.pps.services.CategoriaService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    @Autowired
    private CategoriaRepository repository;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public List<Categoria> findAll() {
        return repository.findAll().stream()
                .map(entity -> modelMapper.map(entity, Categoria.class))
                .toList();
    }

    @Override
    public Categoria findById(Long id) {
        return repository.findById(id)
                .map(entity -> modelMapper.map(entity, Categoria.class))
                .orElseThrow(() -> new NoSuchElementException("Categoria Not Found"));
    }

    @Override
    public Categoria save(Categoria categoria) {
        Optional<CategoriaEntity> categoriaOpcional = repository.findByNombre(categoria.getNombre());
        if(categoriaOpcional.isPresent()) {
            if(categoriaOpcional.get().getActivo()) {
                throw new IllegalStateException("Categoria Activo");
            }
            else {
                categoriaOpcional.get().setActivo(true);
                CategoriaEntity entity = repository.save(categoriaOpcional.get());
                return modelMapper.map(entity, Categoria.class);
            }
        }

        CategoriaEntity entity = new CategoriaEntity();
        entity.setNombre(categoria.getNombre());
        entity.setActivo(true);
        entity = repository.save(entity);
        return modelMapper.map(entity, Categoria.class);
    }

    @Override
    public void delete(String nombre) {
        CategoriaEntity entity = repository.findByNombre(nombre)
                .orElseThrow(() -> new NoSuchElementException("Categoria Not Found"));

        if(!entity.getActivo()) {
            throw new IllegalStateException("La categoria ya esta desactivada");
        }

        entity.setActivo(false);
        repository.save(entity);
    }

}
