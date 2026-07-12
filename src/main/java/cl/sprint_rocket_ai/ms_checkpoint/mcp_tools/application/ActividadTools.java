package cl.sprint_rocket_ai.ms_checkpoint.mcp_tools.application;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.actividad.CrearActividad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.actividad.ListarActividadesByDesarrollador;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.actividad.ListarActividadesByFecha;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.actividad.dtos.ActividadResponse;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.actividad.dtos.CrearActividadRequest;
import org.springframework.ai.chat.model.ToolContext;
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
            ToolContext ctx
    ) {
        String userId = getUserIdFromContext(ctx);

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
            ToolContext ctx
    ) {
        String userId = getUserIdFromContext(ctx);
        return listarActividadesByDesarrollador.execute(userId);
    }

    @McpTool(
            name = "listarActividadByFecha",
            description = "Obtiene las actividades asignadas al desarrollador autenticado en una fecha específica"
    )
    public List<ActividadResponse> listarActividadesByFecha(
            @McpToolParam(description = "Fecha de consulta en formato ISO YYYY-MM-DD", required = true) LocalDate fecha,
            ToolContext ctx
    ) {
        String userId = getUserIdFromContext(ctx);
        return listarActividadesByFecha.execute(userId, fecha);
    }

    /**
     * Método helper privado para reutilizar la extracción segura del X-User-Id
     */
    private String getUserIdFromContext(ToolContext ctx) {
        if (ctx == null || ctx.getContext() == null) {
            throw new IllegalArgumentException("El ToolContext o el contexto interno no pueden ser nulos.");
        }

        String userId = (String) ctx.getContext().get("X-User-Id");

        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("No se encontró el identificador del usuario (X-User-Id) en el contexto de transporte MCP.");
        }

        return userId;
    }
}