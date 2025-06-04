package es.studium.tusservi.ui.chat;

public class Mensaje {
    private int emisorId;
    private String texto;
    private String fecha;

    public Mensaje(int emisorId, String texto, String fecha) {
        this.emisorId = emisorId;
        this.texto = texto;
        this.fecha = fecha;
    }

    public int getEmisorId() { return emisorId; }
    public String getTexto() { return texto; }
    public String getFecha() { return fecha; }
}
