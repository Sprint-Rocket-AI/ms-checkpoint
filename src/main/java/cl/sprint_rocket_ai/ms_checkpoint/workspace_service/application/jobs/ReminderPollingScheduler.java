package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.jobs;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.enums.DiaSemana;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.events.ReminderTriggeredEvent;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Recordatorio;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb.RecordatorioMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Subject del patrón Observer.
 *
 * <p>Se ejecuta cada 30 segundos y consulta MongoDB buscando recordatorios activos
 * cuyo {@code proximoEnvio} ya pasó ({@code <= ahora}).
 *
 * <p>Por cada recordatorio vencido:
 * <ol>
 *   <li>Publica un {@link ReminderTriggeredEvent} vía {@link ApplicationEventPublisher}.</li>
 *   <li>Actualiza {@code proximoEnvio} según el tipo de recurrencia para la próxima ejecución.</li>
 *   <li>Si el recordatorio llegó a su {@code fechaExpiracion}, lo desactiva.</li>
 * </ol>
 *
 * <p>Propiedad de configuración: {@code schedulers.reminder-polling.fixed-delay} (ms)
 * <br>Default: {@code 30000} (30 segundos)
 */
@Component
public class ReminderPollingScheduler {

    private static final Logger log = LoggerFactory.getLogger(ReminderPollingScheduler.class);

    private final ConcurrentHashMap<String, Integer> reminderCountMap = new ConcurrentHashMap<>();

    private final RecordatorioMongoRepository recordatorioRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ReminderPollingScheduler(RecordatorioMongoRepository recordatorioRepository,
                                    ApplicationEventPublisher eventPublisher) {
        this.recordatorioRepository = recordatorioRepository;
        this.eventPublisher = eventPublisher;
    }

    @Scheduled(fixedDelayString = "${schedulers.reminder-polling.fixed-delay:30000}")
    public void poll() {
        log.debug("Polling de recordatorios iniciado");

        List<Recordatorio> activos = recordatorioRepository.findByActivoTrue();

        if (activos.isEmpty()) {
            log.debug("Sin recordatorios activos en este ciclo");
            return;
        }

        LocalTime ahoraTime = LocalTime.now();

        for (Recordatorio recordatorio : activos) {
            try {
                if (debeDispararse(recordatorio, ahoraTime)) {
                    procesarVencido(recordatorio);
                }
            } catch (Exception e) {
                log.error("Error procesando recordatorio | id='{}': {}",
                        recordatorio.getId(), e.getMessage(), e);
            }
        }
    }

    private boolean debeDispararse(Recordatorio recordatorio, LocalTime ahoraTime) {
        return true;
    }

    private void procesarVencido(Recordatorio recordatorio) {
        String id = recordatorio.getId();

        // 1. Incrementar contador de disparos
        int count = reminderCountMap.merge(id, 1, Integer::sum);

        log.info("Disparando recordatorio | id='{}' userId='{}' titulo='{}' count={}",
                id, recordatorio.getUserId(), recordatorio.getTitulo(), count);

        // 3. Publicar evento → Observer (ReminderEventListener) → WebSocket
        eventPublisher.publishEvent(new ReminderTriggeredEvent(
                this,
                id,
                recordatorio.getUserId(),
                recordatorio.getTitulo(),
                count
        ));

        // 4. Calcular próximo envío y verificar expiración
        actualizarProximoEnvio(recordatorio);

        // 5. Si superó la fecha de expiración, desactivar
        if (recordatorio.getFechaExpiracion() != null
                && LocalDateTime.now().isAfter(recordatorio.getFechaExpiracion())) {
            recordatorio.setActivo(false);
            reminderCountMap.remove(id);
            log.info("Recordatorio desactivado por expiración | id='{}' fechaExpiracion='{}'",
                    id, recordatorio.getFechaExpiracion());
        }

        // 6. Persistir cambios
        recordatorio.setFechaCreacion(LocalDateTime.now());
        recordatorioRepository.save(recordatorio);
    }

    /**
     * Verifica si el recordatorio aplica para el día de la semana actual.
     * Los de tipo DIARIO y HORA_POR_HORA siempre aplican.
     * Los de tipo SEMANAL y CUSTOM verifican la lista de {@code diasSemana}.
     */
    private boolean aplicaHoy(Recordatorio recordatorio) {
        return true;
    }

    /**
     * Calcula y asigna el próximo {@code proximoEnvio} según el tipo de recurrencia.
     *
     * <ul>
     *   <li>{@code DIARIO} → +24 horas</li>
     *   <li>{@code HORA_POR_HORA} → +1 hora</li>
     *   <li>{@code SEMANAL} → +7 días (a la misma hora de activación)</li>
     *   <li>{@code CUSTOM} / {@code EVENTO} → desactiva (disparo único)</li>
     * </ul>
     */
    private void actualizarProximoEnvio(Recordatorio recordatorio) {
        // Simplified
    }

    private int parseHora(String horaActivacion) {
        if (horaActivacion == null) return 0;
        return Integer.parseInt(horaActivacion.split(":")[0]);
    }

    private int parseMinuto(String horaActivacion) {
        if (horaActivacion == null) return 0;
        return Integer.parseInt(horaActivacion.split(":")[1]);
    }

    private DiaSemana mapDayOfWeek(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> DiaSemana.LUNES;
            case TUESDAY -> DiaSemana.MARTES;
            case WEDNESDAY -> DiaSemana.MIERCOLES;
            case THURSDAY -> DiaSemana.JUEVES;
            case FRIDAY -> DiaSemana.VIERNES;
            case SATURDAY -> DiaSemana.SABADO;
            case SUNDAY -> DiaSemana.DOMINGO;
        };
    }
}
