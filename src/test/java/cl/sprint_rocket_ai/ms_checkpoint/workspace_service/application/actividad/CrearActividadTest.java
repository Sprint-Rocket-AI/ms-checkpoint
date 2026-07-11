package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.actividad;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.enums.EstadoActividad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Actividad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.actividad.dtos.ActividadResponse;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.actividad.dtos.CrearActividadRequest;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CrearActividad")
class CrearActividadTest {

    @Mock
    private ActividadMongoRepository actividadRepository;

    @InjectMocks
    private CrearActividad crearActividad;

    // =========================================================================
    // Happy path
    // =========================================================================

    @Nested
    @DisplayName("Creación exitosa")
    class CreacionExitosa {

        @Test
        @DisplayName("Debe retornar ActividadResponse con todos los campos mapeados cuando la creación es exitosa")
        void shouldReturnActividadResponseWhenCreationIsSuccessful() {
            // Given
            CrearActividadRequest request = new CrearActividadRequest(
                    "dev-001",
                    "Implementar login OAuth2",
                    "Configurar Google como provider"
            );
            Actividad savedActividad = buildActividad(
                    "mongo-id-abc",
                    "dev-001",
                    "Implementar login OAuth2",
                    "Configurar Google como provider",
                    EstadoActividad.PENDIENTE,
                    LocalDateTime.now()
            );
            when(actividadRepository.save(any(Actividad.class))).thenReturn(savedActividad);

            // When
            ActividadResponse response = crearActividad.execute(request);

            // Then
            assertNotNull(response);
            assertEquals("mongo-id-abc", response.id());
            assertEquals("dev-001", response.userId());
            assertEquals("Implementar login OAuth2", response.titulo());
            assertEquals("Configurar Google como provider", response.descripcion());
            assertEquals(EstadoActividad.PENDIENTE, response.estado());
            assertNotNull(response.fechaCreacion());
        }

