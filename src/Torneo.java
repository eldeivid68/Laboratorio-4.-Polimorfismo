import java.util.*;
public final class Torneo {
    private final String codigo;
    private final String nombre;
    private final Disciplina disciplina;
    private EstadoTorneo estado = EstadoTorneo.EN_INSCRIPCION;
    private final List<Participante> inscritos = new ArrayList<>();
    private List<EventoBase> eventos = List.of();
    private Map<Participante, LineaTabla> tabla = new LinkedHashMap<>();
    private double ingresos;
    private Premio premio;
    public Torneo(String codigo, String nombre, Disciplina disciplina) {
        this.codigo = DatosResultado.texto(codigo, "Código de torneo");
        this.nombre = DatosResultado.texto(nombre, "Nombre de torneo");
        if (disciplina == null) throw new ArenaException("Disciplina obligatoria.");
        this.disciplina = disciplina;
    }
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public Disciplina getDisciplina() { return disciplina; }
    public EstadoTorneo getEstado() { return estado; }
    public List<Participante> getInscritos() { return List.copyOf(inscritos); }
    public List<EventoBase> getEventos() { return eventos; }
    public double getIngresos() { return ingresos; }
    public Optional<Premio> getPremio() { return Optional.ofNullable(premio); }
    public double inscribir(Participante participante) { return agregarInscrito(participante, true); }
    void cargarInscritoInicial(Participante participante) { agregarInscrito(participante, false); }
    private double agregarInscrito(Participante participante, boolean cobrar) {
        if (estado != EstadoTorneo.EN_INSCRIPCION) throw new ArenaException("Las inscripciones están cerradas.");
        if (participante == null) throw new ArenaException("Participante obligatorio.");
        if (inscritos.size() >= disciplina.getMaxParticipantes()) throw new ArenaException("El torneo está lleno.");
        if (inscritos.stream().anyMatch(p -> p.getCodigo().equals(participante.getCodigo()))) throw new ArenaException("Participante ya inscrito.");
        long porEmpresa = inscritos.stream().filter(p -> p.getEmpresa().equals(participante.getEmpresa())).count();
        if (porEmpresa >= 2) throw new ArenaException("Máximo dos inscritos de la misma empresa.");
        double cobro = cobrar ? disciplina.getCuotaInscripcion() : 0;
        LineaTabla linea = new LineaTabla(participante);
        inscritos.add(participante); tabla.put(participante, linea); ingresos += cobro;
        return cobro;
    }
    public void cerrarInscripciones() {
        if (estado != EstadoTorneo.EN_INSCRIPCION) throw new ArenaException("Las inscripciones ya fueron cerradas.");
        disciplina.validarCantidad(inscritos.size());
        List<EventoBase> generados = List.copyOf(disciplina.generarEventos(this, getInscritos()));
        if (generados.isEmpty()) throw new ArenaException("La disciplina debe generar eventos.");
        if (new HashSet<>(generados).size() != generados.size()) throw new ArenaException("La disciplina generó eventos duplicados.");
        for (EventoBase evento : generados)
            if (evento.getTorneo() != this || !inscritos.containsAll(evento.getParticipantes())) throw new ArenaException("Evento ajeno al torneo.");
        eventos = generados; estado = EstadoTorneo.EN_JUEGO;
    }
    public void registrarResultado(EventoBase evento, DatosResultado datos) { aplicarResultado(evento, datos, true); }
    void registrarResultadoInicial(EventoBase evento, DatosResultado datos) { aplicarResultado(evento, datos, false); }
    private void aplicarResultado(EventoBase evento, DatosResultado datos, boolean entregarPremio) {
        if (estado != EstadoTorneo.EN_JUEGO || !eventos.contains(evento)) throw new ArenaException("El evento no pertenece a un torneo en juego.");
        if (evento.getEstado() != EstadoEvento.PROGRAMADO) throw new ArenaException("El evento debe estar programado y sin resultado.");
        if (datos == null) throw new ArenaException("Datos de resultado obligatorios.");
        ResultadoEvento resultado = evento.prepararResultado(datos);
        Map<Participante, LineaTabla> nuevaTabla = new LinkedHashMap<>(tabla);
        Set<Participante> incluidos = new HashSet<>();
        for (AporteResultado aporte : resultado.getAportes()) {
            Participante participante = aporte.getParticipante();
            if (!evento.getParticipantes().contains(participante) || !incluidos.add(participante)) throw new ArenaException("Aportes ajenos o duplicados.");
            nuevaTabla.put(participante, tabla.get(participante).sumar(aporte));
        }
        if (incluidos.size() != evento.getParticipantes().size()) throw new ArenaException("Faltan aportes de participantes del evento.");
        boolean termina = eventos.stream().allMatch(e -> e == evento || e.getEstado() == EstadoEvento.FINALIZADO);
        Premio nuevoPremio = null;
        if (termina && entregarPremio) {
            List<LineaTabla> ordenadas = new ArrayList<>(nuevaTabla.values());
            Collections.sort(ordenadas);
            nuevoPremio = new Premio(this, ordenadas.get(0).getParticipante(), disciplina.calcularPremio(inscritos.size()));
        }
        tabla = nuevaTabla;
        evento.finalizar(resultado);
        if (termina) { estado = EstadoTorneo.FINALIZADO; premio = nuevoPremio; }
    }
    public List<LineaTabla> getTabla() {
        List<LineaTabla> ordenadas = new ArrayList<>(tabla.values());
        Collections.sort(ordenadas); return List.copyOf(ordenadas);
    }
    public Participante getCampeon() {
        if (estado != EstadoTorneo.FINALIZADO) throw new ArenaException("Todavía no hay campeón.");
        return getTabla().get(0).getParticipante();
    }
    @Override public String toString() { return codigo + " - " + nombre + " | " + disciplina.getNombre() + " | " + estado; }
}
