import java.util.*;
public final class Carrera extends EventoBase {
    public Carrera(Torneo torneo, List<Participante> participantes) { super(torneo, participantes); }
    @Override public List<CampoResultado> getCamposResultado() { return getTorneo().getDisciplina().getCamposResultado(getParticipantes()); }
    @Override public ResultadoEvento prepararResultado(DatosResultado datos) { return getTorneo().getDisciplina().validarResultado(getParticipantes(), datos); }
}
