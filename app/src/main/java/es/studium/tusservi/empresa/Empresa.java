package es.studium.tusservi.empresa;

import java.io.Serializable;

public class Empresa implements Serializable {
    private int id;
    private String nombre, descripcion, ubicacion, horario, web, logo, categoria;

    public Empresa(int id, String nombre, String descripcion, String ubicacion,
                   String horario, String web, String logo, String categoria) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.ubicacion = ubicacion;
        this.horario = horario;
        this.web = web;
        this.logo = logo;
        this.categoria = categoria;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getUbicacion() { return ubicacion; }
    public String getHorario() { return horario; }
    public String getWeb() { return web; }

    public String getLogo() {
        return null;
    }
    public String getCategoria() { return categoria; }


    public void setLogo(String logo) {
        this.logo = logo;
    }

    public void setNombre(String string) {
    }

    public void setDescripcion(String string) {
    }

    public void setUbicacion(String string) {
    }

    public void setHorario(String string) {
    }

    public void setWeb(String string) {
    }
}

