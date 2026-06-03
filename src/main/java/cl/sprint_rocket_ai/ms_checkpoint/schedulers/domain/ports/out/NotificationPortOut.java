package cl.sprint_rocket_ai.ms_checkpoint.schedulers.domain.ports.out;

public interface NotificationPortOut {

    void notifyDeveloper(String userId, String contenido);

    void notifyLider(String liderTecnicoId, String contenido);
}
