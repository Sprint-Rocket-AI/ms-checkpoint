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


    @Field("activo")
    private boolean activo;

    @Field("fecha_expiracion")
    private LocalDateTime fechaExpiracion;

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


    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(LocalDateTime fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }


    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
