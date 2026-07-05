package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.actividad.dtos;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.enums.Prioridad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.enums.TipoActividad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Actividad;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(description = "Solicitud para crear una nueva actividad")
public record CrearActividadRequest(

        @Schema(description = "Identificador del usuario/desarrollador", example = "dev-matias-001")
        @NotBlank(message = "El userId es obligatorio")
        String userId,

        @Schema(description = "Título de la actividad", example = "Implementar autenticación OAuth2")
        @NotBlank(message = "El título es obligatorio")
        @Size(min = 3, max = 200, message = "El título debe tener entre 3 y 200 caracteres")
        String titulo,

        @Schema(description = "Descripción detallada de la actividad", example = "Configurar el flujo OAuth2 con provider externo")
        String descripcion


) {
    public void applyTo(Actividad target) {
        target.setUserId(this.userId);
        target.setTitulo(this.titulo);
        target.setDescripcion(this.descripcion);
    }
}
