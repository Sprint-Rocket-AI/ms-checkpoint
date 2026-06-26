package cl.sprint_rocket_ai.ms_checkpoint.gestionar_usuarios.infrastructure.in;

import cl.sprint_rocket_ai.ms_checkpoint.gestionar_usuarios.application.*;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_usuarios.domain.models.Usuario;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_usuarios.infrastructure.in.dtos.UsuarioRequest;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_usuarios.infrastructure.in.dtos.UsuarioResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final CrearUsuario crearUsuario;
    private final ObtenerUsuario obtenerUsuario;
    private final ListarUsuarios listarUsuarios;
    private final ActualizarUsuario actualizarUsuario;
    private final EliminarUsuario eliminarUsuario;

    public UsuarioController(CrearUsuario crearUsuario,
                             ObtenerUsuario obtenerUsuario,
                             ListarUsuarios listarUsuarios,
                             ActualizarUsuario actualizarUsuario,
                             EliminarUsuario eliminarUsuario) {
        this.crearUsuario = crearUsuario;
        this.obtenerUsuario = obtenerUsuario;
        this.listarUsuarios = listarUsuarios;
        this.actualizarUsuario = actualizarUsuario;
        this.eliminarUsuario = eliminarUsuario;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(@RequestBody UsuarioRequest request) {
        Usuario creado = crearUsuario.execute(request.userId(), request.correo());
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioResponse.from(creado));
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

    @PutMapping("/{userId}")
    public ResponseEntity<UsuarioResponse> actualizar(@PathVariable String userId, @RequestBody UsuarioRequest request) {
        Usuario actualizado = actualizarUsuario.execute(userId, request.correo());
        return ResponseEntity.ok(UsuarioResponse.from(actualizado));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> eliminar(@PathVariable String userId) {
        eliminarUsuario.execute(userId);
        return ResponseEntity.noContent().build();
    }
}
