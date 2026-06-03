✔️ Modelo Actividad - Tareas granulares del día (PENDIENTE, EN_PROCESO, COMPLETADA)
    EJEMPLO ATRIBUTOS : 
        
        "userId": "dev-matias-001",
        "titulo": "Implementar autenticación OAuth2",
        "tipo": "TAREA",
        "prioridad": "ALTA",
        "ticketJira": "SPRINT-1234",
        "fechaVencimiento": "2024-06-15",
        "etiquetas": ["backend", "autenticacion"],
        "notas": "..."
✔️ Modelo Recordatorio - Alarmas/notificaciones programadas (DIARIO, HORA_POR_HORA, CUSTOM, SEMANAL)
    EJEMPLO ATRIBUTOS :
        "userId": "dev-matias-001",
        "titulo": "Sincronización matutina",
        "tipoRecordatorio": "DIARIO",
        "horaActivacion": "08:30",
        "diasSemana": ["LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES"],
        "activo":true
        "fechaExpiracion": "2024-12-31"

✔️ Nuevos Enums:
    EstadoActividad (PENDIENTE, EN_PROCESO, COMPLETADA, CANCELADA)
    TipoRecordatorio (DIARIO, HORA_POR_HORA, CUSTOM, EVENTO, SEMANAL)
    DiasSemana (LUNES-DOMINGO)