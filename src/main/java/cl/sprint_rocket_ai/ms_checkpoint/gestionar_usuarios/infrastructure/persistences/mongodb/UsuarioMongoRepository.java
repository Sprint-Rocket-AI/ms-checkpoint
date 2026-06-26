package cl.sprint_rocket_ai.ms_checkpoint.gestionar_usuarios.infrastructure.persistences.mongodb;

import cl.sprint_rocket_ai.ms_checkpoint.gestionar_usuarios.domain.models.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioMongoRepository extends MongoRepository<Usuario, String> {
    
    Optional<Usuario> findByUserId(String userId);

    void deleteByUserId(String userId);

    boolean existsByUserId(String userId);
}
