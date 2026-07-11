package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.out.dto;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.enums.EstadoActividad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Actividad;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ActividadResumenDto")
class ActividadResumenDtoTest {

    @Test
    @DisplayName("from debe mapear titulo, descripcion y estado de la Actividad")
    void shouldMapTituloDescripcionAndEstado() {
        // Given
        Actividad actividad = new Actividad();
        actividad.setTitulo("Deploy a producción");
        actividad.setDescripcion("Subir release 2.0");
        actividad.setEstado(EstadoActividad.EN_PROCESO);
        actividad.setFechaCreacion(LocalDateTime.now());

        // When
        ActividadResumenDto dto = ActividadResumenDto.from(actividad);

        // Then
        assertEquals("Deploy a producción", dto.titulo());
        assertEquals("Subir release 2.0", dto.descripcion());
        assertEquals(EstadoActividad.EN_PROCESO, dto.estado());
    }
}
