package cl.sprint_rocket_ai.ms_checkpoint.schedulers.application;

import cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.domain.models.Actividad;
import cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.domain.ports.out.ActividadPersistencePortOut;
import cl.sprint_rocket_ai.ms_checkpoint.schedulers.domain.models.ResumenDiarioResult;
import cl.sprint_rocket_ai.ms_checkpoint.schedulers.domain.ports.out.IAEnginePortOut;
import cl.sprint_rocket_ai.ms_checkpoint.schedulers.domain.ports.out.NotificationPortOut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public final class GenerarResumenDiario {

    private static final Logger log = LoggerFactory.getLogger(GenerarResumenDiario.class);

    private final ActividadPersistencePortOut actividadPersistencePortOut;
    private final IAEnginePortOut iaEnginePortOut;
    private final NotificationPortOut notificationPortOut;

    public GenerarResumenDiario(ActividadPersistencePortOut actividadPersistencePortOut,
                                IAEnginePortOut iaEnginePortOut,
                                NotificationPortOut notificationPortOut) {
        this.actividadPersistencePortOut = actividadPersistencePortOut;
        this.iaEnginePortOut = iaEnginePortOut;
        this.notificationPortOut = notificationPortOut;
    }

    public void execute() {
        LocalDate ayer = LocalDate.now().minusDays(1);
        log.info("Iniciando generación de resumen diario | fecha='{}'", ayer);

        // 1. Obtener todas las actividades del día anterior (pendientes + completadas)
        List<Actividad> todasActividades = actividadPersistencePortOut.findByFecha(ayer);

        if (todasActividades.isEmpty()) {
            log.info("No se encontraron actividades para la fecha '{}'. Job omitido.", ayer);
            return;
        }

        log.info("Actividades encontradas: {} para la fecha '{}'", todasActividades.size(), ayer);

        // 2. Agrupar por usuario — solo usuarios con actividades reciben el resumen
        Map<String, List<Actividad>> porUsuario = todasActividades.stream()
                .collect(Collectors.groupingBy(Actividad::getUserId));

        log.info("Procesando resumen para {} desarrolladores", porUsuario.size());

        // 3. Procesar cada usuario individualmente
        porUsuario.forEach((userId, actividades) -> {
            log.info("Procesando resumen diario | userId='{}' actividades={}", userId, actividades.size());
            try {
                // 4. Llamar a IA-ENGINE para generar resumen + sugerencias
                ResumenDiarioResult resultado = iaEnginePortOut.generateDailySummary(actividades, userId, ayer);

                // 5. Notificar al desarrollador con el resumen estructurado (correo via log)
                notificationPortOut.notifyDeveloperWithSummary(
                        userId,
                        resultado.resumen(),
                        resultado.sugerencias()
                );

                log.info("Resumen diario enviado exitosamente | userId='{}' sugerencias={}",
                        userId, resultado.sugerencias().size());
            } catch (Exception e) {
                log.error("Error al procesar resumen diario | userId='{}': {}", userId, e.getMessage(), e);
            }
        });

        log.info("Proceso de generación de resúmenes diarios finalizado | fecha='{}'", ayer);
    }
}
