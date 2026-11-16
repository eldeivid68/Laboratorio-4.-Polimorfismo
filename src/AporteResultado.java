public final class AporteResultado {
    private final Participante participante;
    private final int puntos;
    private final long diferencia;
    private final int victorias;
    public AporteResultado(Participante participante, int puntos, long diferencia, int victorias) {
        if (participante == null || puntos < 0 || victorias < 0 || victorias > 1)
            throw new ArenaException("Aporte de resultado inválido.");
        this.participante = participante; this.puntos = puntos;
        this.diferencia = diferencia; this.victorias = victorias;
    }
    public Participante getParticipante() { return participante; }
    public int getPuntos() { return puntos; }
    public long getDiferencia() { return diferencia; }
    public int getVictorias() { return victorias; }
}
