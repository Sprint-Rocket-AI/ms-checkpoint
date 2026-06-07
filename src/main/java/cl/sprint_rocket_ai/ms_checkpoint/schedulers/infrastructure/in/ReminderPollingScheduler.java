package cl.sprint_rocket_ai.ms_checkpoint.schedulers.infrastructure.in;

import cl.sprint_rocket_ai.ms_checkpoint.commons.domain.enums.DiaSemana;
import cl.sprint_rocket_ai.ms_checkpoint.commons.domain.enums.TipoRecordatorio;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.domain.events.ReminderTriggeredEvent;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.domain.models.Recordatorio;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.domain.ports.out.RecordatorioPersistencePortOut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
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

    /**
     * Contador de disparos por recordatorio en memoria.
     * Se reinicia al reiniciar el servidor (no persiste).
     */
    private final ConcurrentHashMap<String, Integer> reminderCountMap = new ConcurrentHashMap<>();

    private final RecordatorioPersistencePortOut recordatorioPersistencePortOut;
    private final ApplicationEventPublisher eventPublisher;

    public ReminderPollingScheduler(RecordatorioPersistencePortOut recordatorioPersistencePortOut,
                                    ApplicationEventPublisher eventPublisher) {
        this.recordatorioPersistencePortOut = recordatorioPersistencePortOut;
        this.eventPublisher = eventPublisher;
    }

    @Scheduled(fixedDelayString = "${schedulers.reminder-polling.fixed-delay:30000}")
    public void poll() {
        log.debug("Polling de recordatorios vencidos iniciado");

        List<Recordatorio> vencidos = recordatorioPersistencePortOut.findVencidos();

        if (vencidos.isEmpty()) {
            log.debug("Sin recordatorios vencidos en este ciclo");
            return;
        }

        log.info("Recordatorios vencidos encontrados: {}", vencidos.size());

        for (Recordatorio recordatorio : vencidos) {
            try {
                procesarVencido(recordatorio);
            } catch (Exception e) {
                log.error("Error procesando recordatorio vencido | id='{}': {}",
                        recordatorio.getId(), e.getMessage(), e);
            }
        }
    }

    // ─── Lógica interna ──────────────────────────────────────────────────────────

    private void procesarVencido(Recordatorio recordatorio) {
        String id = recordatorio.getId();

        // 1. Verificar si aplica para el día actual (recordatorios semanales/custom)
        if (!aplicaHoy(recordatorio)) {
            log.debug("Recordatorio no aplica para hoy | id='{}' diasSemana='{}'",
                    id, recordatorio.getDiasSemana());
            // Actualizar proximoEnvio para no seguir apareciendo en el poll
            actualizarProximoEnvio(recordatorio);
            recordatorioPersistencePortOut.save(recordatorio);
            return;
        }

        // 2. Incrementar contador de disparos
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
                && LocalDate.now().isAfter(recordatorio.getFechaExpiracion())) {
            recordatorio.setActivo(false);
            reminderCountMap.remove(id);
            log.info("Recordatorio desactivado por expiración | id='{}' fechaExpiracion='{}'",
                    id, recordatorio.getFechaExpiracion());
        }

        // 6. Persistir cambios
        recordatorio.setFechaActualizacion(LocalDateTime.now());
        recordatorioPersistencePortOut.save(recordatorio);
    }

    /**
     * Verifica si el recordatorio aplica para el día de la semana actual.
     * Los de tipo DIARIO y HORA_POR_HORA siempre aplican.
     * Los de tipo SEMANAL y CUSTOM verifican la lista de {@code diasSemana}.
     */
    private boolean aplicaHoy(Recordatorio recordatorio) {
        if (recordatorio.getDiasSemana() == null || recordatorio.getDiasSemana().isEmpty()) {
            return true; // Sin restricción de días → siempre aplica
        }
        DiaSemana hoy = mapDayOfWeek(LocalDate.now().getDayOfWeek());
        return recordatorio.getDiasSemana().contains(hoy);
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
        TipoRecordatorio tipo = recordatorio.getTipoRecordatorio();
        LocalDateTime ahora = LocalDateTime.now();

        LocalDateTime siguiente = switch (tipo) {
            case DIARIO -> ahora.plusDays(1)
                    .withHour(parseHora(recordatorio.getHoraActivacion()))
                    .withMinute(parseMinuto(recordatorio.getHoraActivacion()))
                    .withSecond(0).withNano(0);
            case HORA_POR_HORA -> ahora.plusHours(1).withSecond(0).withNano(0);
            case SEMANAL -> ahora.plusWeeks(1)
                    .withHour(parseHora(recordatorio.getHoraActivacion()))
                    .withMinute(parseMinuto(recordatorio.getHoraActivacion()))
                    .withSecond(0).withNano(0);
            default -> {
                // CUSTOM / EVENTO: disparo único, desactivar
                recordatorio.setActivo(false);
                log.info("Recordatorio tipo {} desactivado tras disparo único | id='{}'",
                        tipo, recordatorio.getId());
                yield ahora;
            }
        };

        recordatorio.setProximoEnvio(siguiente);
        log.debug("Próximo envío calculado | id='{}' tipo='{}' siguiente='{}'",
                recordatorio.getId(), tipo, siguiente);
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
