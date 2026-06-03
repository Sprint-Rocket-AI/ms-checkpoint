✔️ Slice gestionar_actividades:

    CrearActividad - POST nueva actividad
    ActualizarActividad - PUT actualizar estado/horas
    ObtenerActividadById - GET actividad
    ListarActividadesByDesarrollador - GET con filtro estado
    EliminarActividad - DELETE


✔️ Slice gestionar_recordatorios:

    CrearRecordatorio - Crear alarma recurrente
    ActualizarRecordatorio - Modificar configuración
    ObtenerRecordatorioById, ListarRecordatorios, EliminarRecordatorio


✔️ Jobs Programados:

    Job 8:30 AM (L-V): Obtiene actividades finalizadas del día anterior → IA-ENGINE genera resumen → notifica developer
    Job cada hora: Obtiene 3 actividades pendientes más importantes → IA-ENGINE genera pop-up → envía notificación