package cl.sprint_rocket_ai.ms_checkpoint.commons.domain.exceptions;

public class RecordatorioNotFoundException extends EntityNotFoundException {

    public RecordatorioNotFoundException(String identificador) {
        super("Recordatorio", identificador);
    }
}
