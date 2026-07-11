package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.out.dto;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.SugerenciaActividad;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SugerenciaActividadDto")
class SugerenciaActividadDtoTest {

    @Test
    @DisplayName("toDomain debe mapear todos los campos al value object de dominio")
    void shouldMapAllFieldsToDomainObject() {
        // Given
        SugerenciaActividadDto dto = new SugerenciaActividadDto(
                "Revisar PR", "Pendiente de revisión", "ALTA", "Bloquea al equipo");

        // When
        SugerenciaActividad domain = dto.toDomain();

        // Then
        assertEquals("Revisar PR", domain.titulo());
        assertEquals("Pendiente de revisión", domain.descripcion());
        assertEquals("ALTA", domain.prioridad());
        assertEquals("Bloquea al equipo", domain.razon());
    }
}
