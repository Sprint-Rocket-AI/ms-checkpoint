package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.recordatorio;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Recordatorio;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.recordatorio.dtos.CrearRecordatorioRequest;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.recordatorio.dtos.RecordatorioResponse;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb.RecordatorioMongoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CrearRecordatorio")
class CrearRecordatorioTest {

    @Mock
    private RecordatorioMongoRepository recordatorioRepository;

    @InjectMocks
    private CrearRecordatorio crearRecordatorio;

    // =========================================================================
    // Happy path
    // =========================================================================

    @Nested
    @DisplayName("Creación exitosa")
    class CreacionExitosa {

        @Test
        @DisplayName("Debe retornar RecordatorioResponse con todos los campos mapeados cuando la creación es exitosa")
        void shouldReturnRecordatorioResponseWhenCreationIsSuccessful() {
            // Given
            LocalDateTime expiracion = LocalDateTime.of(2026, 12, 31, 23, 59);
            CrearRecordatorioRequest request = new CrearRecordatorioRequest(
                    "dev-001", "Sincronización matutina", expiracion
            );
            Recordatorio saved = buildRecordatorio(
                    "mongo-rec-001", "dev-001",
                    "Sincronización matutina", true, expiracion, LocalDateTime.now()
            );
            when(recordatorioRepository.save(any(Recordatorio.class))).thenReturn(saved);

            // When
            RecordatorioResponse response = crearRecordatorio.execute(request);

            // Then
            assertNotNull(response);
            assertEquals("mongo-rec-001", response.id());
            assertEquals("dev-001", response.userId());
            assertEquals("Sincronización matutina", response.titulo());
            assertTrue(response.activo(), "El recordatorio debe estar activo por defecto");
            assertEquals(expiracion, response.fechaExpiracion());
            assertNotNull(response.fechaCreacion());
        }

