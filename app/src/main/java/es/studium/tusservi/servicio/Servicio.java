package es.studium.tusservi.servicio;

public class Servicio {
    private int idServicio;
    private String tituloServicio;
    private String descripcionServicio;
    private double precioEstimadoServicio;

    public Servicio(int id, String nombre, String descripcion, double precio) {
        this.idServicio = id;
        this.tituloServicio = nombre;
        this.descripcionServicio = descripcion;
        this.precioEstimadoServicio = precio;
    }

    public int getId() { return idServicio; }
    public String getTituloServicio() { return tituloServicio; }
    public String getDescripcionServicio() { return descripcionServicio; }
    public double getPrecioEstimadoServicio() { return precioEstimadoServicio; }
}
