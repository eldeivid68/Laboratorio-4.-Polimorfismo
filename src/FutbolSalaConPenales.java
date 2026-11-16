import java.util.*;
public final class FutbolSalaConPenales extends FutbolSala {
    private final int puntosGanadorPenales = 2;
    private final int puntosPerdedorPenales = 1;
    public FutbolSalaConPenales() { super(); }
    @Override public String getNombre() { return "Fútbol sala con penales"; }
    @Override public double getCuotaInscripcion() { return super.getCuotaInscripcion() + 150; }
    @Override public List<CampoResultado> getCamposResultado(List<Participante> participantes) {
        List<CampoResultado> campos = new ArrayList<>(super.getCamposResultado(participantes));
        campos.add(new CampoResultado("pa", "Penales de " + participantes.get(0).getNombre() + " (vacío si no hubo empate)"));
        campos.add(new CampoResultado("pb", "Penales de " + participantes.get(1).getNombre() + " (vacío si no hubo empate)"));
        return List.copyOf(campos);
    }
    @Override public ResultadoEvento validarResultado(List<Participante> participantes, DatosResultado datos) {
        int a = datos.entero("a"), b = datos.entero("b");
        if (a != b) {
            if (!datos.get("pa").isEmpty() || !datos.get("pb").isEmpty()) throw new ArenaException("No se permiten penales sin empate reglamentario.");
            return super.validarResultado(participantes, datos);
        }
        int pa = datos.entero("pa"), pb = datos.entero("pb");
        if (pa == pb) throw new ArenaException("Los penales deben definir un ganador.");
        return resultadoDuelo(participantes, a, b, pa > pb ? puntosGanadorPenales : puntosPerdedorPenales,
                pb > pa ? puntosGanadorPenales : puntosPerdedorPenales, pa > pb ? 1 : 0, pb > pa ? 1 : 0,
                a + "-" + b + "; penales " + pa + "-" + pb);
    }
}
