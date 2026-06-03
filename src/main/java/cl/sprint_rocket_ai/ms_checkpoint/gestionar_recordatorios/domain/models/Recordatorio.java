package cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.domain.models;

import cl.sprint_rocket_ai.ms_checkpoint.commons.domain.enums.DiaSemana;
import cl.sprint_rocket_ai.ms_checkpoint.commons.domain.enums.TipoRecordatorio;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "recordatorios")
public class Recordatorio {

    @Id
    private String id;

    @Field("user_id")
    private String userId;

    @Field("titulo")
    private String titulo;

    @Field("tipo_recordatorio")
    private TipoRecordatorio tipoRecordatorio;

    @Field("hora_activacion")
    private String horaActivacion;

    @Field("dias_semana")
    private List<DiaSemana> diasSemana;

    @Field("activo")
    private boolean activo;

    @Field("fecha_expiracion")
    private LocalDate fechaExpiracion;

    @Field("proximo_envio")
    private LocalDateTime proximoEnvio;

    @Field("fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Field("fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    public Recordatorio() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public TipoRecordatorio getTipoRecordatorio() {
        return tipoRecordatorio;
    }

    public void setTipoRecordatorio(TipoRecordatorio tipoRecordatorio) {
        this.tipoRecordatorio = tipoRecordatorio;
    }

    public String getHoraActivacion() {
        return horaActivacion;
    }

    public void setHoraActivacion(String horaActivacion) {
        this.horaActivacion = horaActivacion;
    }

    public List<DiaSemana> getDiasSemana() {
        return diasSemana;
    }

    public void setDiasSemana(List<DiaSemana> diasSemana) {
        this.diasSemana = diasSemana;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDate getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(LocalDate fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public LocalDateTime getProximoEnvio() {
        return proximoEnvio;
    }

    public void setProximoEnvio(LocalDateTime proximoEnvio) {
        this.proximoEnvio = proximoEnvio;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
