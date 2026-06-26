package cl.sprint_rocket_ai.ms_checkpoint.gestionar_usuarios.infrastructure.in.dtos;

import cl.sprint_rocket_ai.ms_checkpoint.gestionar_usuarios.domain.models.Usuario;

public record UsuarioResponse(String id, String userId, String correo) {
    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getUserId(), usuario.getCorreo());
    }
}
