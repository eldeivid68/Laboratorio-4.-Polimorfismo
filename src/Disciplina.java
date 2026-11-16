import java.util.*;
public abstract class Disciplina {
    protected Disciplina() { }
    public abstract String getNombre();
    public abstract int getMinParticipantes();
    public abstract int getMaxParticipantes();
    public abstract double getCuotaInscripcion();
    public final double calcularCotizacion(int cantidadParticipantes) {
        validarCantidad(cantidadParticipantes);
        return getCuotaInscripcion() * cantidadParticipantes;
    }
    public final double calcularPremio(int cantidadParticipantes) { return calcularCotizacion(cantidadParticipantes) * 0.5; }
    public final void validarCantidad(int cantidad) {
        if (cantidad < getMinParticipantes() || cantidad > getMaxParticipantes())
            throw new ArenaException("Cantidad permitida en " + getNombre() + ": " + getMinParticipantes() + " a " + getMaxParticipantes() + ".");
    }
    public abstract List<EventoBase> generarEventos(Torneo torneo, List<Participante> inscritos);
    public abstract List<CampoResultado> getCamposResultado(List<Participante> participantes);
    public abstract ResultadoEvento validarResultado(List<Participante> participantes, DatosResultado datos);
    protected final List<EventoBase> generarEncuentros(Torneo torneo, List<Participante> inscritos) {
        validarCantidad(inscritos.size());
        List<EventoBase> eventos = new ArrayList<>();
        for (int i = 0; i < inscritos.size(); i++)
            for (int j = i + 1; j < inscritos.size(); j++)
                eventos.add(new Encuentro(torneo, inscritos.get(i), inscritos.get(j)));
        return List.copyOf(eventos);
    }
    protected final List<CampoResultado> camposMarcador(List<Participante> participantes) {
        return List.of(new CampoResultado("a", "Marcador de " + participantes.get(0).getNombre()),
                       new CampoResultado("b", "Marcador de " + participantes.get(1).getNombre()));
    }
    protected final ResultadoEvento resultadoDuelo(List<Participante> participantes, int a, int b, int puntosA, int puntosB, int victoriasA, int victoriasB, String detalle) {
        long diferencia = (long) a - b;
        return new ResultadoEvento(List.of(new AporteResultado(participantes.get(0), puntosA, diferencia, victoriasA),
                new AporteResultado(participantes.get(1), puntosB, -diferencia, victoriasB)), detalle);
    }
}
