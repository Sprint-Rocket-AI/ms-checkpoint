package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.out.dto;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.enums.EstadoActividad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Actividad;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ActividadParaPopUp")
class ActividadParaPopUpTest {

    @Test
    @DisplayName("from debe mapear el titulo de la Actividad al DTO")
    void shouldMapTituloFromActividad() {
        // Given
        Actividad actividad = new Actividad();
        actividad.setId("a1");
        actividad.setTitulo("Implementar OAuth2");
        actividad.setEstado(EstadoActividad.PENDIENTE);
        actividad.setFechaCreacion(LocalDateTime.now());

        // When
        ActividadParaPopUp dto = ActividadParaPopUp.from(actividad);

        // Then
        assertEquals("Implementar OAuth2", dto.titulo());
    }
}
