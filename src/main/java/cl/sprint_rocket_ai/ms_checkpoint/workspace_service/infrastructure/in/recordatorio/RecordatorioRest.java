package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.recordatorio;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.recordatorio.dtos.ActualizarRecordatorioRequest;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.recordatorio.dtos.ActualizarRecordatorioFechaFinalizacionRequest;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.recordatorio.dtos.ActualizarRecordatorioTituloRequest;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.recordatorio.dtos.CrearRecordatorioRequest;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.recordatorio.dtos.RecordatorioResponse;
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

@Tag(name = "Recordatorios", description = "Operaciones CRUD para gestionar recordatorios programados")
public interface RecordatorioRest {

    @Operation(summary = "Crear recordatorio", description = "Registra un nuevo recordatorio programado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Recordatorio creado exitosamente",
                    content = @Content(schema = @Schema(implementation = RecordatorioResponse.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida",
                    content = @Content)
    })
    ResponseEntity<RecordatorioResponse> create(
            @RequestBody @Valid CrearRecordatorioRequest request);

    @Operation(summary = "Obtener recordatorio por ID", description = "Obtiene los detalles de un recordatorio por su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recordatorio encontrado",
                    content = @Content(schema = @Schema(implementation = RecordatorioResponse.class))),
            @ApiResponse(responseCode = "404", description = "Recordatorio no encontrado",
                    content = @Content)
    })
    ResponseEntity<RecordatorioResponse> findById(
            @Parameter(description = "ID del recordatorio", required = true) @PathVariable String id);

    @Operation(summary = "Listar recordatorios por desarrollador", description = "Lista todos los recordatorios de un desarrollador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de recordatorios",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RecordatorioResponse.class))))
    })
    ResponseEntity<List<RecordatorioResponse>> findByDesarrollador(
            @Parameter(description = "ID del desarrollador", required = true) @PathVariable String userId);

    @Operation(summary = "Actualizar recordatorio", description = "Actualiza un recordatorio existente por su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recordatorio actualizado",
                    content = @Content(schema = @Schema(implementation = RecordatorioResponse.class))),
            @ApiResponse(responseCode = "404", description = "Recordatorio no encontrado",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida",
                    content = @Content)
    })
    ResponseEntity<RecordatorioResponse> update(
            @Parameter(description = "ID del recordatorio", required = true) @PathVariable String id,
            @RequestBody @Valid ActualizarRecordatorioRequest request);

    @Operation(summary = "Actualizar título de recordatorio", description = "Actualiza únicamente el título de un recordatorio existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Título actualizado",
                    content = @Content(schema = @Schema(implementation = RecordatorioResponse.class))),
            @ApiResponse(responseCode = "404", description = "Recordatorio no encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content)
    })
    ResponseEntity<RecordatorioResponse> updateTitulo(
            @Parameter(description = "ID del recordatorio", required = true) @PathVariable String id,
            @RequestBody @Valid ActualizarRecordatorioTituloRequest request);

    @Operation(summary = "Actualizar fecha de finalización de recordatorio", description = "Actualiza únicamente la fecha de finalización/expiración de un recordatorio existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fecha actualizada",
                    content = @Content(schema = @Schema(implementation = RecordatorioResponse.class))),
            @ApiResponse(responseCode = "404", description = "Recordatorio no encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content)
    })
    ResponseEntity<RecordatorioResponse> updateFechaFinalizacion(
            @Parameter(description = "ID del recordatorio", required = true) @PathVariable String id,
            @RequestBody @Valid ActualizarRecordatorioFechaFinalizacionRequest request);

    @Operation(summary = "Eliminar recordatorio", description = "Elimina un recordatorio por su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Recordatorio eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Recordatorio no encontrado",
                    content = @Content)
    })
    ResponseEntity<Void> delete(
            @Parameter(description = "ID del recordatorio", required = true) @PathVariable String id);

    @Operation(summary = "Completar recordatorio",
            description = "Marca el recordatorio como completado (activo=false). Invocado desde el popup WebSocket.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recordatorio completado",
                    content = @Content(schema = @Schema(implementation = RecordatorioResponse.class))),
            @ApiResponse(responseCode = "404", description = "Recordatorio no encontrado",
                    content = @Content)
    })
    ResponseEntity<RecordatorioResponse> completar(
            @Parameter(description = "ID del recordatorio", required = true) @PathVariable String id);

    @Operation(summary = "Posponer recordatorio",
            description = "Pospone el recordatorio N minutos desde ahora. Invocado desde el popup WebSocket.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recordatorio pospuesto",
                    content = @Content(schema = @Schema(implementation = RecordatorioResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parámetro minutos inválido",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Recordatorio no encontrado",
                    content = @Content)
    })
    ResponseEntity<RecordatorioResponse> posponer(
            @Parameter(description = "ID del recordatorio", required = true) @PathVariable String id,
            @Parameter(description = "Minutos a posponer", example = "10") @RequestParam(defaultValue = "10") int minutos);
}
