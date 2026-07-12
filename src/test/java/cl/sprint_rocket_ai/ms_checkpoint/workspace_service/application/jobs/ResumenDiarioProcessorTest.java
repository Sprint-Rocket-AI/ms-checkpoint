package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.jobs;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.jobs.step_job.ResumenDiarioProcessor;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.enums.EstadoActividad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Actividad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.ResumenDiarioResult;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.SugerenciaActividad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.out.IAEngineRestClient;
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
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ResumenDiarioProcessor")
class ResumenDiarioProcessorTest {

    @Mock
    private IAEngineRestClient iaEngine;

    @InjectMocks
    private ResumenDiarioProcessor resumenDiarioProcessor;

    private Actividad buildActividad(String id, String userId) {
        Actividad a = new Actividad();
        a.setId(id);
        a.setUserId(userId);
        a.setTitulo("Actividad " + id);
        a.setEstado(EstadoActividad.COMPLETADA);
        a.setFechaCreacion(LocalDateTime.now().minusDays(1));
        return a;
    }

    private UsuarioActividadesDto buildUsuarioActividades(String userId, String correo, List<Actividad> actividades) {
        return new UsuarioActividadesDto(userId, correo, actividades);
    }

    private ResumenDiarioResult buildResultadoIA(String resumen) {
        List<SugerenciaActividad> sugerencias = List.of(
                new SugerenciaActividad("Tarea sugerida", "Descripción", "ALTA", "Por continuidad")
        );
        return new ResumenDiarioResult(resumen, sugerencias);
    }

    @Nested
    @DisplayName("Cuando el proceso es exitoso")
    class CuandoElProcesoEsExitoso {

        @Test
        @DisplayName("Debe llamar a iaEngine.generateDailySummary por cada usuario con sus actividades y fecha")
        void shouldCallIaEngineForEachUserWithActivitiesAndDate() {
            // Given
            LocalDate fecha = LocalDate.now().minusDays(1);
            List<Actividad> acts1 = List.of(buildActividad("act-1", "user-1"));
            List<Actividad> acts2 = List.of(buildActividad("act-2", "user-2"));
            List<UsuarioActividadesDto> datos = List.of(
                    buildUsuarioActividades("user-1", "u1@empresa.com", acts1),
                    buildUsuarioActividades("user-2", "u2@empresa.com", acts2)
            );
            when(iaEngine.generateDailySummary(acts1, "user-1", fecha))
                    .thenReturn(buildResultadoIA("Resumen user-1"));
            when(iaEngine.generateDailySummary(acts2, "user-2", fecha))
                    .thenReturn(buildResultadoIA("Resumen user-2"));

            // When
            resumenDiarioProcessor.process(datos, fecha);

            // Then
            verify(iaEngine).generateDailySummary(acts1, "user-1", fecha);
            verify(iaEngine).generateDailySummary(acts2, "user-2", fecha);
        }

        @Test
        @DisplayName("Debe retornar un ResumenUsuarioDto por cada usuario procesado")
        void shouldReturnOneResumenUsuarioDtoPerUser() {
            // Given
            LocalDate fecha = LocalDate.now().minusDays(1);
            List<Actividad> acts = List.of(buildActividad("act-1", "user-1"));
            List<UsuarioActividadesDto> datos = List.of(
                    buildUsuarioActividades("user-1", "u1@empresa.com", acts),
                    buildUsuarioActividades("user-2", "u2@empresa.com", List.of(buildActividad("act-2", "user-2")))
            );
            when(iaEngine.generateDailySummary(any(), eq("user-1"), eq(fecha)))
                    .thenReturn(buildResultadoIA("Resumen 1"));
            when(iaEngine.generateDailySummary(any(), eq("user-2"), eq(fecha)))
                    .thenReturn(buildResultadoIA("Resumen 2"));

            // When
            List<ResumenUsuarioDto> resultado = resumenDiarioProcessor.process(datos, fecha);

            // Then
            assertEquals(2, resultado.size());
        }

