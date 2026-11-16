public final class LineaRankingEmpresa implements Comparable<LineaRankingEmpresa> {
    private final Empresa empresa;
    private final long puntos;
    public LineaRankingEmpresa(Empresa empresa, long puntos) {
        if (empresa == null || puntos < 0) throw new ArenaException("Ranking de empresa inválido.");
        this.empresa = empresa; this.puntos = puntos;
    }
    public Empresa getEmpresa() { return empresa; }
    public long getPuntos() { return puntos; }
    @Override public int compareTo(LineaRankingEmpresa otra) {
        int orden = Long.compare(otra.puntos, puntos);
        return orden == 0 ? empresa.getNombre().compareTo(otra.empresa.getNombre()) : orden;
    }
    @Override public String toString() { return empresa.getNombre() + ": " + puntos + " pts"; }
}
