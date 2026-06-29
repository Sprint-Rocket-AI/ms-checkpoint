package cl.sprint_rocket_ai.ms_checkpoint.obtener_usuarios.infrastructure.in;

import cl.sprint_rocket_ai.ms_checkpoint.obtener_usuarios.application.*;
import cl.sprint_rocket_ai.ms_checkpoint.obtener_usuarios.domain.models.Usuario;
import cl.sprint_rocket_ai.ms_checkpoint.obtener_usuarios.infrastructure.in.dtos.UsuarioResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final ObtenerUsuario obtenerUsuario;
    private final ListarUsuarios listarUsuarios;

    public UsuarioController(ObtenerUsuario obtenerUsuario,
                             ListarUsuarios listarUsuarios) {
        this.obtenerUsuario = obtenerUsuario;
        this.listarUsuarios = listarUsuarios;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listar() {
        List<UsuarioResponse> usuarios = listarUsuarios.execute().stream()
                .map(UsuarioResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UsuarioResponse> obtener(@PathVariable String userId) {
        Usuario usuario = obtenerUsuario.execute(userId);
        return ResponseEntity.ok(UsuarioResponse.from(usuario));
    }
}

