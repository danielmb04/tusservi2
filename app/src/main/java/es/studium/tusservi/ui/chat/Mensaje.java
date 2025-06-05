package es.studium.tusservi.ui.chat;

public class Mensaje {
    private String mensaje;
    private int emisorId;
    private String texto;
    private String fecha;
    private String tipo;

    public Mensaje(int emisorId, String texto, String fecha, String tipo) {
        this.emisorId = emisorId;
        this.texto = texto;
        this.fecha = fecha;
        this.tipo = tipo;
    }

    public Mensaje(int emisor, String texto, String fecha) {
        this.emisorId = emisor;
        this.texto = texto;
        this.fecha = fecha;
    }
    public Mensaje(String mensaje) {
        this.mensaje = mensaje;
    }
    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    // Método para detectar si es imagen
    public boolean esImagen() {
        if (mensaje == null) return false;
        return mensaje.toLowerCase().matches(".*\\.(jpg|jpeg|png|gif)$");
    }

    // Método para detectar si es video
    public boolean esVideo() {
        if (mensaje == null) return false;
        return mensaje.toLowerCase().matches(".*\\.(mp4|avi|mov|mkv)$");
    }


    public String getTipo() { return tipo; }

    public int getEmisorId() { return emisorId; }
    public String getTexto() { return texto; }
    public String getFecha() { return fecha; }
}
