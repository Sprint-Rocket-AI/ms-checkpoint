# ms-checkpoint — Spec Arquitectónico

Documento normativo. Define las decisiones técnicas que todo código nuevo debe respetar.

---

## 1. Estilo arquitectónico

**Vertical Slice + Hexagonal "directa" dentro de cada slice.**

- **Vertical Slice puro:** el paquete raíz es la **feature** (`registrar_avance`, `generar_resumen`, `consultar_avance`, `notificar_lider`), no la capa. Cada slice contiene su propio `domain/`, `application/` e `infrastructure/`.
- **Slice autocontenido:** un slice solo puede importar de su propio paquete y de `commons/`. Nunca de otro slice.
- **`commons/`:** solo lo que comparten ≥ 2 slices (`Avance` base, `EstadoAvance`, `TipoTarea`, `EstadoResumen`, `EntityNotFoundException`, `GlobalExceptionHandler`). Si una clase la usa **un solo** slice, vive dentro de ese slice.
- **Hexagonal directa:** dentro del slice NO hay puertos de entrada. El controller REST inyecta directamente los casos de uso de `application`.
- **Puertos de salida sí:** interfaces en `<slice>/domain/ports/out/` para desacoplar `application` de la infraestructura (persistencia, IA-ENGINE, notificaciones).

### Estructura de paquetes

```
cl.sprint_rocket_ai.ms_checkpoint
│
├── commons/                                    ← compartido entre slices
│   ├── domain/
│   │   ├── enums/                              (EstadoAvance, TipoTarea, EstadoResumen)
│   │   ├── exceptions/                         (EntityNotFoundException, AvanceNotFoundException)
│   │   └── models/                             (Avance – clase base abstracta)
│   └── infrastructure/                         (GlobalExceptionHandler)
│
└── <slice>/                                    ← un paquete por feature
    ├── domain/
    │   ├── enums/                              (enums propios del slice)
    │   ├── models/                             (modelos + value objects embebidos)
    │   └── ports/out/
    │       ├── AvancePersistencePortOut.java   (persistencia)
    │       ├── IAEnginePortOut.java            (integración IA)
    │       └── NotificationPortOut.java        (notificaciones)
    ├── application/                            (1 clase = 1 caso de uso)
    └── infrastructure/
        ├── in/
        │   ├── AvanceRest.java                 (interfaz Swagger)
        │   ├── AvanceController.java           (@RestController + verbos)
        │   └── dtos/                           (Request/Response records)
        ├── out/
        │   ├── AvancePersistenceAdapterOut.java
        │   ├── IAEngineAdapterOut.java
        │   └── NotificationAdapterOut.java
        └── persistences/mongodb/AvanceMongoRepository.java
```

---

## 2. Reglas de dependencia

| Desde \ Hacia              | commons | propio slice | otro slice |
|----------------------------|:-------:|:------------:|:----------:|
| Cualquier capa de un slice |   ✅    |      ✅      |     ❌     |
| `commons`                  |   ✅    |      ❌      |     ❌     |

Dentro de un slice, entre capas:

| Desde \ Hacia          | domain | application | infra.dtos | infra.in | infra.out | infra.persistences |
|------------------------|:------:|:-----------:|:----------:|:--------:|:---------:|:------------------:|
| **domain**             |   ✅   |     ❌      |     ❌     |    ❌    |    ❌     |         ❌         |
| **application**        |   ✅   |     ✅      |     ✅     |    ❌    |    ❌     |         ❌         |
| **infra.in**           |   ✅   |     ✅      |     ✅     |    ✅    |    ❌     |         ❌         |
| **infra.out**          |   ✅   |     ❌      |     ❌     |    ❌    |    ✅     |         ✅         |

Claves:
1. `domain` no depende de nada fuera de sí mismo (ni Spring, ni DTOs).
2. `application` conoce los DTOs de infraestructura (recibe `*Request`, devuelve `*Response`), pero **NO** conoce el adapter ni el repositorio.
3. `application` depende sólo de las interfaces `<slice>.domain.ports.out.*`.
4. No hay puertos de entrada: el controller llama directo al caso de uso.
5. `commons` jamás importa código de un slice.

---

## 3. Naming

