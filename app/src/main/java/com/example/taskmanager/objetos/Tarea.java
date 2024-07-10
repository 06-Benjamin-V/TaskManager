package com.example.taskmanager.objetos;

public class Tarea {
    private String id;
    private String tituloTarea;
    private String descripcionTarea;
    private boolean actividad;

    public Tarea() {
    }

    public Tarea(String tituloTarea, String descripcionTarea) {
        this.tituloTarea = tituloTarea;
        this.descripcionTarea = descripcionTarea;
        this.actividad = true;
    }

    public String getId() {
        return id;
    }

    public boolean isActividad() {
        return actividad;
    }

    public String getTituloTarea() {
        return tituloTarea;
    }

    public String getDescripcionTarea() {
        return descripcionTarea;
    }

    public void setTituloTarea(String tituloTarea) {
        this.tituloTarea = tituloTarea;
    }

    public void setActividad(boolean actividad) {
        this.actividad = actividad;
    }

    public void setDescripcionTarea(String descripcionTarea) {
        this.descripcionTarea = descripcionTarea;
    }

    public void setId(String id) {
        this.id = id;
    }
}
