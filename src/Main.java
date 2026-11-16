import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
public final class Main {
    private final Scanner entrada;
    private final ArenaGT sistema;
    public Main(Scanner entrada, ArenaGT sistema) { this.entrada = entrada; this.sistema = sistema; }
    public static void main(String[] argumentos) { new Main(new Scanner(System.in), DatosIniciales.cargar()).ejecutar(); }
    public void ejecutar() {
        boolean continuar = true;
        while (continuar) {
            System.out.println("\nArenaGT | 1 Cotizar | 2 Inscribir | 3 Registrar participante | 4 Crear torneo | 5 Cerrar inscripciones | 6 Programar/reprogramar | 7 Registrar resultado | 8 Consultar | 9 Reportes | 10 Pruebas | 0 Salir");
            if (!entrada.hasNextLine()) break;
            try {
                int opcion = entero("Opción");
                switch (opcion) {
                    case 0 -> continuar = false;
                    case 1 -> cotizar();
                    case 2 -> inscribir();
                    case 3 -> registrarParticipante();
                    case 4 -> crearTorneo();
                    case 5 -> { sistema.cerrarInscripciones(leer("Código de torneo")); System.out.println("Inscripciones cerradas."); }
                    case 6 -> programar();
                    case 7 -> registrarResultado();
                    case 8 -> consultar();
                    case 9 -> reportes();
                    case 10 -> mostrarPruebas();
                    default -> throw new ArenaException("Opción fuera de rango.");
                }
            } catch (ArenaException ex) { System.out.println("Error: " + ex.getMessage()); }
            catch (NumberFormatException ex) { System.out.println("Error: ingresá un número entero válido."); }
            catch (DateTimeParseException ex) { System.out.println("Error: fecha inválida; usá AAAA-MM-DD."); }
        }
    }
    private String leer(String pregunta) {
        System.out.println(pregunta + ":");
        if (!entrada.hasNextLine()) throw new ArenaException("Fin de la entrada.");
        return entrada.nextLine().trim();
    }
    private int entero(String pregunta) { return Integer.parseInt(leer(pregunta)); }
    private Disciplina elegirDisciplina() {
        List<Disciplina> disciplinas = sistema.getDisciplinas();
        for (int i = 0; i < disciplinas.size(); i++) System.out.println((i + 1) + ". " + disciplinas.get(i).getNombre());
        int opcion = entero("Disciplina");
        if (opcion < 1 || opcion > disciplinas.size()) throw new ArenaException("Disciplina fuera de rango.");
        return disciplinas.get(opcion - 1);
    }
    private void cotizar() {
        Disciplina disciplina = elegirDisciplina();
        System.out.println(moneda(sistema.cotizar(disciplina, entero("Cantidad de participantes"))));
    }
    private void inscribir() { System.out.println("Cobrado: " + moneda(sistema.inscribir(leer("Código de torneo"), leer("Código de participante")))); }
    private void registrarParticipante() { System.out.println(sistema.registrarParticipante(leer("Código"), leer("Nombre"), leer("Empresa"))); }
    private void crearTorneo() {
        String codigo = leer("Código"), nombre = leer("Nombre");
        System.out.println(sistema.crearTorneo(codigo, nombre, elegirDisciplina()));
    }
    private EventoBase elegirEvento(String codigo) {
        List<EventoBase> eventos = sistema.buscarTorneo(codigo).getEventos();
        for (int i = 0; i < eventos.size(); i++) System.out.println((i + 1) + ". " + eventos.get(i).descripcionCalendario());
        return sistema.buscarEvento(codigo, entero("Número de evento"));
    }
    private void programar() {
        EventoBase evento = elegirEvento(leer("Código de torneo"));
        LocalDate fecha = LocalDate.parse(leer("Fecha AAAA-MM-DD"));
        int hora = entero("Hora 0-23");
        sistema.getCalendario().programar(evento, fecha, hora, leer("Sede"));
        System.out.println("Evento programado.");
    }
    private void registrarResultado() {
        EventoBase evento = elegirEvento(leer("Código de torneo"));
        Map<String, String> valores = new LinkedHashMap<>();
        for (CampoResultado campo : evento.getCamposResultado()) valores.put(campo.getClave(), leer(campo.getPregunta()));
        evento.getTorneo().registrarResultado(evento, new DatosResultado(valores));
        System.out.println("Resultado registrado. Estado: " + evento.getTorneo().getEstado());
    }
    private void consultar() {
        int opcion = entero("1 Torneos | 2 Participantes | 3 Calendario general | 4 Calendario por torneo | 5 Calendario por participante");
        switch (opcion) {
            case 1 -> sistema.getTorneos().forEach(System.out::println);
            case 2 -> { List<Participante> lista = new ArrayList<>(sistema.getParticipantes()); Collections.sort(lista); lista.forEach(System.out::println); }
            case 3 -> mostrarCalendario(sistema.getCalendario());
            case 4 -> mostrarCalendario(sistema.getCalendario().porTorneo(sistema.buscarTorneo(leer("Código de torneo"))));
            case 5 -> mostrarCalendario(sistema.getCalendario().porParticipante(sistema.buscarParticipante(leer("Código de participante"))));
            default -> throw new ArenaException("Consulta fuera de rango.");
        }
    }
    private void mostrarCalendario(Iterable<Programable> eventos) { for (Programable evento : eventos) System.out.println(evento.descripcionCalendario()); }
    private void reportes() {
        int opcion = entero("1 Tabla y campeón | 2 Ingresos | 3 Premios | 4 Ranking de empresas");
        switch (opcion) {
            case 1 -> {
                Torneo torneo = sistema.buscarTorneo(leer("Código de torneo"));
                torneo.getTabla().forEach(System.out::println);
                if (torneo.getEstado() == EstadoTorneo.FINALIZADO) System.out.println("Campeón: " + torneo.getCampeon());
                else System.out.println("Campeón pendiente.");
            }
            case 2 -> { System.out.println("Total: " + moneda(sistema.getIngresosTotal())); sistema.getIngresosPorDisciplina().forEach((nombre, total) -> System.out.println(nombre + ": " + moneda(total))); }
            case 3 -> sistema.getPremios().forEach(System.out::println);
            case 4 -> sistema.getRankingEmpresas().forEach(System.out::println);
            default -> throw new ArenaException("Reporte fuera de rango.");
        }
    }
    private void mostrarPruebas() {
        System.out.println("INICIO_PRUEBAS");
        System.out.println("Entrada\tEsperado\tObtenido");
        List<List<String>> filas = PruebasArenaGT.ejecutar();
        for (List<String> fila : filas) System.out.println(String.join("\t", fila));
        System.out.println("FIN_PRUEBAS");
    }
    private static String moneda(double monto) { return String.format(Locale.US, "Q%.2f", monto); }
}
