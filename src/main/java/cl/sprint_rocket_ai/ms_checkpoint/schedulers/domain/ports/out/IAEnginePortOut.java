package cl.sprint_rocket_ai.ms_checkpoint.schedulers.domain.ports.out;

import cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.domain.models.Actividad;
import cl.sprint_rocket_ai.ms_checkpoint.schedulers.domain.models.ResumenDiarioResult;

import java.time.LocalDate;
import java.util.List;

public interface IAEnginePortOut {

    String generateSummary(List<Actividad> actividades);

    String generatePopUp(List<Actividad> actividades);

    /**
     * Genera el resumen diario ejecutivo y sugerencias de continuidad para un desarrollador.
     *
     * @param actividades todas las actividades del día (pendientes + completadas)
     * @param userId      identificador del desarrollador
     * @param fecha       fecha del día analizado
     * @return resultado con resumen ejecutivo y lista de sugerencias
     */
    ResumenDiarioResult generateDailySummary(List<Actividad> actividades, String userId, LocalDate fecha);
}
