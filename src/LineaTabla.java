public final class LineaTabla implements Comparable<LineaTabla> {
    private final Participante participante;
    private final int puntos;
    private final long diferencia;
    private final int victorias;
    public LineaTabla(Participante participante) { this(participante, 0, 0, 0); }
    private LineaTabla(Participante participante, int puntos, long diferencia, int victorias) {
        if (participante == null) throw new ArenaException("Participante obligatorio.");
        this.participante = participante; this.puntos = puntos; this.diferencia = diferencia; this.victorias = victorias;
    }
    public Participante getParticipante() { return participante; }
    public int getPuntos() { return puntos; }
    public long getDiferencia() { return diferencia; }
    public int getVictorias() { return victorias; }
    LineaTabla sumar(AporteResultado aporte) {
        return new LineaTabla(participante, puntos + aporte.getPuntos(), diferencia + aporte.getDiferencia(), victorias + aporte.getVictorias());
    }
    @Override public int compareTo(LineaTabla otra) {
        int orden = Integer.compare(otra.puntos, puntos);
        if (orden == 0) orden = Long.compare(otra.diferencia, diferencia);
        if (orden == 0) orden = Integer.compare(otra.victorias, victorias);
        if (orden == 0) orden = participante.getNombre().compareTo(otra.participante.getNombre());
        return orden;
    }
    @Override public String toString() { return participante.getNombre() + ": " + puntos + " pts, dif " + diferencia + ", vict " + victorias; }
}
