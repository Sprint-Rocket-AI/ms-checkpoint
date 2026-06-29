package cl.sprint_rocket_ai.ms_checkpoint.obtener_usuarios.domain.models;

public class Usuario {
    
    private String id;
    private String userId;
    private String correo;

    public Usuario() {
    }

    public Usuario(String userId, String correo) {
        this.userId = userId;
        this.correo = correo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
