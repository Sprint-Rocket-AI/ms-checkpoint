package cl.sprint_rocket_ai.ms_checkpoint.gestionar_usuarios.application;

import cl.sprint_rocket_ai.ms_checkpoint.gestionar_usuarios.domain.models.Usuario;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_usuarios.domain.ports.out.UsuarioPersistencePortOut;
import org.springframework.stereotype.Service;

@Service
public class CrearUsuario {

    private final UsuarioPersistencePortOut usuarioPersistencePortOut;

    public CrearUsuario(UsuarioPersistencePortOut usuarioPersistencePortOut) {
        this.usuarioPersistencePortOut = usuarioPersistencePortOut;
    }

    public Usuario execute(String userId, String correo) {
        if (usuarioPersistencePortOut.existsByUserId(userId)) {
            throw new IllegalArgumentException("El usuario con userId " + userId + " ya existe.");
        }
        Usuario usuario = new Usuario(userId, correo);
        return usuarioPersistencePortOut.save(usuario);
    }
}
