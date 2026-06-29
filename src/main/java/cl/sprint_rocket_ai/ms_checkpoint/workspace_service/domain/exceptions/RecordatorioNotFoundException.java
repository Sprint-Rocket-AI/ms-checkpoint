package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.exceptions;

public class RecordatorioNotFoundException extends EntityNotFoundException {

    public RecordatorioNotFoundException(String identificador) {
        super("Recordatorio", identificador);
    }
}
