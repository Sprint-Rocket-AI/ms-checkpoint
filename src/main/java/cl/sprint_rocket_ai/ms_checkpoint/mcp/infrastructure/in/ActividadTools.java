package cl.sprint_rocket_ai.ms_checkpoint.mcp.infrastructure.in;

import cl.sprint_rocket_ai.ms_checkpoint.commons.domain.enums.EstadoActividad;
import cl.sprint_rocket_ai.ms_checkpoint.commons.domain.enums.Prioridad;
import cl.sprint_rocket_ai.ms_checkpoint.commons.domain.enums.TipoActividad;
import cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.application.CrearActividad;
import cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.application.ListarActividadesByDesarrollador;
import cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.application.ListarActividadesByFecha;
import cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.infrastructure.in.dtos.ActividadResponse;
import cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.infrastructure.in.dtos.CrearActividadRequest;
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
            description = "Crear una nueva actividad para un desarrollador"
    )
    public String crearActividad(
            String userId,
            String titulo,
            String descripcion,
            TipoActividad tipo,
            Prioridad prioridad,
            String fechaCreacion // <-- Cambiado de LocalDate a String
    ) {
        LocalDate fecha;
        try {
            fecha = (fechaCreacion != null && !fechaCreacion.isBlank()) ? LocalDate.parse(fechaCreacion) : LocalDate.now();
        } catch (Exception e) {
            fecha = LocalDate.now();
        }

        CrearActividadRequest request = new CrearActividadRequest(
                userId, titulo, descripcion, tipo, prioridad, fecha
        );
        crearActividad.execute(request);
        return "Actividad creada con éxito";
    }

    @McpTool(
            name = "listarActividadByDesarrollador",
            description = "Lista todas las actividades de un desarrollador usando su userId"
    )
    public List<ActividadResponse> listarActividadesByDesarrollador(
            @McpToolParam(description = "ID del desarrollador para filtrar los checkpoints", required = true) String userId
    ) {
        return listarActividadesByDesarrollador.execute(userId, null);
    }

    @McpTool(
            name = "listarActividadByFecha",
            description = "Obtiene las actividades asignadas a un desarrollador en una fecha específica"
    )
    public List<ActividadResponse> listarActividadesByFecha(
            @McpToolParam(description = "ID del desarrollador", required = true) String userId,
            @McpToolParam(description = "Fecha de consulta en formato ISO YYYY-MM-DD", required = true) LocalDate fecha
    ) {
        return listarActividadesByFecha.execute(userId, fecha);
    }
}