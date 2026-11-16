import java.util.*;
public final class ResultadoEvento {
    private final List<AporteResultado> aportes;
    private final String descripcion;
    public ResultadoEvento(List<AporteResultado> aportes, String descripcion) {
        if (aportes == null || aportes.isEmpty() || aportes.stream().anyMatch(Objects::isNull)) throw new ArenaException("Resultado sin aportes válidos.");
        this.aportes = List.copyOf(aportes);
        this.descripcion = DatosResultado.texto(descripcion, "Descripción del resultado");
    }
    public List<AporteResultado> getAportes() { return aportes; }
    public String getDescripcion() { return descripcion; }
}
