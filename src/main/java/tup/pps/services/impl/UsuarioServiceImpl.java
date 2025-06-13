package tup.pps.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tup.pps.dtos.usuarios.UsuarioRegistroDTO;
import tup.pps.dtos.usuarios.UsuarioUpdateDTO;
import tup.pps.entities.RolEntity;
import tup.pps.entities.UsuarioEntity;
import tup.pps.exceptions.EntryNotFoundException;
import tup.pps.exceptions.ResourceAlreadyExistsException;
import tup.pps.models.Usuario;
import tup.pps.repositories.UsuarioRepository;
import tup.pps.services.RolService;
import tup.pps.services.UsuarioService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private RolService rolService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public Optional<UsuarioEntity> findEntityById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Usuario save(UsuarioRegistroDTO dto) {
        // 1. RESOLVER rol (validar que existe)
        RolEntity rol = resolverRol(dto.getRolId());

        // 2. VERIFICAR email existente
        Optional<UsuarioEntity> usuarioExistente = repository.findByEmail(dto.getEmail());

        if (usuarioExistente.isPresent()) {
            if (usuarioExistente.get().getActivo()) {
                throw new ResourceAlreadyExistsException("Ya existe un usuario con ese email");
            } else {
                // REACTIVAR usuario inactivo
                return reactivarUsuario(usuarioExistente.get(), dto, rol);
            }
        }

        // 3. CREAR nuevo usuario
        return crearUsuario(dto, rol);
    }

    @Override
    public Usuario update(Long id, UsuarioUpdateDTO dto) {
        return null;
    }

    @Override
    public Usuario findById(Long id) {
        return null;
    }

    @Override
    public Usuario findByEmail(String email) {
        return null;
    }

    @Override
    public List<Usuario> findAll(String nombre, Boolean activo) {
        return List.of();
    }

    @Override
    public void delete(Long id) {

    }

    private RolEntity resolverRol(Long rolId) {
        return rolService.findEntityById(rolId)
                .orElseThrow(() -> new EntryNotFoundException("Rol no encontrado"));
    }

    private Usuario reactivarUsuario(UsuarioEntity usuario, UsuarioRegistroDTO dto, RolEntity rol) {
        usuario.setActivo(true);
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setPassword(dto.getPassword()); // Plain text por ahora
        usuario.setRol(rol);
        usuario.setFechaCreacion(usuario.getFechaCreacion());

        UsuarioEntity saved = repository.save(usuario);
        return modelMapper.map(saved, Usuario.class);
    }

    private Usuario crearUsuario(UsuarioRegistroDTO dto, RolEntity rol) {
        UsuarioEntity entity = new UsuarioEntity();
        entity.setEmail(dto.getEmail());
        entity.setPassword(dto.getPassword()); // Plain text
        entity.setNombre(dto.getNombre());
        entity.setApellido(dto.getApellido());
        entity.setRol(rol);
        entity.setFechaCreacion(LocalDateTime.now());
        entity.setActivo(true);

        UsuarioEntity saved = repository.save(entity);
        return modelMapper.map(saved, Usuario.class);
    }
}