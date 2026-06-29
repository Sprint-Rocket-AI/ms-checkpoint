package cl.sprint_rocket_ai.ms_checkpoint.obtener_usuarios.infrastructure.in.dtos;

import cl.sprint_rocket_ai.ms_checkpoint.obtener_usuarios.domain.models.Usuario;

public record UsuarioResponse(String id, String userId, String correo) {
    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getUserId(), usuario.getCorreo());
    }
}
