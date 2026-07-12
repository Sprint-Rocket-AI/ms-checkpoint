package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.jobs;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.jobs.step_job.ResumenDiarioWriter;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.SugerenciaActividad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.out.NotificationJMS;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.out.dto.ResumenUsuarioDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ResumenDiarioWriter")
class ResumenDiarioWriterTest {

    @Mock
    private NotificationJMS notificationJMS;

    @InjectMocks
    private ResumenDiarioWriter resumenDiarioWriter;

    private ResumenUsuarioDto buildDto(String userId, String correo) {
        List<SugerenciaActividad> sugerencias = List.of(
                new SugerenciaActividad("Revisar PR", "Pendiente de revisión", "ALTA", "Bloquea al equipo")
        );
        return new ResumenUsuarioDto(userId, correo, "Resumen del día de " + userId, sugerencias);
    }

    @Nested
    @DisplayName("Cuando el envío es exitoso")
    class CuandoElEnvioEsExitoso {

        @Test
        @DisplayName("Debe llamar a notifyDeveloperWithSummary por cada usuario con sus datos correctos")
        void shouldCallNotifyDeveloperWithSummaryForEachUserWithCorrectData() {
            // Given
            ResumenUsuarioDto dto1 = buildDto("user-1", "u1@empresa.com");
            ResumenUsuarioDto dto2 = buildDto("user-2", "u2@empresa.com");

            // When
            resumenDiarioWriter.write(List.of(dto1, dto2));

            // Then
            verify(notificationJMS).notifyDeveloperWithSummary(
                    "user-1", "u1@empresa.com", dto1.resumen(), dto1.sugerencias());
            verify(notificationJMS).notifyDeveloperWithSummary(
                    "user-2", "u2@empresa.com", dto2.resumen(), dto2.sugerencias());
        }

        @Test
        @DisplayName("Debe enviar el correo con el userId, correo, resumen y sugerencias exactos del DTO")
        void shouldSendExactFieldsFromDto() {
            // Given
            ResumenUsuarioDto dto = buildDto("user-X", "sebastian@empresa.com");

            // When
            resumenDiarioWriter.write(List.of(dto));

            // Then
            verify(notificationJMS).notifyDeveloperWithSummary(
                    "user-X",
                    "sebastian@empresa.com",
                    "Resumen del día de user-X",
                    dto.sugerencias()
            );
        }

        @Test
        @DisplayName("Debe llamar a notificationJMS en el orden en que aparecen los usuarios en la lista")
        void shouldCallNotificationInListOrder() {
            // Given
            ResumenUsuarioDto dto1 = buildDto("user-A", "a@empresa.com");
            ResumenUsuarioDto dto2 = buildDto("user-B", "b@empresa.com");
            ResumenUsuarioDto dto3 = buildDto("user-C", "c@empresa.com");

            // When
            resumenDiarioWriter.write(List.of(dto1, dto2, dto3));

            // Then
            InOrder inOrder = inOrder(notificationJMS);
            inOrder.verify(notificationJMS).notifyDeveloperWithSummary(
                    eq("user-A"), eq("a@empresa.com"), any(), any());
            inOrder.verify(notificationJMS).notifyDeveloperWithSummary(
                    eq("user-B"), eq("b@empresa.com"), any(), any());
            inOrder.verify(notificationJMS).notifyDeveloperWithSummary(
                    eq("user-C"), eq("c@empresa.com"), any(), any());
            verifyNoMoreInteractions(notificationJMS);
        }

        @Test
        @DisplayName("No debe llamar a notificationJMS cuando la lista está vacía")
        void shouldNotCallNotificationJMSWhenListIsEmpty() {
            // When
            resumenDiarioWriter.write(List.of());

            // Then
            verifyNoInteractions(notificationJMS);
        }
    }

    @Nested
    @DisplayName("Cuando notificationJMS lanza excepción para un usuario")
    class CuandoNotificationJMSLanzaExcepcion {

        @Test
        @DisplayName("Debe continuar enviando al resto de usuarios sin propagar la excepción")
        void shouldContinueSendingToOtherUsersWithoutPropagatingException() {
            // Given
            ResumenUsuarioDto dtoError = buildDto("user-error", "error@empresa.com");
            ResumenUsuarioDto dtoOk = buildDto("user-ok", "ok@empresa.com");
            doThrow(new RuntimeException("Error JMS"))
                    .when(notificationJMS).notifyDeveloperWithSummary(
                            eq("user-error"), any(), any(), any());

            // When
            resumenDiarioWriter.write(List.of(dtoError, dtoOk));

            // Then
            verify(notificationJMS).notifyDeveloperWithSummary(
                    eq("user-ok"), eq("ok@empresa.com"), any(), any());
        }

        @Test
        @DisplayName("No debe propagar excepción cuando todos los envíos fallan")
        void shouldNotPropagateExceptionWhenAllSendsFail() {
            // Given
            ResumenUsuarioDto dto = buildDto("user-1", "u@empresa.com");
            doThrow(new RuntimeException("Fallo de conexión"))
                    .when(notificationJMS).notifyDeveloperWithSummary(any(), any(), any(), any());

            // When / Then
            assertDoesNotThrow(() -> resumenDiarioWriter.write(List.of(dto)));
        }
    }
}
