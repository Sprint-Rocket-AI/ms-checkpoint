package cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.infrastructure.in;

import cl.sprint_rocket_ai.ms_checkpoint.commons.domain.enums.EstadoActividad;
import cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.infrastructure.in.dtos.ActividadResponse;
import cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.infrastructure.in.dtos.ActualizarActividadRequest;
import cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.infrastructure.in.dtos.CrearActividadRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Actividades", description = "Operaciones CRUD para gestionar actividades de desarrollo")
public interface ActividadRest {

    @Operation(summary = "Crear actividad", description = "Registra una nueva actividad de desarrollo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Actividad creada exitosamente",
                    content = @Content(schema = @Schema(implementation = ActividadResponse.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida",
                    content = @Content)
    })
    ResponseEntity<ActividadResponse> create(
            @RequestBody @Valid CrearActividadRequest request);

    @Operation(summary = "Obtener actividad por ID", description = "Obtiene los detalles de una actividad por su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Actividad encontrada",
                    content = @Content(schema = @Schema(implementation = ActividadResponse.class))),
            @ApiResponse(responseCode = "404", description = "Actividad no encontrada",
                    content = @Content)
    })
    ResponseEntity<ActividadResponse> findById(
            @Parameter(description = "ID de la actividad", required = true) @PathVariable String id);

    @Operation(summary = "Listar actividades por desarrollador", description = "Lista todas las actividades de un desarrollador con filtro opcional por estado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de actividades",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ActividadResponse.class))))
    })
    ResponseEntity<List<ActividadResponse>> findByDesarrollador(
            @Parameter(description = "ID del desarrollador", required = true) @PathVariable String userId,
            @Parameter(description = "Filtrar por estado de la actividad") @RequestParam(required = false) EstadoActividad estado);

    @Operation(summary = "Actualizar actividad", description = "Actualiza una actividad existente por su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Actividad actualizada",
                    content = @Content(schema = @Schema(implementation = ActividadResponse.class))),
            @ApiResponse(responseCode = "404", description = "Actividad no encontrada",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida",
                    content = @Content)
    })
    ResponseEntity<ActividadResponse> update(
            @Parameter(description = "ID de la actividad", required = true) @PathVariable String id,
            @RequestBody @Valid ActualizarActividadRequest request);

    @Operation(summary = "Eliminar actividad", description = "Elimina una actividad por su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Actividad eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Actividad no encontrada",
                    content = @Content)
    })
    ResponseEntity<Void> delete(
            @Parameter(description = "ID de la actividad", required = true) @PathVariable String id);
}
