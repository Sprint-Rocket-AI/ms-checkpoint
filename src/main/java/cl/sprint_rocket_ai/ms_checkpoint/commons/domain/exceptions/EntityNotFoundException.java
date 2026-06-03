package cl.sprint_rocket_ai.ms_checkpoint.commons.domain.exceptions;

public class EntityNotFoundException extends RuntimeException {

    private final String entidad;
    private final String identificador;

    public EntityNotFoundException(String entidad, String identificador) {
        super(String.format("No se encontró %s con identificador: %s", entidad, identificador));
        this.entidad = entidad;
        this.identificador = identificador;
    }

    public String getEntidad() {
        return entidad;
    }

    public String getIdentificador() {
        return identificador;
    }
}
