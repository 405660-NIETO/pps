package tup.pps.services;

import org.springframework.stereotype.Service;
import tup.pps.dtos.LoginResultDTO;
import tup.pps.dtos.usuarios.UsuarioLoginDTO;

@Service
public interface LoginService {
    LoginResultDTO login(UsuarioLoginDTO loginDTO);
}
