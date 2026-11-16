import java.time.LocalDate;
import java.util.*;
public final class DatosIniciales {
    private DatosIniciales() { }
    public static ArenaGT cargar() {
        ArenaGT sistema = new ArenaGT();
        String[] nombres = {"Ana", "Bruno", "Carla", "Diego", "Elena", "Fabio", "Gabriela", "Hugo", "Irene", "Jorge"};
        for (int i = 0; i < nombres.length; i++) sistema.registrarParticipante("P" + (i + 1), nombres[i], "Empresa " + (i / 2 + 1));
        Torneo futbol = crear(sistema, "F1", "Liga central", sistema.getDisciplinas().get(0), List.of("P1", "P3", "P5", "P7", "P2"));
        Torneo penales = crear(sistema, "FP1", "Copa con penales", sistema.getDisciplinas().get(1), List.of("P1", "P3", "P5", "P7"));
        Torneo baloncesto = crear(sistema, "B1", "Copa empresarial", sistema.getDisciplinas().get(2), List.of("P2", "P4", "P6", "P8"));
        Torneo voleibol = crear(sistema, "V1", "Liga de sets", sistema.getDisciplinas().get(3), List.of("P1", "P4", "P6", "P8"));
        Torneo relevos = crear(sistema, "R1", "Carrera inaugural", sistema.getDisciplinas().get(4), List.of("P2", "P3", "P5", "P7", "P9"));
        crear(sistema, "B2", "Próxima temporada", sistema.getDisciplinas().get(2), List.of("P9", "P10"));
        List<Torneo> activos = List.of(futbol, penales, baloncesto, voleibol, relevos);
        int desplazamiento = 0;
        for (Torneo torneo : activos) {
            sistema.cerrarInscripciones(torneo.getCodigo());
            for (int i = 0; i < torneo.getEventos().size(); i++) {
                sistema.programar(torneo.getCodigo(), i + 1, LocalDate.of(2026, 11, 20).plusDays(i), 8 + desplazamiento, "Sede " + desplazamiento);
            }
            desplazamiento++;
        }
        // Ana y Carla quedan con seis puntos y diferencia +2; Carla tiene más victorias.
        int[][] marcadoresFutbol = {{2,0},{0,0},{0,0},{0,0},{4,0},{1,0},{0,1},{0,0},{0,0},{0,0}};
        for (int i = 0; i < marcadoresFutbol.length; i++) resultado(futbol, i, marcador(marcadoresFutbol[i][0], marcadoresFutbol[i][1]));
        resultado(penales, 0, new DatosResultado(Map.of("a","2","b","2","pa","4","pb","3")));
        resultado(baloncesto, 0, marcador(60, 55));
        resultado(voleibol, 0, marcador(3, 2));
        resultado(relevos, 0, new DatosResultado(Map.of("P2","50.1","P3","51.9","P5","52.4","P7","55.0","P9","D")));
        Torneo casi = crear(sistema, "V2", "Final pendiente", sistema.getDisciplinas().get(3), List.of("P1", "P3", "P5", "P7"));
        sistema.cerrarInscripciones("V2");
        for (int i = 0; i < casi.getEventos().size(); i++) {
            sistema.programar("V2", i + 1, LocalDate.of(2026, 12, 1).plusDays(i), 15, "Gimnasio final");
            if (i < casi.getEventos().size() - 1) resultado(casi, i, marcador(3, 0));
        }
        return sistema;
    }
    private static Torneo crear(ArenaGT sistema, String codigo, String nombre, Disciplina disciplina, List<String> codigos) {
        Torneo torneo = sistema.crearTorneo(codigo, nombre, disciplina);
        for (String participante : codigos) torneo.cargarInscritoInicial(sistema.buscarParticipante(participante));
        return torneo;
    }
    private static DatosResultado marcador(int a, int b) { return new DatosResultado(Map.of("a", "" + a, "b", "" + b)); }
    private static void resultado(Torneo torneo, int indice, DatosResultado datos) { torneo.registrarResultadoInicial(torneo.getEventos().get(indice), datos); }
}
