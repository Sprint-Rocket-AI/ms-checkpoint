package cl.sprint_rocket_ai.ms_checkpoint.schedulers.domain.ports.out;

import cl.sprint_rocket_ai.ms_checkpoint.gestionar_actividades.domain.models.Actividad;
import cl.sprint_rocket_ai.ms_checkpoint.schedulers.domain.models.ResumenDiarioResult;

import java.time.LocalDate;
import java.util.List;

public interface IAEnginePortOut {

    String generateSummary(List<Actividad> actividades);

    String generatePopUp(List<Actividad> actividades);

    ResumenDiarioResult generateDailySummary(List<Actividad> actividades, String userId, LocalDate fecha);
}
