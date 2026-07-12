package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.out;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.SugerenciaActividad;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public final class NotificationJMS {

    private static final Logger log = LoggerFactory.getLogger(NotificationJMS.class);

    private final JavaMailSender mailSender;

    private final MailResponseBuilder mailResponseBuilder;

    @Value("${spring.mail.username:noreply@empresa.com}")
    private String fromEmail;

    public NotificationJMS(JavaMailSender mailSender, MailResponseBuilder mailResponseBuilder) {
        this.mailSender = mailSender;
        this.mailResponseBuilder = mailResponseBuilder;
    }

    public void notifyDeveloper(String userId, String contenido) {
        log.info("Enviando notificación al desarrollador: {}", userId);
        sendEmail(userId + "@empresa.com", "Notificación de SpringRocket", contenido);
        log.info("Notificación enviada exitosamente al desarrollador: {}", userId);
    }

    public void notifyDeveloperWithSummary(String userId, String correo, String resumen, List<SugerenciaActividad> sugerencias) {
        log.info("Preparando correo resumen diario para el desarrollador: {} a su correo {}", userId, correo);
        String response = mailResponseBuilder.buildDailySummary(userId, resumen, sugerencias);

        sendEmail(correo, "[SpringRocket] Resumen ejecutivo de actividades del día", response);

        log.info("Correo resumen diario enviado exitosamente al desarrollador: {}", userId);
    }

    private void sendEmail(String to, String subject, String text) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Error al enviar correo a {}: {}", to, e.getMessage());
        }
    }
}