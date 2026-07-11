package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.jobs.scheduler;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.NotificarActividadesPendientes;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.jobs.NotificarActividadesPendientesScheduler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificarActividadesPendientesScheduler")
class NotificarActividadesPendientesSchedulerTest {

    @Mock
    private NotificarActividadesPendientes notificarActividadesPendientes;

    @InjectMocks
    private NotificarActividadesPendientesScheduler scheduler;

    @Test
    @DisplayName("Debe delegar la ejecución al use case NotificarActividadesPendientes")
    void shouldDelegateToUseCase() {
        // When
        scheduler.execute();

        // Then
        verify(notificarActividadesPendientes).execute();
        verifyNoMoreInteractions(notificarActividadesPendientes);
    }

    @Test
    @DisplayName("No debe propagar excepción cuando el use case lanza una")
    void shouldNotPropagateExceptionFromUseCase() {
        // Given
        doThrow(new RuntimeException("Error inesperado")).when(notificarActividadesPendientes).execute();

        // When / Then
        assertDoesNotThrow(() -> scheduler.execute());
    }
}
