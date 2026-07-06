package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.enums.TipoDiagrama;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "diagrams")
public class Diagram {

    @Id
    private String id;

    @Field("name")
    private String name;

    @Field("description")
    private String description;

    @Field("nodes")
    private List<DiagramNode> nodes;

    @Field("edges")
    private List<DiagramEdge> edges;

    @Field("tipo")
    private TipoDiagrama tipo;

    @Field("user_id")
    private String userId;

    @Field("fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Field("fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    public Diagram() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<DiagramNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<DiagramNode> nodes) {
        this.nodes = nodes;
    }

    public List<DiagramEdge> getEdges() {
        return edges;
    }

    public void setEdges(List<DiagramEdge> edges) {
        this.edges = edges;
    }

    public TipoDiagrama getTipo() {
        return tipo;
    }

    public void setTipo(TipoDiagrama tipo) {
        this.tipo = tipo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
