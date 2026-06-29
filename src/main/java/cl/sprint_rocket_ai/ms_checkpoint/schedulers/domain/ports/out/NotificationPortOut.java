package cl.sprint_rocket_ai.ms_checkpoint.schedulers.domain.ports.out;

import cl.sprint_rocket_ai.ms_checkpoint.schedulers.domain.models.SugerenciaActividad;

import java.util.List;

public interface NotificationPortOut {

    void notifyDeveloper(String userId, String contenido);

    void notifyLider(String liderTecnicoId, String contenido);

    void notifyDeveloperWithSummary(String userId, String correo, String resumen, List<SugerenciaActividad> sugerencias);
}