        @Test
        @DisplayName("Debe asignar estado PENDIENTE por defecto sin importar el contenido del request")
        void shouldAssignPendienteStatusWhenActivityIsCreated() {
            // Given
            CrearActividadRequest request = new CrearActividadRequest(
                    "dev-002",
                    "Revisar PRs del sprint",
                    "Revisar los pull requests pendientes"
            );
            ArgumentCaptor<Actividad> captor = ArgumentCaptor.forClass(Actividad.class);
            when(actividadRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            // When
            crearActividad.execute(request);

            // Then
            Actividad actividadGuardada = captor.getValue();
            assertEquals(EstadoActividad.PENDIENTE, actividadGuardada.getEstado(),
                    "El estado debe ser PENDIENTE al momento de persistir");
        }

        @Test
        @DisplayName("Debe asignar fechaCreacion no nula al momento de la ejecución")
        void shouldAssignNonNullFechaCreacionWhenActivityIsCreated() {
            // Given
            CrearActividadRequest request = new CrearActividadRequest(
                    "dev-003",
                    "Configurar pipeline CI/CD",
                    "Agregar stage de pruebas unitarias"
            );
            LocalDateTime antesDeEjecutar = LocalDateTime.now().minusSeconds(1);
            ArgumentCaptor<Actividad> captor = ArgumentCaptor.forClass(Actividad.class);
            when(actividadRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            // When
            crearActividad.execute(request);

            // Then
            Actividad actividadGuardada = captor.getValue();
            assertNotNull(actividadGuardada.getFechaCreacion(),
                    "La fechaCreacion no debe ser null");
            assertFalse(actividadGuardada.getFechaCreacion().isBefore(antesDeEjecutar),
                    "La fechaCreacion debe ser igual o posterior al momento de ejecutar el método");
        }

        @Test
        @DisplayName("Debe aplicar los datos del request (userId, titulo, descripcion) a la actividad antes de guardar")
        void shouldApplyRequestDataToActividadWhenSaving() {
            // Given
            CrearActividadRequest request = new CrearActividadRequest(
                    "dev-matias-001",
                    "Migrar base de datos a MongoDB",
                    "Pasar esquemas de PostgreSQL a colecciones Mongo"
            );
            ArgumentCaptor<Actividad> captor = ArgumentCaptor.forClass(Actividad.class);
            when(actividadRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            // When
            crearActividad.execute(request);

            // Then
            Actividad actividadGuardada = captor.getValue();
            assertEquals("dev-matias-001", actividadGuardada.getUserId());
            assertEquals("Migrar base de datos a MongoDB", actividadGuardada.getTitulo());
            assertEquals("Pasar esquemas de PostgreSQL a colecciones Mongo", actividadGuardada.getDescripcion());
        }

        @Test
        @DisplayName("Debe invocar repository.save() exactamente una vez durante la creación")
        void shouldCallSaveExactlyOnceWhenCreatingActivity() {
            // Given
            CrearActividadRequest request = new CrearActividadRequest(
                    "dev-004",
                    "Añadir métricas con Micrometer",
                    null
            );
            when(actividadRepository.save(any(Actividad.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // When
            crearActividad.execute(request);

            // Then
            verify(actividadRepository, times(1)).save(any(Actividad.class));
            verifyNoMoreInteractions(actividadRepository);
        }

        @Test
        @DisplayName("Debe retornar el id generado por MongoDB tal como lo devuelve repository.save()")
        void shouldReturnMongoGeneratedIdWhenRepositoryReturnsIt() {
            // Given
            String idGeneradoPorMongo = "665a1b2c3d4e5f6a7b8c9d0e";
            CrearActividadRequest request = new CrearActividadRequest(
                    "dev-005",
                    "Documentar endpoints con Swagger",
                    "Agregar @Operation y @ApiResponse a todos los controllers"
            );
            Actividad savedConId = buildActividad(
                    idGeneradoPorMongo, "dev-005",
                    "Documentar endpoints con Swagger",
                    "Agregar @Operation y @ApiResponse a todos los controllers",
                    EstadoActividad.PENDIENTE,
                    LocalDateTime.now()
            );
            when(actividadRepository.save(any(Actividad.class))).thenReturn(savedConId);

            // When
            ActividadResponse response = crearActividad.execute(request);

            // Then
            assertEquals(idGeneradoPorMongo, response.id(),
                    "El id del response debe ser el generado por MongoDB");
        }
    }

    // =========================================================================
    // Edge cases
    // =========================================================================

    @Nested
    @DisplayName("Casos borde")
    class CasosBorde {

        @Test
        @DisplayName("Debe crear la actividad correctamente cuando la descripción es null")
        void shouldCreateActivityWhenDescriptionIsNull() {
            // Given
            CrearActividadRequest request = new CrearActividadRequest(
                    "dev-006",
                    "Tarea sin descripción",
                    null   // descripcion opcional
            );
            ArgumentCaptor<Actividad> captor = ArgumentCaptor.forClass(Actividad.class);
            when(actividadRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            // When
            ActividadResponse response = crearActividad.execute(request);

            // Then
            Actividad actividadGuardada = captor.getValue();
            assertNull(actividadGuardada.getDescripcion(),
                    "La descripcion debe ser null cuando no se proporciona");
            assertEquals(EstadoActividad.PENDIENTE, actividadGuardada.getEstado());
            assertNotNull(response);
        }

        @Test
        @DisplayName("Debe asignar fechaCreacion dentro de un margen de 2 segundos respecto al momento de ejecución")
        void shouldAssignFechaCreacionWithinTwoSecondsOfExecutionWhenCreating() {
            // Given
            CrearActividadRequest request = new CrearActividadRequest(
                    "dev-007",
                    "Verificar tiempo de creación",
                    "Test de precisión temporal"
            );
            LocalDateTime antes = LocalDateTime.now();
            ArgumentCaptor<Actividad> captor = ArgumentCaptor.forClass(Actividad.class);
            when(actividadRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            // When
            crearActividad.execute(request);
            LocalDateTime despues = LocalDateTime.now().plusSeconds(2);

            // Then
            LocalDateTime fechaCreacion = captor.getValue().getFechaCreacion();
            assertFalse(fechaCreacion.isBefore(antes),
                    "fechaCreacion no debe ser anterior al inicio del test");
            assertFalse(fechaCreacion.isAfter(despues),
                    "fechaCreacion no debe superar el margen de 2 segundos");
        }

        @Test
        @DisplayName("Debe mapear correctamente el response cuando el id retornado por MongoDB es null")
        void shouldMapResponseWhenMongoReturnsActivityWithNullId() {
            // Given — MongoDB aún no asignó el id (situación transitoria)
            CrearActividadRequest request = new CrearActividadRequest(
                    "dev-008",
                    "Tarea transitoria",
                    "Descripción"
            );
            Actividad sinId = buildActividad(
                    null, "dev-008", "Tarea transitoria",
                    "Descripción", EstadoActividad.PENDIENTE, LocalDateTime.now()
            );
            when(actividadRepository.save(any(Actividad.class))).thenReturn(sinId);

            // When
            ActividadResponse response = crearActividad.execute(request);

            // Then
            assertNotNull(response);
            assertNull(response.id(),
                    "El response debe reflejar el id null que devolvió el repositorio");
        }

        @Test
        @DisplayName("Debe no invocar ningún otro método del repositorio además de save()")
        void shouldOnlyCallSaveAndNoOtherRepositoryMethodsWhenCreating() {
            // Given
            CrearActividadRequest request = new CrearActividadRequest(
                    "dev-009",
                    "Verificar aislamiento del repositorio",
                    "Solo debe llamarse save"
            );
            when(actividadRepository.save(any(Actividad.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // When
            crearActividad.execute(request);

            // Then
            verify(actividadRepository).save(any(Actividad.class));
            verifyNoMoreInteractions(actividadRepository);
        }
    }

    // =========================================================================
    // Helper
    // =========================================================================

    private Actividad buildActividad(String id, String userId, String titulo,
                                     String descripcion, EstadoActividad estado,
                                     LocalDateTime fechaCreacion) {
        Actividad a = new Actividad();
        a.setId(id);
        a.setUserId(userId);
        a.setTitulo(titulo);
        a.setDescripcion(descripcion);
        a.setEstado(estado);
        a.setFechaCreacion(fechaCreacion);
        return a;
    }
}
