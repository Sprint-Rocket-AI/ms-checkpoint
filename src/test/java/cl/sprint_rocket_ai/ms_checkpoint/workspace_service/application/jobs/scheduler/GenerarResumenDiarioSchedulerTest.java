package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.jobs.scheduler;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.jobs.GenerarResumenDiarioScheduler;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.jobs.step_job.ResumenDiarioProcessor;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.jobs.step_job.ResumenDiarioReader;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.jobs.step_job.ResumenDiarioWriter;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.out.dto.ResumenUsuarioDto;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.out.dto.UsuarioActividadesDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GenerarResumenDiarioScheduler")
class GenerarResumenDiarioSchedulerTest {

    @Mock private ResumenDiarioReader reader;
    @Mock private ResumenDiarioProcessor processor;
    @Mock private ResumenDiarioWriter writer;

    @InjectMocks
    private GenerarResumenDiarioScheduler scheduler;

    @Test
    @DisplayName("Debe ejecutar reader → processor → writer con la fecha de ayer")
    void shouldExecuteFullPipelineWithYesterdaysDate() {
        // Given
        LocalDate ayer = LocalDate.now().minusDays(1);
        List<UsuarioActividadesDto> datos = List.of(
                new UsuarioActividadesDto("user-1", "u@mail.com", List.of()));
        List<ResumenUsuarioDto> procesados = List.of(
                new ResumenUsuarioDto("user-1", "u@mail.com", "Resumen", List.of()));
        when(reader.read(ayer)).thenReturn(datos);
        when(processor.process(datos, ayer)).thenReturn(procesados);

        // When
        scheduler.execute();

        // Then
        var inOrder = inOrder(reader, processor, writer);
        inOrder.verify(reader).read(ayer);
        inOrder.verify(processor).process(datos, ayer);
        inOrder.verify(writer).write(procesados);
    }

    @Test
    @DisplayName("Debe terminar sin llamar a processor ni writer cuando reader no retorna datos")
    void shouldSkipProcessorAndWriterWhenReaderReturnsEmpty() {
        // Given
        when(reader.read(any())).thenReturn(List.of());

        // When
        scheduler.execute();

        // Then
        verifyNoInteractions(processor);
        verifyNoInteractions(writer);
    }

    @Test
    @DisplayName("No debe propagar excepción cuando el pipeline falla")
    void shouldNotPropagateExceptionWhenPipelineFails() {
        // Given
        when(reader.read(any())).thenThrow(new RuntimeException("Error de conexión"));

        // When / Then
        assertDoesNotThrow(() -> scheduler.execute());
    }
}
