package cl.sprint_rocket_ai.ms_checkpoint.mcp_tools.application;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.actividad.CrearActividad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.actividad.ListarActividadesByDesarrollador;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.actividad.ListarActividadesByFecha;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.actividad.dtos.ActividadResponse;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.actividad.dtos.CrearActividadRequest;
import org.springframework.ai.mcp.annotation.McpMeta;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam; // CAMBIO: Importación mandatoria en M7
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class ActividadTools {

    private final CrearActividad crearActividad;
    private final ListarActividadesByDesarrollador listarActividadesByDesarrollador;
    private final ListarActividadesByFecha listarActividadesByFecha;

    public ActividadTools(CrearActividad crearActividad,
                          ListarActividadesByDesarrollador listarActividadesByDesarrollador,
                          ListarActividadesByFecha listarActividadesByFecha) {
        this.crearActividad = crearActividad;
        this.listarActividadesByDesarrollador = listarActividadesByDesarrollador;
        this.listarActividadesByFecha = listarActividadesByFecha;
    }

    @McpTool(
            name = "crearActividad",
            description = "Crear una nueva actividad para el desarrollador autenticado"
    )
    public String crearActividad(
            String titulo,
            String descripcion,
            McpMeta meta
    ) {
        String userId = getUserIdFromMeta(meta);

        CrearActividadRequest request = new CrearActividadRequest(
                userId, titulo, descripcion
        );
        crearActividad.execute(request);
        return "Actividad creada con éxito";
    }

    @McpTool(
            name = "listarActividadByDesarrollador",
            description = "Lista todas las actividades del desarrollador autenticado"
    )
    public List<ActividadResponse> listarActividadesByDesarrollador(
            McpMeta meta
    ) {
        String userId = getUserIdFromMeta(meta);
        return listarActividadesByDesarrollador.execute(userId);
    }

    @McpTool(
            name = "listarActividadByFecha",
            description = "Obtiene las actividades asignadas al desarrollador autenticado en una fecha específica"
    )
    public List<ActividadResponse> listarActividadesByFecha(
            @McpToolParam(description = "Fecha de consulta en formato ISO YYYY-MM-DD", required = true) LocalDate fecha,
            McpMeta meta
    ) {
        String userId = getUserIdFromMeta(meta);
        return listarActividadesByFecha.execute(userId, fecha);
    }

    /**
     * Método helper privado para reutilizar la extracción segura del X-User-Id
     */
    private String getUserIdFromMeta(McpMeta meta) {
        if (meta == null) {
            throw new IllegalArgumentException("El McpMeta no puede ser nulo.");
        }

        String userId = (String) meta.get("userId");

        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("No se encontró el identificador del usuario (userId) en el _meta de la request MCP.");
        }

        return userId;
    }
}