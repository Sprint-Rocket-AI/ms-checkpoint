package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.usuario;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Usuario;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb.UsuarioMongoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ObtenerUsuario")
class ObtenerUsuarioTest {

    @Mock
    private UsuarioMongoRepository usuarioRepository;

    @InjectMocks
    private ObtenerUsuario obtenerUsuario;

    @Test
    @DisplayName("Debe retornar el usuario cuando existe")
    void shouldReturnUserWhenExists() {
        // Given
        Usuario usuario = new Usuario("user-1", "test@mail.com");
        when(usuarioRepository.findByUserId("user-1")).thenReturn(Optional.of(usuario));

        // When
        Usuario result = obtenerUsuario.execute("user-1");

        // Then
        assertNotNull(result);
        assertEquals("user-1", result.getUserId());
        assertEquals("test@mail.com", result.getCorreo());
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando el usuario no existe")
    void shouldThrowIllegalArgumentExceptionWhenUserNotFound() {
        // Given
        when(usuarioRepository.findByUserId("no-existe")).thenReturn(Optional.empty());

        // When / Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> obtenerUsuario.execute("no-existe"));

        assertTrue(ex.getMessage().contains("no-existe"));
    }
}
