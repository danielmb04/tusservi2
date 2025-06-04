package es.studium.tusservi.ui.chat;

public class Usuario {
    private int idUsuario;
    private String nombre;
    private String urlImagenPerfil;  // nuevo campo

    public Usuario(int idUsuario, String nombre, String urlImagenPerfil) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.urlImagenPerfil = urlImagenPerfil;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUrlImagenPerfil() {
        return urlImagenPerfil;
    }
}


