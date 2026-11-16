import java.util.*;
public final class Encuentro extends EventoBase {
    public Encuentro(Torneo torneo, Participante primero, Participante segundo) { super(torneo, List.of(primero, segundo)); }
    @Override public List<CampoResultado> getCamposResultado() { return getTorneo().getDisciplina().getCamposResultado(getParticipantes()); }
    @Override public ResultadoEvento prepararResultado(DatosResultado datos) { return getTorneo().getDisciplina().validarResultado(getParticipantes(), datos); }
}
