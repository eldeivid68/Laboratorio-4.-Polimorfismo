import java.util.*;
public final class Voleibol extends Disciplina {
    private final int puntosVictoriaDirecta = 3;
    private final int puntosDerrotaDirecta = 0;
    private final int puntosVictoriaAjustada = 2;
    private final int puntosDerrotaAjustada = 1;
    public Voleibol() { super(); }
    @Override public String getNombre() { return "Voleibol"; }
    @Override public int getMinParticipantes() { return 4; }
    @Override public int getMaxParticipantes() { return 6; }
    @Override public double getCuotaInscripcion() { return 450; }
    @Override public List<EventoBase> generarEventos(Torneo torneo, List<Participante> inscritos) { return generarEncuentros(torneo, inscritos); }
    @Override public List<CampoResultado> getCamposResultado(List<Participante> participantes) { return camposMarcador(participantes); }
    @Override public ResultadoEvento validarResultado(List<Participante> participantes, DatosResultado datos) {
        int a = datos.entero("a"), b = datos.entero("b");
        if (Math.max(a, b) != 3 || Math.min(a, b) > 2) throw new ArenaException("Sets válidos: 3-0, 3-1 o 3-2, o sus inversos.");
        int ganador = Math.min(a, b) == 2 ? puntosVictoriaAjustada : puntosVictoriaDirecta;
        int perdedor = Math.min(a, b) == 2 ? puntosDerrotaAjustada : puntosDerrotaDirecta;
        return resultadoDuelo(participantes, a, b, a > b ? ganador : perdedor,
                b > a ? ganador : perdedor, a > b ? 1 : 0, b > a ? 1 : 0, a + "-" + b);
    }
}
