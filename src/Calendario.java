import java.time.LocalDate;
import java.util.*;
public final class Calendario implements Iterable<Programable> {
    private final List<Programable> eventos = new ArrayList<>();
    public Calendario() { }
    public void agregar(Programable evento) {
        if (evento == null) throw new ArenaException("Evento obligatorio.");
        if (eventos.contains(evento)) throw new ArenaException("Evento ya agregado al calendario.");
        if (evento.estaProgramado()) validarConflictos(evento, evento.getFecha(), evento.getHora(), evento.getSede());
        eventos.add(evento);
    }
    public void programar(EventoBase evento, LocalDate fecha, int hora, String sede) {
        if (evento == null || !eventos.contains(evento)) throw new ArenaException("Evento no registrado en el calendario.");
        evento.validarProgramacion(fecha, hora, sede);
        validarConflictos(evento, fecha, hora, sede.trim());
        evento.programar(fecha, hora, sede);
    }
    private void validarConflictos(Programable propio, LocalDate fecha, int hora, String sede) {
        for (Programable otro : eventos) {
            if (otro == propio || !otro.estaProgramado()) continue;
            if (fecha.equals(otro.getFecha()) && hora == otro.getHora()) {
                if (sede.equals(otro.getSede())) throw new ArenaException("Sede ocupada en esa fecha y hora.");
                for (Participante participante : propio.getParticipantes())
                    if (otro.getParticipantes().contains(participante)) throw new ArenaException("Participante ocupado: " + participante.getNombre() + ".");
            }
        }
    }
    private List<Programable> vista(java.util.function.Predicate<Programable> filtro) {
        List<Programable> copia = new ArrayList<>();
        for (Programable evento : eventos) if (evento.estaProgramado() && filtro.test(evento)) copia.add(evento);
        Collections.sort(copia);
        return List.copyOf(copia);
    }
    public Iterable<Programable> getGeneral() { return vista(e -> true); }
    public Iterable<Programable> porTorneo(Torneo torneo) { return vista(e -> e.getTorneo() == torneo); }
    public Iterable<Programable> porParticipante(Participante participante) { return vista(e -> e.getParticipantes().contains(participante)); }
    @Override public Iterator<Programable> iterator() { return getGeneral().iterator(); }
}
