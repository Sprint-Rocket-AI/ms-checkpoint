package cl.sprint_rocket_ai.ms_checkpoint.schedulers.domain.ports.out;

import cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.domain.models.Actividad;

import java.util.List;

public interface IAEnginePortOut {

    String generateSummary(List<Actividad> actividades);

    String generatePopUp(List<Actividad> actividades);
}
