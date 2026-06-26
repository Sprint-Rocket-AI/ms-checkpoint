package cl.sprint_rocket_ai.ms_checkpoint.gestionar_usuarios.application;

import cl.sprint_rocket_ai.ms_checkpoint.gestionar_usuarios.domain.models.Usuario;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_usuarios.domain.ports.out.UsuarioPersistencePortOut;
import org.springframework.stereotype.Service;

@Service
public class ActualizarUsuario {

    private final UsuarioPersistencePortOut usuarioPersistencePortOut;

    public ActualizarUsuario(UsuarioPersistencePortOut usuarioPersistencePortOut) {
        this.usuarioPersistencePortOut = usuarioPersistencePortOut;
    }

    public Usuario execute(String userId, String nuevoCorreo) {
        Usuario usuario = usuarioPersistencePortOut.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con userId: " + userId));
        
        usuario.setCorreo(nuevoCorreo);
        return usuarioPersistencePortOut.save(usuario);
    }
}
