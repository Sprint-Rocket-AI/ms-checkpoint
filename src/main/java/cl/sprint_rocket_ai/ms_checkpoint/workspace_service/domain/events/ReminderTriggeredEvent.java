package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.events;

import org.springframework.context.ApplicationEvent;

public class ReminderTriggeredEvent extends ApplicationEvent {

    private final String recordatorioId;
    private final String userId;
    private final String titulo;
    private final int reminderCount;

    public ReminderTriggeredEvent(Object source,
                                  String recordatorioId,
                                  String userId,
                                  String titulo,
                                  int reminderCount) {
        super(source);
        this.recordatorioId = recordatorioId;
        this.userId = userId;
        this.titulo = titulo;
        this.reminderCount = reminderCount;
    }

    public String getRecordatorioId() {
        return recordatorioId;
    }

    public String getUserId() {
        return userId;
    }

    public String getTitulo() {
        return titulo;
    }

    public int getReminderCount() {
        return reminderCount;
    }
}
