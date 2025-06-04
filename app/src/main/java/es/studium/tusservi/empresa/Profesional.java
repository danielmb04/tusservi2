package es.studium.tusservi.empresa;

public class Profesional {
    private final int idProfesional;
    private int idUsuario; // ID de la tabla Usuarios, necesario para el chat

    private final String nombre;
    private final String categoria;
    private final String fotoPerfil;


    public Profesional(int idProfesional, int idUsuario, String nombre, String categoria, String fotoPerfil) {
        this.idProfesional = idProfesional;
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.categoria = categoria;
        this.fotoPerfil = fotoPerfil;
    }
    public int getIdUsuario() {
        return idUsuario;
    }

    public int getIdProfesional() {
        return idProfesional;
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
