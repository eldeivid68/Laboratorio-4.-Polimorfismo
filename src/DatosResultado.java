import java.util.*;
public final class DatosResultado {
    private final Map<String, String> valores;
    public DatosResultado(Map<String, String> valores) {
        if (valores == null || valores.entrySet().stream().anyMatch(e -> e.getKey() == null || e.getValue() == null))
            throw new ArenaException("Los datos del resultado no pueden ser nulos.");
        this.valores = Map.copyOf(valores);
    }
    public String get(String clave) { return valores.getOrDefault(clave, "").trim(); }
    public int entero(String clave) {
        try {
            int valor = Integer.parseInt(get(clave));
            if (valor < 0) throw new ArenaException("El campo " + clave + " debe ser entero no negativo.");
            return valor;
        } catch (NumberFormatException ex) { throw new ArenaException("El campo " + clave + " debe ser entero no negativo."); }
    }
    public double tiempo(String clave) {
        try {
            double valor = Double.parseDouble(get(clave));
            if (!Double.isFinite(valor) || valor <= 0) throw new ArenaException("El tiempo debe ser finito y mayor que cero.");
            return valor;
        } catch (NumberFormatException ex) { throw new ArenaException("Tiempo inválido: " + clave + "."); }
    }
    public static String texto(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) throw new ArenaException(campo + " no puede estar vacío.");
        return valor.trim();
    }
}
