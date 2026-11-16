public final class Participante implements Comparable<Participante> {
    private final String codigo;
    private final String nombre;
    private final Empresa empresa;
    public Participante(String codigo, String nombre, Empresa empresa) {
        this.codigo = DatosResultado.texto(codigo, "Código de participante");
        this.nombre = DatosResultado.texto(nombre, "Nombre de participante");
        if (empresa == null) throw new ArenaException("La empresa es obligatoria.");
        this.empresa = empresa;
    }
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public Empresa getEmpresa() { return empresa; }
    @Override public int compareTo(Participante otro) { return nombre.compareTo(otro.nombre); }
    @Override public String toString() { return codigo + " - " + nombre + " (" + empresa.getNombre() + ")"; }
}
