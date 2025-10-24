package com.empujecomunitario.rest_server.entity;

public class EventReportResponse {
    private Long eventoId;
    private String nombreEvento;
    private String descripcion;
    private String fecha;
    private Boolean repartoDonaciones;
    private String usuarioParticipante;

    // Constructor
    public EventReportResponse(Long eventoId, String nombreEvento, String descripcion,
            String fecha, Boolean repartoDonaciones, String usuarioParticipante) {
        this.eventoId = eventoId;
        this.nombreEvento = nombreEvento;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.repartoDonaciones = repartoDonaciones;
        this.usuarioParticipante = usuarioParticipante;
    }

    // Getters
    public Long getEventoId() {
        return eventoId;
    }

    public String getNombreEvento() {
        return nombreEvento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public Boolean getRepartoDonaciones() {
        return repartoDonaciones;
    }

    public String getUsuarioParticipante() {
        return usuarioParticipante;
    }
}
