import java.time.LocalDate;
import java.util.List;
public interface Programable extends Comparable<Programable> {
    LocalDate getFecha();
    int getHora();
    String getSede();
    boolean estaProgramado();
    List<Participante> getParticipantes();
    Torneo getTorneo();
    String descripcionCalendario();
}