        @Test
        @DisplayName("Debe setear activo=true por defecto antes de persistir, sin importar el contenido del request")
        void shouldSetActivoTrueByDefaultWhenCreatingRecordatorio() {
            // Given
            CrearRecordatorioRequest request = new CrearRecordatorioRequest(
                    "dev-002", "Revisar PRs pendientes", null
            );
            ArgumentCaptor<Recordatorio> captor = ArgumentCaptor.forClass(Recordatorio.class);
            when(recordatorioRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            // When
            crearRecordatorio.execute(request);

            // Then
            assertTrue(captor.getValue().isActivo(),
                    "El campo activo debe ser true al momento de persistir");
        }

        @Test
        @DisplayName("Debe setear fechaCreacion no nula al momento de la ejecución")
        void shouldSetNonNullFechaCreacionWhenCreatingRecordatorio() {
            // Given
            LocalDateTime antesDeEjecutar = LocalDateTime.now().minusSeconds(1);
            CrearRecordatorioRequest request = new CrearRecordatorioRequest(
                    "dev-003", "Verificar deploy en staging", null
            );
            ArgumentCaptor<Recordatorio> captor = ArgumentCaptor.forClass(Recordatorio.class);
            when(recordatorioRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            // When
            crearRecordatorio.execute(request);

            // Then
            LocalDateTime fechaCreacion = captor.getValue().getFechaCreacion();
            assertNotNull(fechaCreacion, "La fechaCreacion no debe ser null");
            assertFalse(fechaCreacion.isBefore(antesDeEjecutar),
                    "La fechaCreacion debe ser igual o posterior al inicio de la ejecución");
        }

        @Test
        @DisplayName("Debe aplicar los datos del request (userId, titulo, fechaExpiracion) al recordatorio antes de guardar")
        void shouldApplyRequestDataToRecordatorioBeforeSaving() {
            // Given
            LocalDateTime expiracion = LocalDateTime.of(2025, 6, 30, 12, 0);
            CrearRecordatorioRequest request = new CrearRecordatorioRequest(
                    "dev-matias-001", "Reunión de sprint", expiracion
            );
            ArgumentCaptor<Recordatorio> captor = ArgumentCaptor.forClass(Recordatorio.class);
            when(recordatorioRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            // When
            crearRecordatorio.execute(request);

            // Then
            Recordatorio guardado = captor.getValue();
            assertEquals("dev-matias-001", guardado.getUserId());
            assertEquals("Reunión de sprint", guardado.getTitulo());
            assertEquals(expiracion, guardado.getFechaExpiracion());
        }

        @Test
        @DisplayName("Debe invocar repository.save() exactamente una vez durante la creación")
        void shouldCallSaveExactlyOnceWhenCreatingRecordatorio() {
            // Given
            CrearRecordatorioRequest request = new CrearRecordatorioRequest(
                    "dev-004", "Revisar métricas de producción", null
            );
            when(recordatorioRepository.save(any(Recordatorio.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // When
            crearRecordatorio.execute(request);

            // Then
            verify(recordatorioRepository, times(1)).save(any(Recordatorio.class));
            verifyNoMoreInteractions(recordatorioRepository);
        }

        @Test
        @DisplayName("Debe retornar el id generado por MongoDB tal como lo devuelve repository.save()")
        void shouldReturnMongoGeneratedIdWhenRepositoryReturnsIt() {
            // Given
            String idGeneradoPorMongo = "665a1b2c3d4e5f6a7b8c9d0f";
            CrearRecordatorioRequest request = new CrearRecordatorioRequest(
                    "dev-005", "Recordatorio de cobertura", null
            );
            Recordatorio savedConId = buildRecordatorio(
                    idGeneradoPorMongo, "dev-005",
                    "Recordatorio de cobertura", true, null, LocalDateTime.now()
            );
            when(recordatorioRepository.save(any(Recordatorio.class))).thenReturn(savedConId);

            // When
            RecordatorioResponse response = crearRecordatorio.execute(request);

            // Then
            assertEquals(idGeneradoPorMongo, response.id(),
                    "El id del response debe ser el generado por MongoDB");
        }
    }

    // =========================================================================
    // Casos borde
    // =========================================================================

    @Nested
    @DisplayName("Casos borde")
    class CasosBorde {

        @Test
        @DisplayName("Debe crear el recordatorio correctamente cuando fechaExpiracion es null (campo opcional)")
        void shouldCreateRecordatorioWhenFechaExpiracionIsNull() {
            // Given
            CrearRecordatorioRequest request = new CrearRecordatorioRequest(
                    "dev-006", "Tarea sin vencimiento", null
            );
            ArgumentCaptor<Recordatorio> captor = ArgumentCaptor.forClass(Recordatorio.class);
            when(recordatorioRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            // When
            RecordatorioResponse response = crearRecordatorio.execute(request);

            // Then
            assertNull(captor.getValue().getFechaExpiracion(),
                    "La fechaExpiracion debe permanecer null cuando no se proporciona");
            assertTrue(captor.getValue().isActivo(),
                    "El recordatorio debe estar activo incluso sin fecha de expiración");
            assertNotNull(response);
        }

        @Test
        @DisplayName("Debe respetar que activo=true se setea antes de applyTo() y no es sobreescrito por el request")
        void shouldKeepActivoTrueAfterApplyToWhenRequestDoesNotOverrideIt() {
            // Given — applyTo() solo setea userId, titulo y fechaExpiracion; nunca toca activo
            CrearRecordatorioRequest request = new CrearRecordatorioRequest(
                    "dev-007", "Verificar orden de inicialización", LocalDateTime.now().plusDays(1)
            );
            ArgumentCaptor<Recordatorio> captor = ArgumentCaptor.forClass(Recordatorio.class);
            when(recordatorioRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            // When
            crearRecordatorio.execute(request);

            // Then
            Recordatorio guardado = captor.getValue();
            assertTrue(guardado.isActivo(),
                    "activo debe seguir siendo true tras la aplicación del request");
            // Además los datos del request deben estar presentes
            assertEquals("dev-007", guardado.getUserId());
            assertEquals("Verificar orden de inicialización", guardado.getTitulo());
        }

        @Test
        @DisplayName("Debe setear fechaCreacion dentro de un margen de 2 segundos respecto al momento de ejecución")
        void shouldSetFechaCreacionWithinTwoSecondsOfExecutionTime() {
            // Given
            CrearRecordatorioRequest request = new CrearRecordatorioRequest(
                    "dev-008", "Test de precisión temporal", null
            );
            LocalDateTime antes = LocalDateTime.now();
            ArgumentCaptor<Recordatorio> captor = ArgumentCaptor.forClass(Recordatorio.class);
            when(recordatorioRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            // When
            crearRecordatorio.execute(request);
            LocalDateTime despues = LocalDateTime.now().plusSeconds(2);

            // Then
            LocalDateTime fechaCreacion = captor.getValue().getFechaCreacion();
            assertFalse(fechaCreacion.isBefore(antes),
                    "fechaCreacion no debe ser anterior al inicio del test");
            assertFalse(fechaCreacion.isAfter(despues),
                    "fechaCreacion no debe superar el margen de 2 segundos");
        }

        @Test
        @DisplayName("Debe no invocar ningún otro método del repositorio además de save()")
        void shouldOnlyCallSaveAndNoOtherRepositoryMethodsWhenCreating() {
            // Given
            CrearRecordatorioRequest request = new CrearRecordatorioRequest(
                    "dev-009", "Verificar aislamiento", null
            );
            when(recordatorioRepository.save(any(Recordatorio.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // When
            crearRecordatorio.execute(request);

            // Then
            verify(recordatorioRepository).save(any(Recordatorio.class));
            verifyNoMoreInteractions(recordatorioRepository);
        }
    }

    // =========================================================================
    // Helper
    // =========================================================================

    private Recordatorio buildRecordatorio(String id, String userId, String titulo,
                                           boolean activo, LocalDateTime fechaExpiracion,
                                           LocalDateTime fechaCreacion) {
        Recordatorio r = new Recordatorio();
        r.setId(id);
        r.setUserId(userId);
        r.setTitulo(titulo);
        r.setActivo(activo);
        r.setFechaExpiracion(fechaExpiracion);
        r.setFechaCreacion(fechaCreacion);
        return r;
    }
}