        @Test
        @DisplayName("Debe mapear correctamente userId, correo, resumen y sugerencias al ResumenUsuarioDto")
        void shouldMapUserIdCorreoResumenAndSugerenciasToDto() {
            // Given
            LocalDate fecha = LocalDate.now().minusDays(1);
            List<Actividad> acts = List.of(buildActividad("act-1", "user-1"));
            List<UsuarioActividadesDto> datos = List.of(
                    buildUsuarioActividades("user-1", "sebastian@empresa.com", acts)
            );
            ResumenDiarioResult resultadoIA = buildResultadoIA("Gran resumen del día");
            when(iaEngine.generateDailySummary(acts, "user-1", fecha)).thenReturn(resultadoIA);

            // When
            List<ResumenUsuarioDto> resultado = resumenDiarioProcessor.process(datos, fecha);

            // Then
            assertEquals(1, resultado.size());
            ResumenUsuarioDto dto = resultado.get(0);
            assertEquals("user-1", dto.userId());
            assertEquals("sebastian@empresa.com", dto.correo());
            assertEquals("Gran resumen del día", dto.resumen());
            assertEquals(1, dto.sugerencias().size());
            assertEquals("Tarea sugerida", dto.sugerencias().get(0).titulo());
        }

        @Test
        @DisplayName("Debe enviar la fecha correcta a iaEngine en cada llamada")
        void shouldSendCorrectDateToIaEngine() {
            // Given
            LocalDate fecha = LocalDate.of(2026, 7, 10);
            List<Actividad> acts = List.of(buildActividad("act-1", "user-1"));
            List<UsuarioActividadesDto> datos = List.of(
                    buildUsuarioActividades("user-1", "u@empresa.com", acts)
            );
            when(iaEngine.generateDailySummary(acts, "user-1", fecha))
                    .thenReturn(buildResultadoIA("Resumen"));

            // When
            resumenDiarioProcessor.process(datos, fecha);

            // Then
            verify(iaEngine).generateDailySummary(acts, "user-1", fecha);
        }

        @Test
        @DisplayName("Debe retornar lista vacía cuando no hay datos de entrada")
        void shouldReturnEmptyListWhenNoDatos() {
            // Given
            LocalDate fecha = LocalDate.now().minusDays(1);

            // When
            List<ResumenUsuarioDto> resultado = resumenDiarioProcessor.process(List.of(), fecha);

            // Then
            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
            verifyNoInteractions(iaEngine);
        }
    }

    @Nested
    @DisplayName("Cuando iaEngine lanza excepción para un usuario")
    class CuandoIaEngineLanzaExcepcion {

        @Test
        @DisplayName("Debe omitir el usuario fallido y continuar procesando los demás")
        void shouldSkipFailedUserAndContinueProcessingOthers() {
            // Given
            LocalDate fecha = LocalDate.now().minusDays(1);
            List<Actividad> actsError = List.of(buildActividad("act-err", "user-error"));
            List<Actividad> actsOk = List.of(buildActividad("act-ok", "user-ok"));
            List<UsuarioActividadesDto> datos = List.of(
                    buildUsuarioActividades("user-error", "error@empresa.com", actsError),
                    buildUsuarioActividades("user-ok", "ok@empresa.com", actsOk)
            );
            when(iaEngine.generateDailySummary(actsError, "user-error", fecha))
                    .thenThrow(new RuntimeException("Fallo de conexión con IA-ENGINE"));
            when(iaEngine.generateDailySummary(actsOk, "user-ok", fecha))
                    .thenReturn(buildResultadoIA("Resumen ok"));

            // When
            List<ResumenUsuarioDto> resultado = resumenDiarioProcessor.process(datos, fecha);

            // Then
            assertEquals(1, resultado.size());
            assertEquals("user-ok", resultado.get(0).userId());
        }

        @Test
        @DisplayName("Debe retornar lista vacía cuando todos los usuarios fallan")
        void shouldReturnEmptyListWhenAllUsersFail() {
            // Given
            LocalDate fecha = LocalDate.now().minusDays(1);
            List<Actividad> acts = List.of(buildActividad("act-1", "user-1"));
            List<UsuarioActividadesDto> datos = List.of(
                    buildUsuarioActividades("user-1", "u@empresa.com", acts)
            );
            when(iaEngine.generateDailySummary(any(), any(), any()))
                    .thenThrow(new RuntimeException("IA no disponible"));

            // When
            List<ResumenUsuarioDto> resultado = resumenDiarioProcessor.process(datos, fecha);

            // Then
            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }
    }
}
