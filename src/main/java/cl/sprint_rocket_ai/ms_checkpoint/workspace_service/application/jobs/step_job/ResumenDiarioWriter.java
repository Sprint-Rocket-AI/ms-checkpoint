package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.jobs.step_job;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.out.NotificationJMS;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.out.dto.ResumenUsuarioDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ResumenDiarioWriter {

    private static final Logger log = LoggerFactory.getLogger(ResumenDiarioWriter.class);

    private final NotificationJMS notificationJMS;

    public ResumenDiarioWriter(NotificationJMS notificationPortOut) {
        this.notificationJMS = notificationPortOut;
    }

    public void write(List<ResumenUsuarioDto> datos) {
        log.info("Writer: Enviando resúmenes para {} usuarios", datos.size());
        for (ResumenUsuarioDto dato : datos) {
            try {
                notificationJMS.notifyDeveloperWithSummary(
                        dato.userId(),
                        dato.correo(),
                        dato.resumen(),
                        dato.sugerencias()
                );
                log.info("Writer: Resumen enviado exitosamente a {}", dato.correo());
            } catch (Exception e) {
                log.error("Writer: Error al enviar resumen a {}: {}", dato.correo(), e.getMessage(), e);
            }
        }
    }
}
