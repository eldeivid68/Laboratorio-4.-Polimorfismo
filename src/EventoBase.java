import java.time.LocalDate;
import java.util.*;
public abstract class EventoBase implements Programable {
    private final Torneo torneo;
    private final List<Participante> participantes;
    private LocalDate fecha;
    private int hora;
    private String sede;
    private EstadoEvento estado = EstadoEvento.SIN_PROGRAMAR;
    private ResultadoEvento resultado;
    protected EventoBase(Torneo torneo, List<Participante> participantes) {
        if (torneo == null || participantes == null || participantes.isEmpty() || participantes.stream().anyMatch(Objects::isNull))
            throw new ArenaException("Evento sin torneo o participantes válidos.");
        if (new HashSet<>(participantes).size() != participantes.size()) throw new ArenaException("Participantes repetidos en el evento.");
        this.torneo = torneo; this.participantes = List.copyOf(participantes);
    }
    @Override public final Torneo getTorneo() { return torneo; }
    @Override public final List<Participante> getParticipantes() { return participantes; }
    @Override public final LocalDate getFecha() { return fecha; }
    @Override public final int getHora() { return hora; }
    @Override public final String getSede() { return sede; }
    public final EstadoEvento getEstado() { return estado; }
    public final Optional<ResultadoEvento> getResultado() { return Optional.ofNullable(resultado); }
    @Override public final boolean estaProgramado() { return fecha != null; }
    final void validarProgramacion(LocalDate fecha, int hora, String sede) {
        if (estado == EstadoEvento.FINALIZADO) throw new ArenaException("Un evento finalizado no se puede reprogramar.");
        if (torneo.getEstado() != EstadoTorneo.EN_JUEGO) throw new ArenaException("El torneo debe estar en juego.");
        if (fecha == null || hora < 0 || hora > 23) throw new ArenaException("Fecha obligatoria y hora entre 0 y 23.");
        DatosResultado.texto(sede, "Sede");
    }
    final void programar(LocalDate fecha, int hora, String sede) {
        validarProgramacion(fecha, hora, sede);
        this.fecha = fecha; this.hora = hora; this.sede = sede.trim(); this.estado = EstadoEvento.PROGRAMADO;
    }
    final void finalizar(ResultadoEvento resultado) { this.resultado = resultado; estado = EstadoEvento.FINALIZADO; }
    public abstract List<CampoResultado> getCamposResultado();
    public abstract ResultadoEvento prepararResultado(DatosResultado datos);
    @Override public final int compareTo(Programable otro) {
        int orden = Comparator.nullsLast(Comparator.<LocalDate>naturalOrder()).compare(fecha, otro.getFecha());
        if (orden == 0) orden = Integer.compare(hora, otro.getHora());
        if (orden == 0) orden = Comparator.nullsLast(Comparator.<String>naturalOrder()).compare(sede, otro.getSede());
        return orden;
    }
    @Override public String descripcionCalendario() {
        StringJoiner nombres = new StringJoiner(" / ");
        for (Participante participante : participantes) nombres.add(participante.getNombre());
        return torneo.getCodigo() + " | " + fecha + " " + hora + ":00 | " + sede + " | " + nombres + " | " + estado;
    }
}
