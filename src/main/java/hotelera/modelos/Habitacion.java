package hotelera.modelos;

public class Habitacion {
    private int idHabitacion;
    private int numero;
    private String tipo;
    private double precio;
    
    public Habitacion() {}
    
    public int getIdHabitacion() { return idHabitacion; }
    public void setIdHabitacion(int idHabitacion) { this.idHabitacion = idHabitacion; }
    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
}