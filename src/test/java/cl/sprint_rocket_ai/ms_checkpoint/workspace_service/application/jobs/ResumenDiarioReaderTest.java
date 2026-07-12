package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.jobs;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.enums.EstadoActividad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Actividad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Usuario;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.out.dto.UsuarioActividadesDto;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb.ActividadMongoRepository;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb.UsuarioMongoRepository;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.jobs.step_job.ResumenDiarioReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ResumenDiarioReader")
class ResumenDiarioReaderTest {

    @Mock
    private ActividadMongoRepository actividadRepository;

    @Mock
    private UsuarioMongoRepository usuarioRepository;

    @InjectMocks
    private ResumenDiarioReader resumenDiarioReader;

    private Actividad buildActividad(String id, String userId) {
        Actividad a = new Actividad();
        a.setId(id);
        a.setUserId(userId);
        a.setTitulo("Actividad " + id);
        a.setEstado(EstadoActividad.PENDIENTE);
        a.setFechaCreacion(LocalDateTime.now().minusDays(1));
        return a;
    }

    private Usuario buildUsuario(String userId, String correo) {
        return new Usuario(userId, correo);
    }

    @Nested
    @DisplayName("Cuando hay actividades para la fecha dada")
    class CuandoHayActividades {

        @Test
        @DisplayName("Debe buscar actividades usando el rango inicio-fin del día indicado")
        void shouldSearchActivitiesUsingFullDayRange() {
            // Given
            LocalDate fecha = LocalDate.now().minusDays(1);
            LocalDateTime expectedDesde = fecha.atStartOfDay();
            LocalDateTime expectedHasta = fecha.atTime(LocalTime.MAX);

            Actividad actividad = buildActividad("act-1", "user-1");
            when(actividadRepository.findByFechaCreacionBetween(expectedDesde, expectedHasta))
                    .thenReturn(List.of(actividad));
            when(usuarioRepository.findByUserId("user-1"))
                    .thenReturn(Optional.of(buildUsuario("user-1", "user1@empresa.com")));

            // When
            resumenDiarioReader.read(fecha);

            // Then
            verify(actividadRepository).findByFechaCreacionBetween(expectedDesde, expectedHasta);
        }

        @Test
        @DisplayName("Debe agrupar actividades por userId y retornar un UsuarioActividadesDto por usuario")
        void shouldGroupActivitiesByUserIdAndReturnOneDtoPerUser() {
            // Given
            LocalDate fecha = LocalDate.now().minusDays(1);
            List<Actividad> actividades = List.of(
                    buildActividad("act-1", "user-1"),
                    buildActividad("act-2", "user-1"),
                    buildActividad("act-3", "user-2")
            );
            when(actividadRepository.findByFechaCreacionBetween(any(), any())).thenReturn(actividades);
            when(usuarioRepository.findByUserId("user-1"))
                    .thenReturn(Optional.of(buildUsuario("user-1", "u1@empresa.com")));
            when(usuarioRepository.findByUserId("user-2"))
                    .thenReturn(Optional.of(buildUsuario("user-2", "u2@empresa.com")));

            // When
            List<UsuarioActividadesDto> resultado = resumenDiarioReader.read(fecha);

            // Then
            assertEquals(2, resultado.size());
            UsuarioActividadesDto dtoUser1 = resultado.stream()
                    .filter(d -> d.userId().equals("user-1")).findFirst().orElseThrow();
            assertEquals(2, dtoUser1.actividades().size());

            UsuarioActividadesDto dtoUser2 = resultado.stream()
                    .filter(d -> d.userId().equals("user-2")).findFirst().orElseThrow();
            assertEquals(1, dtoUser2.actividades().size());
        }

