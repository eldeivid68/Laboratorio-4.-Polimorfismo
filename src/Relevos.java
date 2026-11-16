import java.util.*;
public final class Relevos extends Disciplina {
    private final List<Integer> puntosPorLugar = List.of(10, 8, 6, 5, 4, 3, 2, 1);
    public Relevos() { super(); }
    @Override public String getNombre() { return "Relevos"; }
    @Override public int getMinParticipantes() { return 3; }
    @Override public int getMaxParticipantes() { return 8; }
    @Override public double getCuotaInscripcion() { return 300; }
    @Override public List<EventoBase> generarEventos(Torneo torneo, List<Participante> inscritos) {
        validarCantidad(inscritos.size());
        return List.of(new Carrera(torneo, inscritos));
    }
    @Override public List<CampoResultado> getCamposResultado(List<Participante> participantes) {
        List<CampoResultado> campos = new ArrayList<>();
        for (Participante participante : participantes)
            campos.add(new CampoResultado(participante.getCodigo(), "Tiempo de " + participante.getNombre() + " en segundos, o D para descalificar"));
        return List.copyOf(campos);
    }
    @Override public ResultadoEvento validarResultado(List<Participante> participantes, DatosResultado datos) {
        Map<Participante, Double> tiempos = new LinkedHashMap<>();
        Set<Double> distintos = new HashSet<>();
        for (Participante participante : participantes) {
            if (!datos.get(participante.getCodigo()).equalsIgnoreCase("D")) {
                double tiempo = datos.tiempo(participante.getCodigo());
                if (!distintos.add(tiempo)) throw new ArenaException("Los tiempos deben ser distintos.");
                tiempos.put(participante, tiempo);
            }
        }
        List<Participante> orden = new ArrayList<>(tiempos.keySet());
        orden.sort(Comparator.comparingDouble(tiempos::get));
        List<AporteResultado> aportes = new ArrayList<>();
        StringJoiner detalle = new StringJoiner("; ");
        for (Participante participante : participantes) {
            int lugar = orden.indexOf(participante);
            aportes.add(new AporteResultado(participante, lugar < 0 ? 0 : puntosPorLugar.get(lugar), 0, lugar == 0 ? 1 : 0));
            detalle.add(participante.getCodigo() + "=" + datos.get(participante.getCodigo()));
        }
        return new ResultadoEvento(aportes, detalle.toString());
    }
}
