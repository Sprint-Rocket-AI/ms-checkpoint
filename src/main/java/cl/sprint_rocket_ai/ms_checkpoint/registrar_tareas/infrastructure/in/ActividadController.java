package cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.infrastructure.in;

import cl.sprint_rocket_ai.ms_checkpoint.commons.domain.enums.EstadoActividad;
import cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.application.ActualizarActividad;
import cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.application.CrearActividad;
import cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.application.EliminarActividad;
import cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.application.ListarActividadesByDesarrollador;
import cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.application.ListarActividadesByFecha;
import cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.application.ObtenerActividadById;
import cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.infrastructure.in.dtos.ActividadResponse;
import cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.infrastructure.in.dtos.ActualizarActividadRequest;
import cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.infrastructure.in.dtos.CrearActividadRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/actividades")
public final class ActividadController implements ActividadRest {

    private final CrearActividad crearActividad;
    private final ObtenerActividadById obtenerActividadById;
    private final ListarActividadesByDesarrollador listarActividadesByDesarrollador;
    private final ActualizarActividad actualizarActividad;
    private final EliminarActividad eliminarActividad;
    private final ListarActividadesByFecha listarActividadesByFecha;

    public ActividadController(CrearActividad crearActividad,
                               ObtenerActividadById obtenerActividadById,
                               ListarActividadesByDesarrollador listarActividadesByDesarrollador,
                               ActualizarActividad actualizarActividad,
                               EliminarActividad eliminarActividad,
                               ListarActividadesByFecha listarActividadesByFecha) {
        this.crearActividad = crearActividad;
        this.obtenerActividadById = obtenerActividadById;
        this.listarActividadesByDesarrollador = listarActividadesByDesarrollador;
        this.actualizarActividad = actualizarActividad;
        this.eliminarActividad = eliminarActividad;
        this.listarActividadesByFecha = listarActividadesByFecha;
    }

    @Override
    @PostMapping
    public ResponseEntity<ActividadResponse> create(@RequestBody CrearActividadRequest request) {
        return new ResponseEntity<>(crearActividad.execute(request), HttpStatus.CREATED);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ActividadResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(obtenerActividadById.execute(id));
    }

    @Override
    @GetMapping("/desarrollador/{userId}")
    public ResponseEntity<List<ActividadResponse>> findByDesarrollador(
            @PathVariable String userId,
            @RequestParam(required = false) EstadoActividad estado) {
        return ResponseEntity.ok(listarActividadesByDesarrollador.execute(userId, estado));
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<ActividadResponse> update(@PathVariable String id,
                                                    @RequestBody ActualizarActividadRequest request) {
        return ResponseEntity.ok(actualizarActividad.execute(id, request));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        eliminarActividad.execute(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<List<ActividadResponse>> findByFecha(
            @PathVariable java.time.LocalDate fecha,
            @RequestParam String userId) {
        return ResponseEntity.ok(listarActividadesByFecha.execute(userId, fecha));
    }
}
