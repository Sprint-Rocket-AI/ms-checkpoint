package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.usuario;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Usuario;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb.UsuarioMongoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarUsuarios {

    private final UsuarioMongoRepository usuarioRepository;

    public ListarUsuarios(UsuarioMongoRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> execute() {
        return usuarioRepository.findAll();
    }
}
