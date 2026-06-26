package cl.sprint_rocket_ai.ms_checkpoint.gestionar_usuarios.domain.ports.out;

import cl.sprint_rocket_ai.ms_checkpoint.gestionar_usuarios.domain.models.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioPersistencePortOut {
    
    Usuario save(Usuario usuario);
    
    Optional<Usuario> findByUserId(String userId);

    List<Usuario> findAll();

    void deleteByUserId(String userId);

    boolean existsByUserId(String userId);
}
