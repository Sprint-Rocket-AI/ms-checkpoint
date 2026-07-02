package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.diagram;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.diagram.ActualizarDiagram;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.diagram.CrearDiagram;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.diagram.EliminarDiagram;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.diagram.ObtenerDiagramById;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.diagram.dtos.ActualizarDiagramRequest;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.diagram.dtos.CrearDiagramRequest;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.diagram.dtos.DiagramResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/diagrams")
public final class DiagramController implements DiagramRest {

    private final CrearDiagram crearDiagram;
    private final ObtenerDiagramById obtenerDiagramById;
    private final ActualizarDiagram actualizarDiagram;
    private final EliminarDiagram eliminarDiagram;

    public DiagramController(CrearDiagram crearDiagram,
                             ObtenerDiagramById obtenerDiagramById,
                             ActualizarDiagram actualizarDiagram,
                             EliminarDiagram eliminarDiagram) {
        this.crearDiagram = crearDiagram;
        this.obtenerDiagramById = obtenerDiagramById;
        this.actualizarDiagram = actualizarDiagram;
        this.eliminarDiagram = eliminarDiagram;
    }

    @Override
    @PostMapping
    public ResponseEntity<DiagramResponse> create(@RequestBody CrearDiagramRequest request) {
        return new ResponseEntity<>(crearDiagram.execute(request), HttpStatus.CREATED);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<DiagramResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(obtenerDiagramById.execute(id));
    }

    @Override
    @PatchMapping("/{id}/name")
    public ResponseEntity<DiagramResponse> updateName(@PathVariable String id,
                                                      @RequestBody ActualizarDiagramRequest request) {
        return ResponseEntity.ok(actualizarDiagram.execute(id, request));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        eliminarDiagram.execute(id);
        return ResponseEntity.noContent().build();
    }
}
