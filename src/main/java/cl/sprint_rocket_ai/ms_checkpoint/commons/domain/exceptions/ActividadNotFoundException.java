package cl.sprint_rocket_ai.ms_checkpoint.commons.domain.exceptions;

public class ActividadNotFoundException extends EntityNotFoundException {

    public ActividadNotFoundException(String identificador) {
        super("Actividad", identificador);
    }
}
