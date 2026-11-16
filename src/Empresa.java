import java.util.Objects;
public final class Empresa {
    private final String nombre;
    public Empresa(String nombre) { this.nombre = DatosResultado.texto(nombre, "Nombre de empresa"); }
    public String getNombre() { return nombre; }
    @Override public boolean equals(Object otra) {
        if (this == otra) return true;
        if (otra == null) return false;
        try { return nombre.equals(((Empresa) otra).nombre); }
        catch (ClassCastException ex) { return false; }
    }
    @Override public int hashCode() { return Objects.hash(nombre); }
}
