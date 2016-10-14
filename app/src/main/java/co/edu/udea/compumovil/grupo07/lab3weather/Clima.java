package co.edu.udea.compumovil.grupo07.lab3weather;

/**
 * Created by Usuario on 26/09/2016.
 */

public class Clima {
    private double temperatura;
    private int humedad;
    private String descripcion;
    private String icono;
    private byte [] imagenClima;

    public Clima(){}

    public double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(double temperatura) {
        this.temperatura = temperatura;
    }

    public int getHumedad() {
        return humedad;
    }

    public void setHumedad(int humedad) {
        this.humedad = humedad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public byte[] getImagenClima() {
        return imagenClima;
    }

    public void setImagenClima(byte[] imagenClima) {
        this.imagenClima = imagenClima;
    }

}
