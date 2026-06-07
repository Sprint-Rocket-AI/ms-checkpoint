package cl.sprint_rocket_ai.ms_checkpoint.schedulers.domain.ports.out;

import cl.sprint_rocket_ai.ms_checkpoint.schedulers.domain.models.SugerenciaActividad;

import java.util.List;

public interface NotificationPortOut {

    void notifyDeveloper(String userId, String contenido);

    void notifyLider(String liderTecnicoId, String contenido);

    /**
     * Notifica al desarrollador con el resumen ejecutivo del día y las sugerencias de continuidad.
     * El adaptador es responsable de formatear el contenido como correo electrónico.
     *
     * @param userId      identificador del desarrollador
     * @param resumen     texto del resumen ejecutivo generado por la IA
     * @param sugerencias lista de actividades sugeridas para el día siguiente
     */
    void notifyDeveloperWithSummary(String userId, String resumen, List<SugerenciaActividad> sugerencias);
}
