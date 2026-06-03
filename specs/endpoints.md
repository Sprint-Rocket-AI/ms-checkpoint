✔️ CRUD Actividades:

    POST /api/actividades - crear
    GET /api/actividades/{id} - obtener
    PUT /api/actividades/{id} - actualizar
    GET /api/actividades/desarrollador/{id} - listar con filtro
    DELETE /api/actividades/{id} - eliminar


✔️ CRUD Recordatorios:

    POST /api/recordatorios - crear
    GET /api/recordatorios/{id} - obtener
    PUT /api/recordatorios/{id} - actualizar
    GET /api/recordatorios/desarrollador/{id} - listar
    DELETE /api/recordatorios/{id} - eliminar

EJEMPLO DE INTERACCION CON MS :

Crear actividad diaria
POST /api/actividades
{
  "devId": "dev-matias-001",
  "titulo": "Implementar autenticación OAuth2",
  "tipo": "TAREA",
  "prioridad": "ALTA",
  "ticketJira": "SPRINT-1234",
  "fechaVencimiento": "2024-06-15",
  "etiquetas": ["backend", "autenticacion"],
  "notas": "..."
}

Crear recordatorio con cron
POST /api/actividades
{
  "devId": "dev-matias-001",
  "titulo": "Implementar autenticación OAuth2",
  "tipo": "TAREA",
  "prioridad": "ALTA",
  "ticketJira": "SPRINT-1234",
  "fechaVencimiento": "2024-06-15",
  "etiquetas": ["backend", "autenticacion"],
  "notas": "..."
}

postergar tarea
PUT /api/recordatorios/{id}
{
  "proximoEnvio": "2024-05-30T14:30:00"
}

Detener recordatorio
PUT /api/recordatorios/{id}
{
  "isActivo": false
}