- **Atributos:** SIEMPRE en **español** (`titulo`, `desarrolladorId`, `fechaCreacion`, `descripcion`, `estado`, `horasEstimadas`, `tareas`).
- **Métodos:** SIEMPRE en **inglés** (`execute`, `save`, `findById`, `applyTo`, `from`, `toDomain`, `generateSummary`).
- **Casos de uso:** verbo inglés + entidad español → `RegistrarAvance`, `GenerarResumenEjecutivo`, `ObtenerAvanceById`, `ListarAvanceByDesarrollador`, `ActualizarAvance`, `NotificarLider`.
- **Clases:** `<Entidad>Rest`, `<Entidad>Controller`, `<Entidad>AdapterOut`, `<Entidad>PortOut`, `<Entidad>MongoRepository`, `<Entidad>Request`, `<Entidad>Response`.
- **Colecciones Mongo:** snake_case en plural (`avances`, `resumenes_ejecutivos`, `notificaciones`).
- **Campos persistidos:** `@Field("snake_case")` sobre atributo camelCase español.
- **Endpoints:** kebab-case en plural (`/api/avances`, `/api/resumenes-ejecutivos`, `/api/notificaciones`).
- **Enums:** valores en MAYÚSCULAS español (`EN_CURSO`, `COMPLETADA`, `BLOQUEADA`, `PENDIENTE`, `ENVIADO`, `FALLIDO`).
- **Paquete del slice:** snake_case (`registrar_avance`, `generar_resumen`, `consultar_avance`, `notificar_lider`).

---

## 4. Capa `domain` (por slice)

- **Modelos:** POJOs anémicos con getters/setters. Se aceptan anotaciones Mongo (`@Document`, `@Id`, `@Field`).
- Jerarquía: `commons.domain.models.Avance` abstracta + subclase por slice si aplica, con método abstracto `getTipo()`.
- **Value Objects embebidos** (p. ej. `Tarea`, `Bloqueador`, `Resumen` en sus respectivos slices): viven junto al modelo raíz en `<slice>/domain/models/`.
- **Enums** propios del slice en `<slice>/domain/enums/`; los compartidos en `commons/domain/enums/`.
- **Excepciones** en `commons/domain/exceptions/`, extienden `RuntimeException`. Usar `AvanceNotFoundException` para 404.
- **Puertos de salida:** interfaces en `<slice>/domain/ports/out/` que operan sobre modelos de dominio:
  - `AvancePersistencePortOut`: `save`, `findById`, `findByDesarrolladorId`, `findByFecha`.
  - `IAEnginePortOut`: `generateSummary`, `analyzeBlockers`, `suggestOptimizations`.
  - `NotificationPortOut`: `notifyLider`, `notifyDeveloper`.
  - No devuelven tipos de Spring Data ni DTOs.

---

## 5. Capa `application` (casos de uso)

- **Una clase = un caso de uso.** `@Service`, declarada `final`.
- Inyección por constructor. **Sin Lombok, sin `@Autowired`.**
- Único método público: `execute(...)`.
- Recibe `*Request`, devuelve `*Response` (o `List<*Response>`).
- Conversión:
  - **Request → modelo:** `request.applyTo(modelo)` (método de instancia del record).
  - **Modelo → Response:** `Response.from(modelo)` (factory estático del record).
- `Save` (Registrar Avance): instancia el modelo, llama `request.applyTo(modelo)`, setea `fechaCreacion = LocalDateTime.now()`, `estado = PENDIENTE`, persiste, retorna `Response.from(saved)`.
- `Update`: `findById(...).map(existing -> { request.applyTo(existing); existing.setFechaActualizacion(now); save; }).map(Response::from).orElseThrow(AvanceNotFoundException)`.
- `GenerarResumen`: obtiene avances del día, llama a IA-ENGINE para análisis, consolida resumen, persiste, notifica al líder, retorna `Response.from(resumen)`.
- Logging SLF4J (`private static final Logger log = LoggerFactory.getLogger(...)`), inicio y fin con placeholders `{}`, mensajes en español.

---

## 6. Capa `infrastructure` (por slice)

### 6.1 DTOs (`<slice>/infrastructure/in/dtos/`)
- **SIEMPRE `record`**.
- `@Schema` en clase y en cada campo (`description`, `example`).
- **Bean Validation** en los Request (`@NotBlank`, `@NotNull`, `@NotEmpty`, `@Size`, `@Valid` para anidados). Mensajes en español.
- **`*Request.applyTo(<Entidad> target)`** (método de instancia): muta `target` con los valores del request, incluida la conversión recursiva de DTOs anidados (`toDomain()` interno por VO).
- **`*Response.from(<Entidad> modelo)`** (factory estático): convierte modelo → DTO, con mapeo recursivo si aplica.
- Los Response NO llevan constraints de validación.
- Sin lógica de negocio. **No compartir DTOs entre slices.**

### 6.2 Interfaz Swagger (`<slice>/infrastructure/in/<Entidad>Rest.java`)
- Interfaz con `@Tag` + todo el detalle Swagger por método: `@Operation`, `@ApiResponses`, `@ApiResponse`, `@Content`, `@Schema`, `@ArraySchema`, `@Parameter`.
- **NO lleva `@RequestMapping` ni anotaciones de verbo.**
- Sí puede llevar `@RequestBody`, `@PathVariable`, `@RequestParam` en parámetros.

