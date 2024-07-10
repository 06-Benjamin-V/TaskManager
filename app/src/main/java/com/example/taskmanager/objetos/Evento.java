package com.example.taskmanager.objetos;
public class Evento {
    private String titulo;
    private String descripcion;
    private String fechaHoraInicio;
    private String fechaHoraFin;
    public Evento(){

    }

    public Evento(String titulo, String descripcion, String fechaHoraInicio, String fechaHoraFin) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getFechaHoraInicio() {
        return fechaHoraInicio;
    }

    public String getFechaHoraFin() {
        return fechaHoraFin;
    }



    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setFechaHoraInicio(String fechaHoraInicio) {
        this.fechaHoraInicio = fechaHoraInicio;
    }

    public void setFechaHoraFin(String fechaHoraFin) {
        this.fechaHoraFin = fechaHoraFin;
    }
}

