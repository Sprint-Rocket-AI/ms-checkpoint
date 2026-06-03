package cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.infrastructure.in;

import cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.application.ActualizarRecordatorio;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.application.CrearRecordatorio;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.application.EliminarRecordatorio;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.application.ListarRecordatoriosByDesarrollador;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.application.ObtenerRecordatorioById;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.infrastructure.in.dtos.ActualizarRecordatorioRequest;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.infrastructure.in.dtos.CrearRecordatorioRequest;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.infrastructure.in.dtos.RecordatorioResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recordatorios")
public final class RecordatorioController implements RecordatorioRest {

    private final CrearRecordatorio crearRecordatorio;
    private final ObtenerRecordatorioById obtenerRecordatorioById;
    private final ListarRecordatoriosByDesarrollador listarRecordatoriosByDesarrollador;
    private final ActualizarRecordatorio actualizarRecordatorio;
    private final EliminarRecordatorio eliminarRecordatorio;

    public RecordatorioController(CrearRecordatorio crearRecordatorio,
                                  ObtenerRecordatorioById obtenerRecordatorioById,
                                  ListarRecordatoriosByDesarrollador listarRecordatoriosByDesarrollador,
                                  ActualizarRecordatorio actualizarRecordatorio,
                                  EliminarRecordatorio eliminarRecordatorio) {
        this.crearRecordatorio = crearRecordatorio;
        this.obtenerRecordatorioById = obtenerRecordatorioById;
        this.listarRecordatoriosByDesarrollador = listarRecordatoriosByDesarrollador;
        this.actualizarRecordatorio = actualizarRecordatorio;
        this.eliminarRecordatorio = eliminarRecordatorio;
    }

    @Override
    @PostMapping
    public ResponseEntity<RecordatorioResponse> create(@RequestBody CrearRecordatorioRequest request) {
        return new ResponseEntity<>(crearRecordatorio.execute(request), HttpStatus.CREATED);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<RecordatorioResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(obtenerRecordatorioById.execute(id));
    }

    @Override
    @GetMapping("/desarrollador/{userId}")
    public ResponseEntity<List<RecordatorioResponse>> findByDesarrollador(@PathVariable String userId) {
        return ResponseEntity.ok(listarRecordatoriosByDesarrollador.execute(userId));
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<RecordatorioResponse> update(@PathVariable String id,
                                                       @RequestBody ActualizarRecordatorioRequest request) {
        return ResponseEntity.ok(actualizarRecordatorio.execute(id, request));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        eliminarRecordatorio.execute(id);
        return ResponseEntity.noContent().build();
    }
}