### 6.3 Controller (`<slice>/infrastructure/in/<Entidad>Controller.java`)
- `@RestController` + `@RequestMapping("/api/<entidades>")`, `implements <Entidad>Rest`, `final`.
- Inyección por constructor de los casos de uso.
- Cada método `@Override` añade SOLO el verbo y el path (`@PostMapping`, `@GetMapping("/{id}")`, `@PutMapping("/{id}")`, …).
- Cuerpo = una línea delegando al caso de uso y envolviendo en `ResponseEntity`:
  - `create` → `new ResponseEntity<>(uc.execute(req), HttpStatus.CREATED)`
  - resto → `ResponseEntity.ok(uc.execute(...))`
- **Sin lógica, sin validaciones manuales, sin try/catch.**

### 6.4 Adapters de salida (`<slice>/infrastructure/out/`)

#### 6.4.1 AvancePersistenceAdapterOut
- `@Component`, `final`, `implements AvancePersistencePortOut`.
- Inyecta `AvanceMongoRepository` por constructor. Logging SLF4J. Sin lógica de negocio.
- Implementa: `save`, `findById`, `findByDesarrolladorId`, `findByFecha`.

#### 6.4.2 IAEngineAdapterOut
- `@Component`, `final`, `implements IAEnginePortOut`.
- Inyecta `RestTemplate` o `WebClient` para llamar a IA-ENGINE.
- Implementa: `generateSummary` (POST a IA-ENGINE con avances), `analyzeBlockers`, `suggestOptimizations`.
- Manejo de errores si IA-ENGINE no responde: loguea y retorna respuesta por defecto.

#### 6.4.3 NotificationAdapterOut
- `@Component`, `final`, `implements NotificationPortOut`.
- Inyecta cliente de Teams/Email/Slack para enviar notificaciones.
- Implementa: `notifyLider`, `notifyDeveloper`.
- Logging de éxito/fallo de notificación.

### 6.5 Repositorio Mongo (`<slice>/infrastructure/persistences/mongodb/<Entidad>MongoRepository.java`)
- Interfaz `extends MongoRepository<<Entidad>, String>` con métodos derivados por nombre.
- `findByDesarrolladorId(String desarrolladorId)`: `List<<Entidad>>`.
- `findByFecha(LocalDate fecha)`: `List<<Entidad>>`.

---

## 7. Rutas REST estándar

Base path `/api/<entidades>`:

| Operación               | Verbo | Path                          | Status | Caso de uso                 |
|-------------------------|-------|-------------------------------|:------:|------------------------------|
| Registrar avance        | POST  | `/`                           | 201    | `RegistrarAvance`            |
| Obtener por id          | GET   | `/{id}`                       | 200    | `ObtenerAvanceById`          |
| Listar por desarrollador | GET   | `/desarrollador/{desarrolladorId}` | 200    | `ListarAvanceByDesarrollador`|
| Listar por fecha        | GET   | `/fecha/{fecha}`              | 200    | `ListarAvanceByFecha`        |
| Actualizar              | PUT   | `/{id}`                       | 200    | `ActualizarAvance`           |
| Generar resumen         | POST  | `/resumen/generar`            | 201    | `GenerarResumenEjecutivo`    |
| Notificar líder         | POST  | `/notificar`                  | 200    | `NotificarLider`             |

Errores documentados en Swagger: `400` (request inválida), `404` (no encontrado), `503` (IA-ENGINE no disponible).

---

## 8. Slices principales de CHECKPOINT

### 8.1 Slice `registrar_avance`
- **Responsabilidad:** Registrar el progreso diario de un desarrollador.
- **Modelo:** `Avance` (id, desarrolladorId, titulo, descripcion, estado, horasEstimadas, fechaCreacion, tareas).
- **Value Objects:** `Tarea` (id, titulo, descripcion, estado, horasReales).
- **Casos de uso:**
  - `RegistrarAvance`: POST `/api/avances`, crea nuevo avance.
  - `ActualizarAvance`: PUT `/api/avances/{id}`, actualiza avance existente.
  - `ObtenerAvanceById`: GET `/api/avances/{id}`, obtiene un avance.
- **Puertos de salida:** `AvancePersistencePortOut`.

