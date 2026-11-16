import java.time.LocalDate;
import java.util.*;
public final class ArenaGT {
    private final Map<String, Empresa> empresas = new LinkedHashMap<>();
    private final Map<String, Participante> participantes = new LinkedHashMap<>();
    private final Map<String, Torneo> torneos = new LinkedHashMap<>();
    private final List<Disciplina> disciplinas;
    private final Calendario calendario = new Calendario();
    public ArenaGT() { this(List.of(new FutbolSala(), new FutbolSalaConPenales(), new Baloncesto(), new Voleibol(), new Relevos())); }
    public ArenaGT(List<Disciplina> disciplinas) {
        if (disciplinas == null || disciplinas.isEmpty() || disciplinas.stream().anyMatch(Objects::isNull)) throw new ArenaException("Catálogo de disciplinas obligatorio.");
        this.disciplinas = List.copyOf(disciplinas);
    }
    public List<Disciplina> getDisciplinas() { return disciplinas; }
    public List<Participante> getParticipantes() { return List.copyOf(participantes.values()); }
    public List<Torneo> getTorneos() { return List.copyOf(torneos.values()); }
    public List<Empresa> getEmpresas() { return List.copyOf(empresas.values()); }
    public Calendario getCalendario() { return calendario; }
    public Participante registrarParticipante(String codigo, String nombre, String nombreEmpresa) {
        String clave = DatosResultado.texto(codigo, "Código de participante");
        if (participantes.containsKey(clave)) throw new ArenaException("Código de participante repetido.");
        Empresa candidata = new Empresa(nombreEmpresa);
        Empresa empresa = empresas.getOrDefault(candidata.getNombre(), candidata);
        Participante nuevo = new Participante(clave, nombre, empresa);
        empresas.putIfAbsent(empresa.getNombre(), empresa); participantes.put(clave, nuevo);
        return nuevo;
    }
    public Torneo crearTorneo(String codigo, String nombre, Disciplina disciplina) {
        String clave = DatosResultado.texto(codigo, "Código de torneo");
        if (torneos.containsKey(clave)) throw new ArenaException("Código de torneo repetido.");
        if (!disciplinas.contains(disciplina)) throw new ArenaException("Disciplina fuera del catálogo.");
        Torneo nuevo = new Torneo(clave, nombre, disciplina); torneos.put(clave, nuevo); return nuevo;
    }
    public Participante buscarParticipante(String codigo) {
        Participante participante = participantes.get(DatosResultado.texto(codigo, "Código de participante"));
        if (participante == null) throw new ArenaException("Participante inexistente.");
        return participante;
    }
    public Torneo buscarTorneo(String codigo) {
        Torneo torneo = torneos.get(DatosResultado.texto(codigo, "Código de torneo"));
        if (torneo == null) throw new ArenaException("Torneo inexistente.");
        return torneo;
    }
    public double cotizar(Disciplina disciplina, int cantidad) {
        if (!disciplinas.contains(disciplina)) throw new ArenaException("Disciplina fuera del catálogo.");
        return disciplina.calcularCotizacion(cantidad);
    }
    public double inscribir(String torneo, String participante) { return buscarTorneo(torneo).inscribir(buscarParticipante(participante)); }
    public void cerrarInscripciones(String codigo) {
        Torneo torneo = buscarTorneo(codigo);
        torneo.cerrarInscripciones();
        for (EventoBase evento : torneo.getEventos()) calendario.agregar(evento);
    }
    public void programar(String codigo, int numeroEvento, LocalDate fecha, int hora, String sede) { calendario.programar(buscarEvento(codigo, numeroEvento), fecha, hora, sede); }
    public EventoBase buscarEvento(String codigo, int numeroEvento) {
        List<EventoBase> eventos = buscarTorneo(codigo).getEventos();
        if (numeroEvento < 1 || numeroEvento > eventos.size()) throw new ArenaException("Número de evento fuera de rango.");
        return eventos.get(numeroEvento - 1);
    }
    public void registrarResultado(String codigo, int numeroEvento, DatosResultado datos) {
        Torneo torneo = buscarTorneo(codigo); torneo.registrarResultado(buscarEvento(codigo, numeroEvento), datos);
    }
    public double getIngresosTotal() { return torneos.values().stream().mapToDouble(Torneo::getIngresos).sum(); }
    public Map<String, Double> getIngresosPorDisciplina() {
        Map<String, Double> ingresos = new LinkedHashMap<>();
        for (Disciplina disciplina : disciplinas) ingresos.putIfAbsent(disciplina.getNombre(), 0.0);
        for (Torneo torneo : torneos.values()) ingresos.merge(torneo.getDisciplina().getNombre(), torneo.getIngresos(), Double::sum);
        return Collections.unmodifiableMap(ingresos);
    }
    public List<Premio> getPremios() {
        List<Premio> premios = new ArrayList<>();
        for (Torneo torneo : torneos.values()) torneo.getPremio().ifPresent(premios::add);
        return List.copyOf(premios);
    }
    public List<LineaRankingEmpresa> getRankingEmpresas() {
        Map<Empresa, Long> puntos = new LinkedHashMap<>();
        for (Empresa empresa : empresas.values()) puntos.put(empresa, 0L);
        for (Torneo torneo : torneos.values()) for (LineaTabla linea : torneo.getTabla())
            puntos.merge(linea.getParticipante().getEmpresa(), (long) linea.getPuntos(), Long::sum);
        List<LineaRankingEmpresa> ranking = new ArrayList<>();
        puntos.forEach((empresa, total) -> ranking.add(new LineaRankingEmpresa(empresa, total)));
        Collections.sort(ranking); return List.copyOf(ranking);
    }
}
