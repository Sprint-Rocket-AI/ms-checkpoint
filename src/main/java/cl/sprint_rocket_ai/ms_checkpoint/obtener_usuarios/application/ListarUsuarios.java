package cl.sprint_rocket_ai.ms_checkpoint.obtener_usuarios.application;

import cl.sprint_rocket_ai.ms_checkpoint.obtener_usuarios.domain.models.Usuario;
import cl.sprint_rocket_ai.ms_checkpoint.obtener_usuarios.domain.ports.out.UsuarioPersistencePortOut;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarUsuarios {

    private final UsuarioPersistencePortOut usuarioPersistencePortOut;

    public ListarUsuarios(UsuarioPersistencePortOut usuarioPersistencePortOut) {
        this.usuarioPersistencePortOut = usuarioPersistencePortOut;
    }

    public List<Usuario> execute() {
        return usuarioPersistencePortOut.findAll();
    }
}
