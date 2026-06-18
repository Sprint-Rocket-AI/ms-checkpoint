package cl.sprint_rocket_ai.ms_checkpoint.mcp.infrastructure.in;

import cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.application.CrearRecordatorio;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.application.ListarRecordatoriosByDesarrollador;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.infrastructure.in.dtos.CrearRecordatorioRequest;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.infrastructure.in.dtos.RecordatorioResponse;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.stereotype.Component;

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
            description = "Crear un nuevo recordatorio para un desarrollador"
    )
    public RecordatorioResponse crearRecordatorio(
            CrearRecordatorioRequest request) {

        return crearRecordatorio.execute(request);
    }

    @McpTool(
            name = "listarRecordatoriosByDesarrollador",
            description = "Crear un nuevo recordatorio para un desarrollador"
    )
    public List<RecordatorioResponse> listarRecordatoriosByDesarrollador(
            String userId) {

        return listarRecordatoriosByDesarrollador.execute(userId);
    }



}
