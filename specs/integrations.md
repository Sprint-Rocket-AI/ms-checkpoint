✔️ Scheduler 1: GenerarResumenActividadesFinalizadasScheduler

    Cron: 0 30 8 * * MON-FRI (8:30 AM L-V)
    Flujo: Obtiene actividades completadas SYSDATE-1 → IA-ENGINE → persiste + notifica


✔️ Scheduler 2: NotificarActividadesPendientesScheduler

    Cron: 0 0 * * * * (cada hora)
    Flujo: Obtiene top 3 actividades pendientes → IA-ENGINE genera pop-up → envía


✔️ Nuevos DTOs para IA-ENGINE:

    GenerarPopUpRequest - solicitud de pop-up con actividades
    PopUpResponse - respuesta JSON renderizable
    ActividadParaPopUp - actividad simplificada para pop-up


✔️ Configuración:

    Properties de schedulers
    Monitoreo y reportes
    Manejo de errores en jobs