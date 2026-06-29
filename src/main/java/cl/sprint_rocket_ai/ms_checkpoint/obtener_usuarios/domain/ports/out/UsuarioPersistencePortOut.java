package cl.sprint_rocket_ai.ms_checkpoint.obtener_usuarios.domain.ports.out;

import cl.sprint_rocket_ai.ms_checkpoint.obtener_usuarios.domain.models.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioPersistencePortOut {
    
    Optional<Usuario> findByUserId(String userId);

    List<Usuario> findAll();

}
