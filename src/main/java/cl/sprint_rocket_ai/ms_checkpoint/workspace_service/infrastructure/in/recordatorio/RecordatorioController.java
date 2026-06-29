package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.recordatorio;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.recordatorio.ActualizarRecordatorio;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.recordatorio.CompletarRecordatorio;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.recordatorio.CrearRecordatorio;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.recordatorio.EliminarRecordatorio;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.recordatorio.ListarRecordatoriosByDesarrollador;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.recordatorio.ObtenerRecordatorioById;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.recordatorio.PosponerRecordatorio;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.recordatorio.dtos.ActualizarRecordatorioRequest;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.recordatorio.dtos.CrearRecordatorioRequest;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.recordatorio.dtos.RecordatorioResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    private final CompletarRecordatorio completarRecordatorio;
    private final PosponerRecordatorio posponerRecordatorio;

    public RecordatorioController(CrearRecordatorio crearRecordatorio,
                                  ObtenerRecordatorioById obtenerRecordatorioById,
                                  ListarRecordatoriosByDesarrollador listarRecordatoriosByDesarrollador,
                                  ActualizarRecordatorio actualizarRecordatorio,
                                  EliminarRecordatorio eliminarRecordatorio,
                                  CompletarRecordatorio completarRecordatorio,
                                  PosponerRecordatorio posponerRecordatorio) {
        this.crearRecordatorio = crearRecordatorio;
        this.obtenerRecordatorioById = obtenerRecordatorioById;
        this.listarRecordatoriosByDesarrollador = listarRecordatoriosByDesarrollador;
        this.actualizarRecordatorio = actualizarRecordatorio;
        this.eliminarRecordatorio = eliminarRecordatorio;
        this.completarRecordatorio = completarRecordatorio;
        this.posponerRecordatorio = posponerRecordatorio;
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

    @Override
    @PatchMapping("/{id}/completar")
    public ResponseEntity<RecordatorioResponse> completar(@PathVariable String id) {
        return ResponseEntity.ok(completarRecordatorio.execute(id));
    }

    @Override
    @PostMapping("/{id}/posponer")
    public ResponseEntity<RecordatorioResponse> posponer(
            @PathVariable String id,
            @RequestParam(defaultValue = "10") int minutos) {
        return ResponseEntity.ok(posponerRecordatorio.execute(id, minutos));
    }
}
