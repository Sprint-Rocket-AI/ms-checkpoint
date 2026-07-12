package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.jobs.scheduler;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.jobs.ReminderPollingScheduler;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.events.ReminderTriggeredEvent;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Recordatorio;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb.RecordatorioMongoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReminderPollingScheduler")
class ReminderPollingSchedulerTest {

    @Mock
    private RecordatorioMongoRepository recordatorioRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ReminderPollingScheduler scheduler;

    private Recordatorio buildRecordatorio(String id, String userId, LocalDateTime expiracion) {
        Recordatorio r = new Recordatorio();
        r.setId(id);
        r.setUserId(userId);
        r.setTitulo("Recordatorio " + id);
        r.setActivo(true);
        r.setFechaExpiracion(expiracion);
        return r;
    }

    @Nested
    @DisplayName("poll — cuando no hay recordatorios activos")
    class SinRecordatorios {

        @Test
        @DisplayName("Debe terminar sin publicar eventos cuando no hay recordatorios activos")
        void shouldNotPublishEventsWhenNoActiveReminders() {
            // Given
            when(recordatorioRepository.findByActivoTrue()).thenReturn(List.of());

            // When
            scheduler.poll();

            // Then
            verifyNoInteractions(eventPublisher);
        }
    }

    @Nested
    @DisplayName("poll — cuando hay recordatorios activos")
    class ConRecordatorios {

        @Test
        @DisplayName("Debe publicar un ReminderTriggeredEvent por cada recordatorio activo")
        void shouldPublishEventForEachActiveReminder() {
            // Given
            List<Recordatorio> activos = List.of(
                    buildRecordatorio("r1", "user-1", null),
                    buildRecordatorio("r2", "user-2", null)
            );
            when(recordatorioRepository.findByActivoTrue()).thenReturn(activos);
            when(recordatorioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // When
            scheduler.poll();

            // Then
            verify(eventPublisher, times(2)).publishEvent(any(ReminderTriggeredEvent.class));
        }

        @Test
        @DisplayName("Debe publicar el evento con los datos correctos del recordatorio")
        void shouldPublishEventWithCorrectReminderData() {
            // Given
            Recordatorio rec = buildRecordatorio("r1", "user-1", null);
            when(recordatorioRepository.findByActivoTrue()).thenReturn(List.of(rec));
            when(recordatorioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            ArgumentCaptor<ReminderTriggeredEvent> captor = ArgumentCaptor.forClass(ReminderTriggeredEvent.class);

            // When
            scheduler.poll();

            // Then
            verify(eventPublisher).publishEvent(captor.capture());
            ReminderTriggeredEvent event = captor.getValue();
            assertEquals("r1", event.getRecordatorioId());
            assertEquals("user-1", event.getUserId());
            assertEquals("Recordatorio r1", event.getTitulo());
        }

        @Test
        @DisplayName("Debe desactivar el recordatorio cuando supera su fecha de expiración")
        void shouldDeactivateReminderWhenExpired() {
            // Given
            Recordatorio rec = buildRecordatorio("r1", "user-1", LocalDateTime.now().minusMinutes(1));
            when(recordatorioRepository.findByActivoTrue()).thenReturn(List.of(rec));
            ArgumentCaptor<Recordatorio> saveCaptor = ArgumentCaptor.forClass(Recordatorio.class);
            when(recordatorioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // When
            scheduler.poll();

            // Then
            verify(recordatorioRepository).save(saveCaptor.capture());
            assertFalse(saveCaptor.getValue().isActivo());
        }

        @Test
        @DisplayName("No debe desactivar el recordatorio cuando aún no ha expirado")
        void shouldNotDeactivateReminderWhenNotExpired() {
            // Given
            Recordatorio rec = buildRecordatorio("r1", "user-1", LocalDateTime.now().plusDays(1));
            when(recordatorioRepository.findByActivoTrue()).thenReturn(List.of(rec));
            ArgumentCaptor<Recordatorio> saveCaptor = ArgumentCaptor.forClass(Recordatorio.class);
            when(recordatorioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // When
            scheduler.poll();

            // Then
            verify(recordatorioRepository).save(saveCaptor.capture());
            assertTrue(saveCaptor.getValue().isActivo());
        }

        @Test
        @DisplayName("Debe continuar procesando otros recordatorios cuando uno lanza excepción")
        void shouldContinueProcessingWhenOneReminderFails() {
            // Given
            Recordatorio recOk = buildRecordatorio("r-ok", "user-ok", null);
            when(recordatorioRepository.findByActivoTrue()).thenReturn(List.of(recOk));
            when(recordatorioRepository.save(any())).thenThrow(new RuntimeException("DB error"));

            // When / Then
            assertDoesNotThrow(() -> scheduler.poll());
            verify(eventPublisher, atLeastOnce()).publishEvent(any());
        }
    }
}
