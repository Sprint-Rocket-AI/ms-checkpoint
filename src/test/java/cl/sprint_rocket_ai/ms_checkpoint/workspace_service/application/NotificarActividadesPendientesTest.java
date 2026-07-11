package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.enums.EstadoActividad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Actividad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.out.IAEngineRestClient;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.out.NotificationJMS;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb.ActividadMongoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificarActividadesPendientes")
class NotificarActividadesPendientesTest {

    @Mock
    private ActividadMongoRepository actividadMongoRepository;

    @Mock
    private IAEngineRestClient iAEngineRestClient;

    @Mock
    private NotificationJMS notificationJMS;

    @InjectMocks
    private NotificarActividadesPendientes notificarActividadesPendientes;

    private Actividad buildActividad(String id, String userId) {
        Actividad a = new Actividad();
        a.setId(id);
        a.setUserId(userId);
        a.setTitulo("Actividad " + id);
        a.setEstado(EstadoActividad.PENDIENTE);
        a.setFechaCreacion(LocalDateTime.now().minusMinutes(30));
        return a;
    }

    @Nested
    @DisplayName("Cuando hay actividades pendientes en la última hora")
    class CuandoHayActividadesPendientes {

        @Test
        @DisplayName("Debe buscar actividades PENDIENTE con rango de la última hora")
        void shouldSearchPendingActivitiesWithLastHourRange() {
            // Given
            LocalDateTime antes = LocalDateTime.now().minusHours(1).minusSeconds(1);
            List<Actividad> pendientes = List.of(buildActividad("act-1", "user-1"));
            when(actividadMongoRepository.findByEstadoAndFechaCreacionBetween(
                    eq(EstadoActividad.PENDIENTE), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(pendientes);
            when(actividadMongoRepository.findByUserIdAndEstado("user-1", EstadoActividad.PENDIENTE))
                    .thenReturn(List.of(buildActividad("act-1", "user-1")));
            when(iAEngineRestClient.generatePopUp(any())).thenReturn("PopUp generado");

            // When
            notificarActividadesPendientes.execute();

            // Then
            ArgumentCaptor<LocalDateTime> desdeCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
            ArgumentCaptor<LocalDateTime> hastaCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
            verify(actividadMongoRepository).findByEstadoAndFechaCreacionBetween(
                    eq(EstadoActividad.PENDIENTE), desdeCaptor.capture(), hastaCaptor.capture());

            assertFalse(desdeCaptor.getValue().isBefore(antes),
                    "El inicio del rango debe ser aprox. hace 1 hora");
            assertFalse(hastaCaptor.getValue().isAfter(LocalDateTime.now().plusSeconds(1)),
                    "El fin del rango debe ser aprox. ahora");
        }

        @Test
        @DisplayName("Debe buscar todas las actividades PENDIENTE de cada userId agrupado")
        void shouldFetchAllPendingActivitiesForEachGroupedUser() {
            // Given
            List<Actividad> pendientes = List.of(
                    buildActividad("act-1", "user-1"),
                    buildActividad("act-2", "user-2")
            );
            when(actividadMongoRepository.findByEstadoAndFechaCreacionBetween(
                    eq(EstadoActividad.PENDIENTE), any(), any())).thenReturn(pendientes);
            when(actividadMongoRepository.findByUserIdAndEstado("user-1", EstadoActividad.PENDIENTE))
                    .thenReturn(List.of(buildActividad("act-1", "user-1")));
            when(actividadMongoRepository.findByUserIdAndEstado("user-2", EstadoActividad.PENDIENTE))
                    .thenReturn(List.of(buildActividad("act-2", "user-2")));
            when(iAEngineRestClient.generatePopUp(any())).thenReturn("PopUp");

            // When
            notificarActividadesPendientes.execute();

            // Then
            verify(actividadMongoRepository).findByUserIdAndEstado("user-1", EstadoActividad.PENDIENTE);
            verify(actividadMongoRepository).findByUserIdAndEstado("user-2", EstadoActividad.PENDIENTE);
        }

        @Test
        @DisplayName("Debe enviar solo un top 3 de actividades a generatePopUp cuando hay más de 3")
        void shouldSendOnlyTop3ActivitiesToGeneratePopUp() {
            // Given
            List<Actividad> todasPendientes = List.of(buildActividad("act-1", "user-1"));
            List<Actividad> actividadesUsuario = List.of(
                    buildActividad("a1", "user-1"),
                    buildActividad("a2", "user-1"),
                    buildActividad("a3", "user-1"),
                    buildActividad("a4", "user-1"),
                    buildActividad("a5", "user-1")
            );
            when(actividadMongoRepository.findByEstadoAndFechaCreacionBetween(
                    eq(EstadoActividad.PENDIENTE), any(), any())).thenReturn(todasPendientes);
            when(actividadMongoRepository.findByUserIdAndEstado("user-1", EstadoActividad.PENDIENTE))
                    .thenReturn(actividadesUsuario);
            when(iAEngineRestClient.generatePopUp(any())).thenReturn("PopUp top 3");

            // When
            notificarActividadesPendientes.execute();

            // Then
            ArgumentCaptor<List<Actividad>> captor = ArgumentCaptor.forClass(List.class);
            verify(iAEngineRestClient).generatePopUp(captor.capture());
            assertEquals(3, captor.getValue().size());
        }

        @Test
        @DisplayName("Debe enviar exactamente las actividades disponibles cuando son menos de 3")
        void shouldSendAllActivitiesWhenFewerThan3() {
            // Given
            List<Actividad> todasPendientes = List.of(buildActividad("act-1", "user-1"));
            List<Actividad> actividadesUsuario = List.of(
                    buildActividad("a1", "user-1"),
                    buildActividad("a2", "user-1")
            );
            when(actividadMongoRepository.findByEstadoAndFechaCreacionBetween(
                    eq(EstadoActividad.PENDIENTE), any(), any())).thenReturn(todasPendientes);
            when(actividadMongoRepository.findByUserIdAndEstado("user-1", EstadoActividad.PENDIENTE))
                    .thenReturn(actividadesUsuario);
            when(iAEngineRestClient.generatePopUp(any())).thenReturn("PopUp 2 acts");

            // When
            notificarActividadesPendientes.execute();

            // Then
            ArgumentCaptor<List<Actividad>> captor = ArgumentCaptor.forClass(List.class);
            verify(iAEngineRestClient).generatePopUp(captor.capture());
            assertEquals(2, captor.getValue().size());
        }

        @Test
        @DisplayName("Debe enviar el popUp obtenido de IAEngine a notifyDeveloper con el userId correcto")
        void shouldSendIaEnginePopUpToNotifyDeveloperWithCorrectUserId() {
            // Given
            List<Actividad> pendientes = List.of(buildActividad("act-1", "user-X"));
            List<Actividad> actividadesUsuario = List.of(buildActividad("a1", "user-X"));
            when(actividadMongoRepository.findByEstadoAndFechaCreacionBetween(
                    eq(EstadoActividad.PENDIENTE), any(), any())).thenReturn(pendientes);
            when(actividadMongoRepository.findByUserIdAndEstado("user-X", EstadoActividad.PENDIENTE))
                    .thenReturn(actividadesUsuario);
            when(iAEngineRestClient.generatePopUp(actividadesUsuario)).thenReturn("Contenido popup IA");

            // When
            notificarActividadesPendientes.execute();

            // Then
            verify(notificationJMS).notifyDeveloper("user-X", "Contenido popup IA");
        }

        @Test
        @DisplayName("Debe llamar a generatePopUp y luego notifyDeveloper en orden por usuario")
        void shouldCallGeneratePopUpThenNotifyDeveloperInOrder() {
            // Given
            List<Actividad> pendientes = List.of(buildActividad("act-1", "user-1"));
            List<Actividad> actividadesUsuario = List.of(buildActividad("a1", "user-1"));
            when(actividadMongoRepository.findByEstadoAndFechaCreacionBetween(
                    eq(EstadoActividad.PENDIENTE), any(), any())).thenReturn(pendientes);
            when(actividadMongoRepository.findByUserIdAndEstado("user-1", EstadoActividad.PENDIENTE))
                    .thenReturn(actividadesUsuario);
            when(iAEngineRestClient.generatePopUp(any())).thenReturn("PopUp");

            // When
            notificarActividadesPendientes.execute();

            // Then
            var inOrder = inOrder(iAEngineRestClient, notificationJMS);
            inOrder.verify(iAEngineRestClient).generatePopUp(any());
            inOrder.verify(notificationJMS).notifyDeveloper(eq("user-1"), anyString());
        }
    }

    @Nested
    @DisplayName("Cuando no hay actividades pendientes en la última hora")
    class CuandoNoHayActividadesPendientes {

        @Test
        @DisplayName("Debe retornar sin llamar a IAEngine ni a NotificationJMS")
        void shouldReturnEarlyWithoutCallingIaEngineOrJms() {
            // Given
            when(actividadMongoRepository.findByEstadoAndFechaCreacionBetween(
                    eq(EstadoActividad.PENDIENTE), any(), any())).thenReturn(List.of());

            // When
            notificarActividadesPendientes.execute();

            // Then
            verifyNoInteractions(iAEngineRestClient);
            verifyNoInteractions(notificationJMS);
        }
    }

    @Nested
    @DisplayName("Cuando ocurre un error al procesar un usuario")
    class CuandoOcurreErrorEnUnUsuario {

        @Test
        @DisplayName("Debe continuar procesando otros usuarios sin propagar la excepción")
        void shouldContinueProcessingOtherUsersOnException() {
            // Given
            List<Actividad> pendientes = List.of(
                    buildActividad("act-1", "user-error"),
                    buildActividad("act-2", "user-ok")
            );
            when(actividadMongoRepository.findByEstadoAndFechaCreacionBetween(
                    eq(EstadoActividad.PENDIENTE), any(), any())).thenReturn(pendientes);
            when(actividadMongoRepository.findByUserIdAndEstado("user-error", EstadoActividad.PENDIENTE))
                    .thenThrow(new RuntimeException("Error DB"));
            when(actividadMongoRepository.findByUserIdAndEstado("user-ok", EstadoActividad.PENDIENTE))
                    .thenReturn(List.of(buildActividad("act-2", "user-ok")));
            when(iAEngineRestClient.generatePopUp(any())).thenReturn("PopUp ok");

            // When / Then
            assertDoesNotThrow(() -> notificarActividadesPendientes.execute());
            verify(notificationJMS).notifyDeveloper(eq("user-ok"), anyString());
        }
    }
}
