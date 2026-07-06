package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.diagram;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.diagram.ActualizarDiagram;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.diagram.CrearDiagram;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.diagram.EliminarDiagram;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.diagram.ObtenerDiagramById;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.diagram.ActualizarDiagramGraph;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.diagram.ActualizarDiagramDescription;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.diagram.ObtenerDiagramasByUsuario;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.diagram.dtos.ActualizarDiagramRequest;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.diagram.dtos.ActualizarDiagramGraphRequest;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.diagram.dtos.ActualizarDiagramDescriptionRequest;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.diagram.dtos.CrearDiagramRequest;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.diagram.dtos.DiagramResponse;
import java.util.List;
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
    private final ActualizarDiagramGraph actualizarDiagramGraph;
    private final ActualizarDiagramDescription actualizarDiagramDescription;
    private final ObtenerDiagramasByUsuario obtenerDiagramasByUsuario;

    public DiagramController(CrearDiagram crearDiagram,
                             ObtenerDiagramById obtenerDiagramById,
                             ActualizarDiagram actualizarDiagram,
                             EliminarDiagram eliminarDiagram,
                             ActualizarDiagramGraph actualizarDiagramGraph,
                             ActualizarDiagramDescription actualizarDiagramDescription,
                             ObtenerDiagramasByUsuario obtenerDiagramasByUsuario) {
        this.crearDiagram = crearDiagram;
        this.obtenerDiagramById = obtenerDiagramById;
        this.actualizarDiagram = actualizarDiagram;
        this.eliminarDiagram = eliminarDiagram;
        this.actualizarDiagramGraph = actualizarDiagramGraph;
        this.actualizarDiagramDescription = actualizarDiagramDescription;
        this.obtenerDiagramasByUsuario = obtenerDiagramasByUsuario;
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

    @Override
    @PatchMapping("/{id}/graph")
    public ResponseEntity<DiagramResponse> updateGraph(@PathVariable String id,
                                                       @RequestBody ActualizarDiagramGraphRequest request) {
        return ResponseEntity.ok(actualizarDiagramGraph.execute(id, request));
    }

    @Override
    @PatchMapping("/{id}/description")
    public ResponseEntity<DiagramResponse> updateDescription(@PathVariable String id,
                                                             @RequestBody ActualizarDiagramDescriptionRequest request) {
        return ResponseEntity.ok(actualizarDiagramDescription.execute(id, request));
    }

    @Override
    @GetMapping("/desarrollador/{userId}")
    public ResponseEntity<List<DiagramResponse>> findByDesarrollador(@PathVariable String userId) {
        return ResponseEntity.ok(obtenerDiagramasByUsuario.execute(userId));
    }
}
