package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.out;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.SugerenciaActividad;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public final class MailResponseBuilder {

    public String buildDailySummary(String userId, String resumen, List<SugerenciaActividad> sugerencias){

        StringBuilder body = new StringBuilder();

        body.append("<div style='font-family: Arial, sans-serif; color: #333333; line-height: 1.6; max-width: 600px; margin: 0 auto;'>");
        body.append("<h2 style='color: #2c3e50;'>¡Hola, ").append(userId).append("! 👋</h2>");
        body.append("<p>Aquí tienes el resumen ejecutivo de tus actividades del día para ayudarte a preparar tu jornada:</p>");

        body.append("<div style='background-color: #f8f9fa; border-left: 4px solid #007bff; padding: 15px; margin: 20px 0; border-radius: 4px;'>");
        body.append("<h3 style='margin-top: 0; color: #007bff;'>📋 Listo para tu Daily</h3>");
        body.append("<p style='white-space: pre-line; margin-bottom: 0;'>").append(resumen).append("</p>");
        body.append("</div>");

        body.append("<h3 style='color: #2c3e50; border-bottom: 1px solid #eeeeee; padding-bottom: 8px;'>💡 Actividades sugeridas para HOY</h3>");

        if (sugerencias == null || sugerencias.isEmpty()) {
            body.append("<p style='color: #666666; font-style: italic;'>No se generaron sugerencias automáticas para hoy. ¡Buen ritmo!</p>");
        } else {
            body.append("<ul style='list-style-type: none; padding-left: 0;'>");
            for (SugerenciaActividad s : sugerencias) {
                String badgeColor = "ALTA".equalsIgnoreCase(s.prioridad()) ? "#dc3545" :
                        "MEDIA".equalsIgnoreCase(s.prioridad()) ? "#ffc107" : "#28a745";
                String textColor = "MEDIA".equalsIgnoreCase(s.prioridad()) ? "#212529" : "#ffffff";

                body.append("<li style='margin-bottom: 20px; border-bottom: 1px dashed #e9ecef; padding-bottom: 15px;'>");
                body.append("<span style='background-color: ").append(badgeColor)
                        .append("; color: ").append(textColor)
                        .append("; padding: 3px 8px; border-radius: 3px; font-size: 12px; font-weight: bold; margin-right: 10px;'>")
                        .append(s.prioridad()).append("</span> ");
                body.append("<strong style='font-size: 16px; color: #2c3e50;'>").append(s.titulo()).append("</strong>");
                body.append("<p style='margin: 8px 0 4px 0; color: #555555;'>").append(s.descripcion()).append("</p>");
                body.append("<p style='margin: 0; font-size: 13px; color: #7f8c8d;'><strong>Razón:</strong> ").append(s.razon()).append("</p>");
                body.append("</li>");
            }
            body.append("</ul>");
        }

        body.append("<hr style='border: 0; border-top: 1px solid #eeeeee; margin-top: 30px;'>");
        body.append("<p style='font-size: 12px; color: #95a5a6; text-align: center;'>");
        body.append("Este correo fue generado automáticamente por <strong>SpringRocket IA</strong>.<br>");
        body.append("Por favor, no respondas a este mensaje.");
        body.append("</p>");
        body.append("</div>");

        return body.toString();
    }
}
