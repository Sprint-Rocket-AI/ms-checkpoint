package cl.sprint_rocket_ai.ms_checkpoint.obtener_usuarios.infrastructure.out;

import cl.sprint_rocket_ai.ms_checkpoint.obtener_usuarios.domain.models.Usuario;
import cl.sprint_rocket_ai.ms_checkpoint.obtener_usuarios.domain.ports.out.UsuarioPersistencePortOut;
import cl.sprint_rocket_ai.ms_checkpoint.obtener_usuarios.infrastructure.persistences.mongodb.UsuarioMongoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UsuarioPersistenceAdapterOut implements UsuarioPersistencePortOut {

    private final UsuarioMongoRepository repository;

    public UsuarioPersistenceAdapterOut(UsuarioMongoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Usuario> findByUserId(String userId) {
        return repository.findByUserId(userId);
    }

    @Override
    public List<Usuario> findAll() {
        return repository.findAll();
    }

}
