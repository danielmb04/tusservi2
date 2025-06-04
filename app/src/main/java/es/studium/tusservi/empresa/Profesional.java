package es.studium.tusservi.empresa;

public class Profesional {
    private final String nombre;
    private final String categoria;
    private final String fotoPerfil;

    public Profesional(String nombre, String categoria, String fotoPerfil) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.fotoPerfil = fotoPerfil;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }
}
