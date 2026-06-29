package cl.sprint_rocket_ai.ms_checkpoint.obtener_usuarios.application;

import cl.sprint_rocket_ai.ms_checkpoint.obtener_usuarios.domain.models.Usuario;
import cl.sprint_rocket_ai.ms_checkpoint.obtener_usuarios.domain.ports.out.UsuarioPersistencePortOut;
import org.springframework.stereotype.Service;

@Service
public class ObtenerUsuario {

    private final UsuarioPersistencePortOut usuarioPersistencePortOut;

    public ObtenerUsuario(UsuarioPersistencePortOut usuarioPersistencePortOut) {
        this.usuarioPersistencePortOut = usuarioPersistencePortOut;
    }

    public Usuario execute(String userId) {
        return usuarioPersistencePortOut.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con userId: " + userId));
    }
}
