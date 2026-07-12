package cl.sprint_rocket_ai.ms_checkpoint.mcp_tools.application;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.recordatorio.CrearRecordatorio;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.recordatorio.ListarRecordatoriosByDesarrollador;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.recordatorio.dtos.CrearRecordatorioRequest;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.recordatorio.dtos.RecordatorioResponse;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class RecordatorioTools {

    private final CrearRecordatorio crearRecordatorio;
    private final ListarRecordatoriosByDesarrollador listarRecordatoriosByDesarrollador;

    public RecordatorioTools(CrearRecordatorio crearRecordatorio,
                             ListarRecordatoriosByDesarrollador listarRecordatoriosByDesarrollador) {
        this.crearRecordatorio = crearRecordatorio;
        this.listarRecordatoriosByDesarrollador = listarRecordatoriosByDesarrollador;
    }

    @McpTool(
            name = "crearRecordatorio",
            description = "Agenda un nuevo recordatorio o alerta programada para el desarrollador autenticado"
    )
    public RecordatorioResponse crearRecordatorio(
            @McpToolParam(description = "Título descriptivo del recordatorio (ej. Sincronización matutina)", required = true) String titulo,
            @McpToolParam(description = "Fecha y hora de expiración en formato ISO (YYYY-MM-DDTHH:mm:ss)", required = false) String fechaExpiracion,
            ToolContext ctx
    ) {
        String userId = getUserIdFromContext(ctx);

        LocalDateTime expiracion = null;
        if (fechaExpiracion != null && !fechaExpiracion.isBlank()) {
            try {
                expiracion = LocalDateTime.parse(fechaExpiracion);
            } catch (Exception e) {
                expiracion = LocalDateTime.now().plusDays(1);
            }
        }

        CrearRecordatorioRequest request = new CrearRecordatorioRequest(
                userId,
                titulo,
                expiracion
        );

        return crearRecordatorio.execute(request);
    }

    @McpTool(
            name = "listarRecordatoriosByDesarrollador",
            description = "Recupera todos los recordatorios activos y programados asociados al desarrollador autenticado"
    )
    public List<RecordatorioResponse> listarRecordatoriosByDesarrollador(
            ToolContext ctx
    ) {
        String userId = getUserIdFromContext(ctx);
        return listarRecordatoriosByDesarrollador.execute(userId);
    }

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