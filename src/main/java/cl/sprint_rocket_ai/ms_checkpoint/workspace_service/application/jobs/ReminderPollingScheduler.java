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

    private void actualizarProximoEnvio(Recordatorio recordatorio) {
        // Simplified
    }

}
