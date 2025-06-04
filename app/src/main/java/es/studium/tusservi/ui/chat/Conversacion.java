package es.studium.tusservi.ui.chat;

public class Conversacion {
    private int idConversacion;
    private int idOtroUsuario;
    private String nombreOtroUsuario;
    private String ultimoMensaje;

    public Conversacion(int idConversacion, int idOtroUsuario, String nombreOtroUsuario, String ultimoMensaje) {
        this.idConversacion = idConversacion;
        this.idOtroUsuario = idOtroUsuario;
        this.nombreOtroUsuario = nombreOtroUsuario;
        this.ultimoMensaje = ultimoMensaje;
    }

    public int getIdConversacion() { return idConversacion; }
    public int getIdOtroUsuario() { return idOtroUsuario; }
    public String getNombreOtroUsuario() { return nombreOtroUsuario; }
    public String getUltimoMensaje() { return ultimoMensaje; }
}


