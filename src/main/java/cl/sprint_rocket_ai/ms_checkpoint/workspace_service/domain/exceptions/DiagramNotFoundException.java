package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.exceptions;

public class DiagramNotFoundException extends EntityNotFoundException {

    public DiagramNotFoundException(String identificador) {
        super("Diagram", identificador);
    }
}
