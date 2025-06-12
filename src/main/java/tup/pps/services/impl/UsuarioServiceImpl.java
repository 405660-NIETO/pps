package tup.pps.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tup.pps.entities.UsuarioEntity;
import tup.pps.repositories.UsuarioRepository;
import tup.pps.services.UsuarioService;

import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Override
    public Optional<UsuarioEntity> findEntityById(Long id) {
        return repository.findById(id);
    }
}