import java.util.*;
public class FutbolSala extends Disciplina {
    private final int puntosVictoria = 3;
    private final int puntosEmpate = 1;
    private final int puntosDerrota = 0;
    public FutbolSala() { super(); }
    @Override public String getNombre() { return "Fútbol sala"; }
    @Override public int getMinParticipantes() { return 4; }
    @Override public int getMaxParticipantes() { return 8; }
    @Override public double getCuotaInscripcion() { return 600; }
    @Override public List<EventoBase> generarEventos(Torneo torneo, List<Participante> inscritos) { return generarEncuentros(torneo, inscritos); }
    @Override public List<CampoResultado> getCamposResultado(List<Participante> participantes) { return camposMarcador(participantes); }
    @Override public ResultadoEvento validarResultado(List<Participante> participantes, DatosResultado datos) {
        int a = datos.entero("a"), b = datos.entero("b");
        if (a == b) return resultadoDuelo(participantes, a, b, puntosEmpate, puntosEmpate, 0, 0, a + "-" + b);
        return resultadoDuelo(participantes, a, b, a > b ? puntosVictoria : puntosDerrota,
                b > a ? puntosVictoria : puntosDerrota, a > b ? 1 : 0, b > a ? 1 : 0, a + "-" + b);
    }
}
