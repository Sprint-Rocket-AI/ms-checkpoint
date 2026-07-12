package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.diagram.dtos;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.DiagramNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DiagramNodeDTO")
class DiagramNodeDTOTest {

    @Test
    @DisplayName("from debe mapear todos los campos del modelo al DTO")
    void shouldMapAllFieldsFromModelToDto() {
        // Given
        DiagramNode node = new DiagramNode();
        node.setId("node-1");
        node.setType("default");
        node.setLabel("Inicio");
        node.setPositionX(100.0);
        node.setPositionY(200.0);

        // When
        DiagramNodeDTO dto = DiagramNodeDTO.from(node);

        // Then
        assertEquals("node-1", dto.id());
        assertEquals("default", dto.type());
        assertEquals("Inicio", dto.label());
        assertEquals(100.0, dto.positionX());
        assertEquals(200.0, dto.positionY());
    }

    @Test
    @DisplayName("toModel debe mapear todos los campos del DTO al modelo")
    void shouldMapAllFieldsFromDtoToModel() {
        // Given
        DiagramNodeDTO dto = new DiagramNodeDTO("node-2", "circle", "Proceso", 50.0, 75.0);

        // When
        DiagramNode model = dto.toModel();

        // Then
        assertEquals("node-2", model.getId());
        assertEquals("circle", model.getType());
        assertEquals("Proceso", model.getLabel());
        assertEquals(50.0, model.getPositionX());
        assertEquals(75.0, model.getPositionY());
    }
}
