package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.usuario;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Usuario;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb.UsuarioMongoRepository;
import org.springframework.stereotype.Service;

@Service
public class ObtenerUsuario {

    private final UsuarioMongoRepository usuarioRepository;

    public ObtenerUsuario(UsuarioMongoRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario execute(String userId) {
        return usuarioRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con userId: " + userId));
    }
}
