package com.example.taskmanager.objetos;

import com.google.type.DateTime;

public class Tarea {
    private String titulo;
    private String descripcion;
    private String fechaHoraInicio;
    private String fechaHoraFin;

    public Tarea(String titulo, String descripcion, String fechaHoraInicio, String fechaHoraFin) {
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
}

