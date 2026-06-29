package cl.sprint_rocket_ai.ms_checkpoint.mcp_tools.application;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.recordatorio.CrearRecordatorio;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.recordatorio.ListarRecordatoriosByDesarrollador;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.recordatorio.dtos.CrearRecordatorioRequest;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.recordatorio.dtos.RecordatorioResponse;
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
            description = "Agenda un nuevo recordatorio o alerta programada para un desarrollador específico"
    )
    public RecordatorioResponse crearRecordatorio(
            @McpToolParam(description = "ID único del desarrollador o usuario dueño del recordatorio", required = true) String userId,
            @McpToolParam(description = "Título descriptivo del recordatorio (ej. Sincronización matutina)", required = true) String titulo,
            @McpToolParam(description = "Fecha y hora de expiración en formato ISO (YYYY-MM-DDTHH:mm:ss)", required = false) String fechaExpiracion
    ) {
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
            description = "Recupera todos los recordatorios activos y programados asociados al userId de un desarrollador"
    )
    public List<RecordatorioResponse> listarRecordatoriosByDesarrollador(
            @McpToolParam(description = "ID único del desarrollador para filtrar los recordatorios", required = true) String userId
    ) {
        return listarRecordatoriosByDesarrollador.execute(userId);
    }
}