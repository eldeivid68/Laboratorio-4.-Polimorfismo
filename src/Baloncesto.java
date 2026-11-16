import java.util.*;
public final class Baloncesto extends Disciplina {
    private final int puntosVictoria = 2;
    private final int puntosDerrota = 1;
    public Baloncesto() { super(); }
    @Override public String getNombre() { return "Baloncesto"; }
    @Override public int getMinParticipantes() { return 4; }
    @Override public int getMaxParticipantes() { return 6; }
    @Override public double getCuotaInscripcion() { return 500; }
    @Override public List<EventoBase> generarEventos(Torneo torneo, List<Participante> inscritos) { return generarEncuentros(torneo, inscritos); }
    @Override public List<CampoResultado> getCamposResultado(List<Participante> participantes) { return camposMarcador(participantes); }
    @Override public ResultadoEvento validarResultado(List<Participante> participantes, DatosResultado datos) {
        int a = datos.entero("a"), b = datos.entero("b");
        if (a == b) throw new ArenaException("Baloncesto no permite empates.");
        return resultadoDuelo(participantes, a, b, a > b ? puntosVictoria : puntosDerrota,
                b > a ? puntosVictoria : puntosDerrota, a > b ? 1 : 0, b > a ? 1 : 0, a + "-" + b);
    }
}
