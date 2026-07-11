package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.diagram.dtos;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.DiagramEdge;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DiagramEdgeDTO")
class DiagramEdgeDTOTest {

    @Test
    @DisplayName("from debe mapear todos los campos del modelo al DTO")
    void shouldMapAllFieldsFromModelToDto() {
        // Given
        DiagramEdge edge = new DiagramEdge();
        edge.setId("edge-1");
        edge.setSource("node-1");
        edge.setTarget("node-2");
        edge.setLabel("flujo principal");
        edge.setType("smoothstep");

        // When
        DiagramEdgeDTO dto = DiagramEdgeDTO.from(edge);

        // Then
        assertEquals("edge-1", dto.id());
        assertEquals("node-1", dto.source());
        assertEquals("node-2", dto.target());
        assertEquals("flujo principal", dto.label());
        assertEquals("smoothstep", dto.type());
    }

    @Test
    @DisplayName("toModel debe mapear todos los campos del DTO al modelo")
    void shouldMapAllFieldsFromDtoToModel() {
        // Given
        DiagramEdgeDTO dto = new DiagramEdgeDTO("edge-2", "n1", "n2", "etiqueta", "straight");

        // When
        DiagramEdge model = dto.toModel();

        // Then
        assertEquals("edge-2", model.getId());
        assertEquals("n1", model.getSource());
        assertEquals("n2", model.getTarget());
        assertEquals("etiqueta", model.getLabel());
        assertEquals("straight", model.getType());
    }
}