        @Test
        @DisplayName("Debe buscar el correo de cada usuario en usuarioRepository")
        void shouldFetchEmailForEachUser() {
            // Given
            LocalDate fecha = LocalDate.now().minusDays(1);
            List<Actividad> actividades = List.of(
                    buildActividad("act-1", "user-A"),
                    buildActividad("act-2", "user-B")
            );
            when(actividadRepository.findByFechaCreacionBetween(any(), any())).thenReturn(actividades);
            when(usuarioRepository.findByUserId("user-A"))
                    .thenReturn(Optional.of(buildUsuario("user-A", "a@empresa.com")));
            when(usuarioRepository.findByUserId("user-B"))
                    .thenReturn(Optional.of(buildUsuario("user-B", "b@empresa.com")));

            // When
            resumenDiarioReader.read(fecha);

            // Then
            verify(usuarioRepository).findByUserId("user-A");
            verify(usuarioRepository).findByUserId("user-B");
        }

        @Test
        @DisplayName("Debe incluir el correo correcto en cada UsuarioActividadesDto")
        void shouldIncludeCorrectEmailInEachDto() {
            // Given
            LocalDate fecha = LocalDate.now().minusDays(1);
            List<Actividad> actividades = List.of(buildActividad("act-1", "user-1"));
            when(actividadRepository.findByFechaCreacionBetween(any(), any())).thenReturn(actividades);
            when(usuarioRepository.findByUserId("user-1"))
                    .thenReturn(Optional.of(buildUsuario("user-1", "sebastian@empresa.com")));

            // When
            List<UsuarioActividadesDto> resultado = resumenDiarioReader.read(fecha);

            // Then
            assertEquals(1, resultado.size());
            assertEquals("user-1", resultado.get(0).userId());
            assertEquals("sebastian@empresa.com", resultado.get(0).correo());
        }

        @Test
        @DisplayName("Debe usar correo fallback cuando el usuario no existe en el repositorio")
        void shouldUseFallbackEmailWhenUserNotFound() {
            // Given
            LocalDate fecha = LocalDate.now().minusDays(1);
            List<Actividad> actividades = List.of(buildActividad("act-1", "user-sin-registro"));
            when(actividadRepository.findByFechaCreacionBetween(any(), any())).thenReturn(actividades);
            when(usuarioRepository.findByUserId("user-sin-registro")).thenReturn(Optional.empty());

            // When
            List<UsuarioActividadesDto> resultado = resumenDiarioReader.read(fecha);

            // Then
            assertEquals(1, resultado.size());
            assertEquals("sin_correo@empresa.com", resultado.get(0).correo());
        }

        @Test
        @DisplayName("Debe retornar todas las actividades del usuario dentro del UsuarioActividadesDto")
        void shouldReturnAllUserActivitiesInsideDto() {
            // Given
            LocalDate fecha = LocalDate.now().minusDays(1);
            List<Actividad> actividades = List.of(
                    buildActividad("act-1", "user-X"),
                    buildActividad("act-2", "user-X"),
                    buildActividad("act-3", "user-X")
            );
            when(actividadRepository.findByFechaCreacionBetween(any(), any())).thenReturn(actividades);
            when(usuarioRepository.findByUserId("user-X"))
                    .thenReturn(Optional.of(buildUsuario("user-X", "x@empresa.com")));

            // When
            List<UsuarioActividadesDto> resultado = resumenDiarioReader.read(fecha);

            // Then
            assertEquals(1, resultado.size());
            assertEquals(3, resultado.get(0).actividades().size());
        }
    }

    @Nested
    @DisplayName("Cuando no hay actividades para la fecha dada")
    class CuandoNoHayActividades {

        @Test
        @DisplayName("Debe retornar lista vacía sin consultar usuarioRepository")
        void shouldReturnEmptyListWithoutQueryingUserRepository() {
            // Given
            LocalDate fecha = LocalDate.now().minusDays(1);
            when(actividadRepository.findByFechaCreacionBetween(any(), any())).thenReturn(List.of());

            // When
            List<UsuarioActividadesDto> resultado = resumenDiarioReader.read(fecha);

            // Then
            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
            verifyNoInteractions(usuarioRepository);
        }
    }
}
