package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioMongoRepository extends MongoRepository<Usuario, String> {
    
    Optional<Usuario> findByUserId(String userId);

}