### 8.2 Slice `generar_resumen`
- **Responsabilidad:** Generar resumen ejecutivo consolidado de avances.
- **Modelo:** `ResumenEjecutivo` (id, fecha, periodo, avanceTotal, tareasCompletadas, tareasEnCurso, bloqueadores, notasImportantes, estadisticas).
- **Value Objects:** `Bloqueador` (titulo, descripcion, impacto), `Estadistica` (metrica, valor).
- **Casos de uso:**
  - `GenerarResumenEjecutivo`: POST `/api/resumenes-ejecutivos/generar`, consolida avances del día + IA-ENGINE, notifica al líder.
  - `ObtenerResumenById`: GET `/api/resumenes-ejecutivos/{id}`.
  - `ListarResumenesByFecha`: GET `/api/resumenes-ejecutivos/fecha/{fecha}`.
- **Puertos de salida:** `AvancePersistencePortOut`, `IAEnginePortOut`, `NotificationPortOut`.

### 8.3 Slice `consultar_avance`
- **Responsabilidad:** Consultar y listar avances.
- **Casos de uso:**
  - `ListarAvanceByDesarrollador`: GET `/api/avances/desarrollador/{desarrolladorId}`.
  - `ListarAvanceByFecha`: GET `/api/avances/fecha/{fecha}`.
- **Puertos de salida:** `AvancePersistencePortOut`.

### 8.4 Slice `notificar_lider`
- **Responsabilidad:** Notificar al líder técnico sobre avances y bloqueadores.
- **Modelo:** `Notificacion` (id, liderTecnicoId, contenido, tipo, estado, fechaEnvio).
- **Casos de uso:**
  - `NotificarLider`: POST `/api/notificaciones`, envía notificación al líder.
  - `ObtenerNotificacionById`: GET `/api/notificaciones/{id}`.
- **Puertos de salida:** `NotificationPortOut`.

---

## 9. Integraciones críticas

### 9.1 Con IA-ENGINE
- **Endpoint:** `POST /api/ia/generar-resumen` (especificar contrato).
- **Request:** lista de `Avance`, periodo de análisis.
- **Response:** `ResumenEjecutivo` generado con análisis de bloqueadores, sugerencias de optimización.
- **Fallback:** si IA-ENGINE no responde, generar resumen básico sin análisis.

### 9.2 Con notificaciones (Teams/Email/Slack)
- **Objetivo:** notificar a líder técnico cada mañana con resumen ejecutivo.
- **Canal:** a definir (Teams webhook, Email SMTP, Slack bot).
- **Contenido:** resumen consolidado, bloqueadores críticos, métricas del día.

---

## 10. Checklist para nueva feature (nuevo slice)

1. Crear paquete `<slice>/` en la raíz del proyecto.
2. **Domain:** modelo (extiende `commons.domain.models.Avance` si aplica), enums propios, `*PortOut` interfaces.
3. **Application:** `Registrar`, `Obtener`, `Listar`, `Actualizar` (con `request.applyTo()` / `Response.from()`).
4. **DTOs:** `*Request` (con `applyTo`) y `*Response` (con `from`), records con `@Schema` y Bean Validation.
5. **REST:** interfaz `*Rest` (Swagger) + `*Controller` (verbos/paths).
6. **Out:** `*AdapterOut` por cada puerta de salida + `*MongoRepository`.
7. **Tests:** unitarios para `application`, integración para `infrastructure`.

---

## 11. Anti-patrones prohibidos

- ❌ Lombok.
- ❌ `@Autowired` en campos.
- ❌ Importar entre slices (`registrar_avance` no puede ver `generar_resumen`).
- ❌ Mover al slice algo que ya usan ≥ 2 slices (debe ir a `commons`), o viceversa.
- ❌ Anotaciones de verbo (`@GetMapping`, …) o `@RequestMapping` en la interfaz `*Rest`.
- ❌ Anotaciones Swagger en el controller.
- ❌ Lógica en el controller distinta a delegar al caso de uso.
- ❌ `application` dependiendo de `adapters.out` o `persistences`.
- ❌ `domain` importando DTOs, Spring Web o cualquier framework no-persistencia.
- ❌ DTOs como `class` (deben ser `record`).
- ❌ Atributos en inglés / métodos en español.
- ❌ Compartir DTOs entre slices.
- ❌ Mappers fuera de `Request.applyTo()` / `Response.from()` (incluye `toDomain()` interno de DTOs anidados del MISMO slice).
- ❌ Lógica de notificación dentro de `application` (delegarla a `NotificationPortOut`).
- ❌ Llamadas síncronas bloqueantes a IA-ENGINE sin timeout.

---

## 12. Tecnologías

- **Framework:** Spring Boot 3.2+
- **Persistencia:** MongoDB con Spring Data MongoDB.
- **HTTP Client:** RestTemplate o WebClient para IA-ENGINE.
- **Validación:** Jakarta Bean Validation.
- **API Doc:** SpringDoc OpenAPI (Swagger).
- **Logging:** SLF4J + Logback.
- **Testing:** JUnit 5, Mockito, TestContainers (para Mongo).
- **Build:** Maven.
