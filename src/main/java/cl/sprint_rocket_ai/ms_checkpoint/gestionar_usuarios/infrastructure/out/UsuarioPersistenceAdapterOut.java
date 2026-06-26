package cl.sprint_rocket_ai.ms_checkpoint.gestionar_usuarios.infrastructure.out;

import cl.sprint_rocket_ai.ms_checkpoint.gestionar_usuarios.domain.models.Usuario;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_usuarios.domain.ports.out.UsuarioPersistencePortOut;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_usuarios.infrastructure.persistences.mongodb.UsuarioMongoRepository;
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
    public Usuario save(Usuario usuario) {
        return repository.save(usuario);
    }

    @Override
    public Optional<Usuario> findByUserId(String userId) {
        return repository.findByUserId(userId);
    }

    @Override
    public List<Usuario> findAll() {
        return repository.findAll();
    }

    @Override
    public void deleteByUserId(String userId) {
        repository.deleteByUserId(userId);
    }

    @Override
    public boolean existsByUserId(String userId) {
        return repository.existsByUserId(userId);
    }
}
