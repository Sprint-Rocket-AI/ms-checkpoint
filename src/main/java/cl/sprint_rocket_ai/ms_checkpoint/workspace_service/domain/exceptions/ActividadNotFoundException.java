package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.exceptions;

public class ActividadNotFoundException extends EntityNotFoundException {

    public ActividadNotFoundException(String identificador) {
        super("Actividad", identificador);
    }
}
