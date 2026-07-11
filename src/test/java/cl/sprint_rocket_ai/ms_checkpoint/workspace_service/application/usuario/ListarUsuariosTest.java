package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.usuario;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Usuario;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb.UsuarioMongoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListarUsuarios")
class ListarUsuariosTest {

    @Mock
    private UsuarioMongoRepository usuarioRepository;

    @InjectMocks
    private ListarUsuarios listarUsuarios;

    @Test
    @DisplayName("Debe retornar todos los usuarios del repositorio")
    void shouldReturnAllUsersFromRepository() {
        // Given
        List<Usuario> usuarios = List.of(new Usuario("u1", "a@mail.com"), new Usuario("u2", "b@mail.com"));
        when(usuarioRepository.findAll()).thenReturn(usuarios);

        // When
        List<Usuario> result = listarUsuarios.execute();

        // Then
        assertEquals(2, result.size());
        verify(usuarioRepository).findAll();
        verifyNoMoreInteractions(usuarioRepository);
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay usuarios")
    void shouldReturnEmptyListWhenNoUsers() {
        // Given
        when(usuarioRepository.findAll()).thenReturn(List.of());

        // When
        List<Usuario> result = listarUsuarios.execute();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
