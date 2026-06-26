package cl.sprint_rocket_ai.ms_checkpoint.gestionar_usuarios.application;

import cl.sprint_rocket_ai.ms_checkpoint.gestionar_usuarios.domain.ports.out.UsuarioPersistencePortOut;
import org.springframework.stereotype.Service;

@Service
public class EliminarUsuario {

    private final UsuarioPersistencePortOut usuarioPersistencePortOut;

    public EliminarUsuario(UsuarioPersistencePortOut usuarioPersistencePortOut) {
        this.usuarioPersistencePortOut = usuarioPersistencePortOut;
    }

    public void execute(String userId) {
        if (!usuarioPersistencePortOut.existsByUserId(userId)) {
            throw new IllegalArgumentException("Usuario no encontrado con userId: " + userId);
        }
        usuarioPersistencePortOut.deleteByUserId(userId);
    }
}
