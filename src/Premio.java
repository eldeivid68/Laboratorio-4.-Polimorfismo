public final class Premio {
    private final Torneo torneo;
    private final Participante ganador;
    private final double monto;
    public Premio(Torneo torneo, Participante ganador, double monto) {
        if (torneo == null || ganador == null || !Double.isFinite(monto) || monto < 0) throw new ArenaException("Premio inválido.");
        this.torneo = torneo; this.ganador = ganador; this.monto = monto;
    }
    public Torneo getTorneo() { return torneo; }
    public Participante getGanador() { return ganador; }
    public double getMonto() { return monto; }
    @Override public String toString() { return torneo.getCodigo() + " | " + ganador.getNombre() + " | " + String.format(java.util.Locale.US, "Q%.2f", monto); }
}
