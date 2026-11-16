import java.time.LocalDate;
import java.util.*;
import java.util.function.Supplier;
public final class PruebasArenaGT {
    private PruebasArenaGT() { }
    public static List<List<String>> ejecutar() {
        List<List<String>> filas = new ArrayList<>();
        List<Disciplina> disciplinas = new ArenaGT().getDisciplinas();
        double[] cuotas = {600,750,500,450,300};
        for (int i = 0; i < disciplinas.size(); i++) {
            Disciplina disciplina = disciplinas.get(i);
            double cuota = cuotas[i];
            probar(filas, disciplina.getNombre() + ": cuota / cotización(5) / premio(5)", formato(cuota) + " / " + formato(cuota*5) + " / " + formato(cuota*2.5),
                    () -> formato(disciplina.getCuotaInscripcion()) + " / " + formato(disciplina.calcularCotizacion(5)) + " / " + formato(disciplina.calcularPremio(5)));
        }
        List<Participante> participantes = participantes(8);
        probar(filas, "Fútbol sala 4-1", "3,3,1 / 0,-3,0", () -> aportes(new FutbolSala(), participantes, datos("a","4","b","1")));
        probar(filas, "Fútbol sala 2-2", "1,0,0 / 1,0,0", () -> aportes(new FutbolSala(), participantes, datos("a","2","b","2")));
        probar(filas, "Penales: reglamentario 1-3", "0,-2,0 / 3,2,1", () -> aportes(new FutbolSalaConPenales(), participantes, datos("a","1","b","3")));
        probar(filas, "Penales: 2-2, tanda 5-4", "2,0,1 / 1,0,0", () -> aportes(new FutbolSalaConPenales(), participantes, datos("a","2","b","2","pa","5","pb","4")));
        probar(filas, "Baloncesto 60-55", "2,5,1 / 1,-5,0", () -> aportes(new Baloncesto(), participantes, datos("a","60","b","55")));
        for (String[] caso : List.of(new String[]{"3","0","3,3,1 / 0,-3,0"}, new String[]{"3","1","3,2,1 / 0,-2,0"}, new String[]{"3","2","2,1,1 / 1,-1,0"}, new String[]{"2","3","1,-1,0 / 2,1,1"}))
            probar(filas, "Voleibol " + caso[0] + "-" + caso[1], caso[2], () -> aportes(new Voleibol(), participantes, datos("a",caso[0],"b",caso[1])));
        probar(filas, "Relevos 50.1,51.9,52.4,55.0,D", "10,0,1 / 8,0,0 / 6,0,0 / 5,0,0 / 0,0,0", () -> resumen(new Relevos().validarResultado(participantes.subList(0,5), datos("P1","50.1","P2","51.9","P3","52.4","P4","55.0","P5","D"))));
        probar(filas, "Relevos ocho lugares", "10,0,1 / 8,0,0 / 6,0,0 / 5,0,0 / 4,0,0 / 3,0,0 / 2,0,0 / 1,0,0", () -> resumen(new Relevos().validarResultado(participantes, datos("P1","1","P2","2","P3","3","P4","4","P5","5","P6","6","P7","7","P8","8"))));
        probar(filas, "Igualdad/hash por nombre de empresa y otro objeto", "true / true / false / false", () -> { Empresa a = new Empresa("A"), b = new Empresa("A"); return a.equals(b) + " / " + (a.hashCode()==b.hashCode()) + " / " + a.equals("A") + " / " + a.equals(null); });
        probar(filas, "Tabla: prioridad de puntos", "B,A", () -> orden(linea("A",2,99,1), linea("B",3,-9,0)));
        probar(filas, "Tabla: empate en puntos, diferencia", "B,A", () -> orden(linea("A",3,1,1), linea("B",3,2,0)));
        probar(filas, "Tabla: empate puntos/diferencia, victorias", "B,A", () -> orden(linea("A",3,1,0), linea("B",3,1,1)));
        probar(filas, "Tabla: empate completo, nombre", "A,B", () -> orden(linea("B",3,1,1), linea("A",3,1,1)));
        probar(filas, "Ranking empresas: puntos y nombre", "Z,A,B", () -> { List<LineaRankingEmpresa> ranking = new ArrayList<>(List.of(new LineaRankingEmpresa(new Empresa("B"),5),new LineaRankingEmpresa(new Empresa("Z"),6),new LineaRankingEmpresa(new Empresa("A"),5))); Collections.sort(ranking); return String.join(",",ranking.stream().map(l -> l.getEmpresa().getNombre()).toList()); });
        probar(filas, "Ranking suma estadísticas en dos torneos", "true", () -> {
            ArenaGT sistema = escenario();
            Torneo uno = sistema.buscarTorneo("T");
            Torneo dos = sistema.crearTorneo("T2","Otro",sistema.getDisciplinas().get(0));
            for (Participante participante : sistema.getParticipantes()) dos.cargarInscritoInicial(participante);
            sistema.cerrarInscripciones("T2");
            sistema.programar("T",1,LocalDate.of(2027,1,1),8,"A");
            sistema.programar("T2",1,LocalDate.of(2027,1,1),9,"A");
            uno.registrarResultado(uno.getEventos().get(0),datos("a","2","b","0"));
            dos.registrarResultado(dos.getEventos().get(0),datos("a","1","b","0"));
            return "" + (sistema.getRankingEmpresas().get(0).getPuntos()==6);
        });
        probar(filas, "Calendario mezcla carrera/encuentros; fecha, hora y sede", "R,T,U,V", () -> {
            ArenaGT sistema = escenario();
            Disciplina carrera = sistema.getDisciplinas().get(4);
            for (int i=5; i<=12; i++) sistema.registrarParticipante("P"+i,"N"+i,"E"+i);
            Torneo r = sistema.crearTorneo("R","Carrera",carrera);
            for(int i=5;i<=7;i++) r.cargarInscritoInicial(sistema.buscarParticipante("P"+i));
            sistema.cerrarInscripciones("R");
            Torneo u = sistema.crearTorneo("U","Paralelo",carrera), v=sistema.crearTorneo("V","Paralelo 2",carrera);
            for(int i=8;i<=10;i++) u.cargarInscritoInicial(sistema.buscarParticipante("P"+i));
            for(int i : new int[]{5,11,12}) v.cargarInscritoInicial(sistema.buscarParticipante("P"+i));
            sistema.cerrarInscripciones("U"); sistema.cerrarInscripciones("V");
            LocalDate fecha=LocalDate.of(2027,1,2);
            sistema.programar("V",1,fecha,10,"Z"); sistema.programar("U",1,fecha,10,"A");
            sistema.programar("T",1,fecha,9,"Z"); sistema.programar("R",1,fecha.minusDays(1),22,"Z");
            List<String> orden = new ArrayList<>(); for(Programable e:sistema.getCalendario()) orden.add(e.getTorneo().getCodigo()); return String.join(",",orden);
        });
        probar(filas, "Vistas filtradas Iterable y sin modificar originales", "1 / 1 / true", () -> {
            ArenaGT sistema=escenario(); sistema.programar("T",1,LocalDate.of(2027,1,1),8,"A");
            Iterable<Programable> t=sistema.getCalendario().porTorneo(sistema.buscarTorneo("T"));
            Iterable<Programable> p=sistema.getCalendario().porParticipante(sistema.buscarParticipante("P1"));
            Iterator<Programable> iterador=t.iterator(); iterador.next(); boolean protegido=false;
            try {iterador.remove();} catch(UnsupportedOperationException ex){protegido=true;}
            return contar(t)+" / "+contar(p)+" / "+protegido;
        });
        probar(filas, "Reprogramar mismo evento ignorándolo", "2027-01-01 / 10 / A", () -> {
            ArenaGT sistema=escenario(); sistema.programar("T",1,LocalDate.of(2027,1,1),8,"A"); sistema.programar("T",1,LocalDate.of(2027,1,1),8,"A"); sistema.programar("T",1,LocalDate.of(2027,1,1),10,"A");
            EventoBase evento=sistema.buscarEvento("T",1); return evento.getFecha()+" / "+evento.getHora()+" / "+evento.getSede();
        });
        probar(filas, "Finalización automática V2: último 3-0; repetir resultado", "FINALIZADO / Q900.00 / 1 / rechazo sin cambios", () -> {
            ArenaGT sistema=DatosIniciales.cargar(); Torneo torneo=sistema.buscarTorneo("V2");
            sistema.registrarResultado("V2",6,datos("a","3","b","0"));
            String antes=huella(sistema); boolean rechazo=rechaza(()->sistema.registrarResultado("V2",6,datos("a","3","b","0")));
            return torneo.getEstado()+" / "+formato(torneo.getPremio().orElseThrow().getMonto())+" / "+sistema.getPremios().size()+" / "+(rechazo&&antes.equals(huella(sistema))?"rechazo sin cambios":"FALLO");
        });
        probar(filas, "Datos iniciales: 7 torneos, 10 participantes, cero ingresos/premios", "7 / 10 / Q0.00 / 0", () -> {ArenaGT sistema=DatosIniciales.cargar();return sistema.getTorneos().size()+" / "+sistema.getParticipantes().size()+" / "+formato(sistema.getIngresosTotal())+" / "+sistema.getPremios().size();});
        probar(filas, "Inicial F1: Carla/Ana puntos, diferencia y victorias", "6,2,2 / 6,2,1", () -> {
            Torneo torneo=DatosIniciales.cargar().buscarTorneo("F1"); Map<String,LineaTabla> tabla=new HashMap<>();for(LineaTabla l:torneo.getTabla())tabla.put(l.getParticipante().getNombre(),l);
            LineaTabla a=tabla.get("Carla"),b=tabla.get("Ana");return a.getPuntos()+","+a.getDiferencia()+","+a.getVictorias()+" / "+b.getPuntos()+","+b.getDiferencia()+","+b.getVictorias();
        });
        probar(filas, "Cobro único y contabilidad por disciplina", "Q600.00 / Q600.00 / Q600.00", () -> {
            ArenaGT sistema=new ArenaGT();sistema.registrarParticipante("P","Persona","Empresa");sistema.crearTorneo("T","Torneo",sistema.getDisciplinas().get(0));
            double cobrado=sistema.inscribir("T","P");return formato(cobrado)+" / "+formato(sistema.getIngresosTotal())+" / "+formato(sistema.getIngresosPorDisciplina().get("Fútbol sala"));
        });
        probar(filas, "Un participante en dos torneos conserva estadísticas separadas", "3 / 0", () -> {
            ArenaGT sistema=escenario();Torneo otro=sistema.crearTorneo("U","Otro",sistema.getDisciplinas().get(0));otro.cargarInscritoInicial(sistema.buscarParticipante("P1"));
            sistema.programar("T",1,LocalDate.of(2027,1,1),8,"A");sistema.registrarResultado("T",1,datos("a","1","b","0"));
            return sistema.buscarTorneo("T").getTabla().get(0).getPuntos()+" / "+otro.getTabla().get(0).getPuntos();
        });
        atomicidad(filas);
        return List.copyOf(filas);
    }
    private static void atomicidad(List<List<String>> filas) {
        ArenaGT sistema=escenario();
        probar(filas,"Cotizar Fútbol(5): tabla e ingresos antes/después",huella(sistema)+" => "+huella(sistema),()->{String antes=huella(sistema);sistema.cotizar(sistema.getDisciplinas().get(0),5);return antes+" => "+huella(sistema);});
        rechazoAtomico(filas,"Cerrar sin mínimo",()->{ArenaGT a=new ArenaGT();a.crearTorneo("X","Vacío",a.getDisciplinas().get(0));return a;}, a->a.cerrarInscripciones("X"));
        rechazoAtomico(filas,"Inscripción duplicada",PruebasArenaGT::enInscripcion,a->a.inscribir("T","P1"));
        rechazoAtomico(filas,"Tercer participante misma empresa",()->{ArenaGT a=enInscripcion();a.registrarParticipante("P3","C","E");return a;},a->a.inscribir("T","P3"));
        rechazoAtomico(filas,"Inscripción con cupo lleno",()->{ArenaGT a=enInscripcion();for(int i=3;i<=8;i++){a.registrarParticipante("P"+i,"N"+i,"E"+i);a.inscribir("T","P"+i);}a.registrarParticipante("P9","N9","E9");return a;},a->a.inscribir("T","P9"));
        rechazoAtomico(filas,"Inscripción con estado EN_JUEGO",PruebasArenaGT::escenario,a->a.inscribir("T","P1"));
        rechazoAtomico(filas,"Resultado sin programar",PruebasArenaGT::escenario,a->a.registrarResultado("T",1,datos("a","1","b","0")));
        rechazoAtomico(filas,"Marcador negativo",PruebasArenaGT::programado,a->a.registrarResultado("T",1,datos("a","-1","b","0")));
        rechazoAtomico(filas,"Marcador no entero",PruebasArenaGT::programado,a->a.registrarResultado("T",1,datos("a","1.5","b","0")));
        rechazoAtomico(filas,"Hora 24",PruebasArenaGT::programado,a->a.programar("T",1,LocalDate.of(2027,1,1),24,"A"));
        rechazoAtomico(filas,"Sede vacía",PruebasArenaGT::programado,a->a.programar("T",1,LocalDate.of(2027,1,1),8," "));
        rechazoAtomico(filas,"Conflicto de sede entre torneos",PruebasArenaGT::conDosTorneos,a->a.programar("U",1,LocalDate.of(2027,1,1),8,"A"));
        rechazoAtomico(filas,"Conflicto de participante entre torneos",PruebasArenaGT::conDosTorneos,a->a.programar("U",1,LocalDate.of(2027,1,1),8,"B"));
        rechazoAtomico(filas,"Reprogramación en conflicto conserva cita previa",()->{ArenaGT a=conDosTorneos();a.programar("U",1,LocalDate.of(2027,1,2),10,"B");return a;},a->a.programar("U",1,LocalDate.of(2027,1,1),8,"A"));
        rechazoAtomico(filas,"Reprogramar finalizado",()->{ArenaGT a=programado();a.registrarResultado("T",1,datos("a","1","b","0"));return a;},a->a.programar("T",1,LocalDate.of(2027,1,2),8,"B"));
        rechazoAtomico(filas,"Baloncesto empatado",()->porDisciplina(2),a->a.registrarResultado("T",1,datos("a","1","b","1")));
        rechazoAtomico(filas,"Voleibol 3-3",()->porDisciplina(3),a->a.registrarResultado("T",1,datos("a","3","b","3")));
        rechazoAtomico(filas,"Penales sin empate reglamentario",()->porDisciplina(1),a->a.registrarResultado("T",1,datos("a","2","b","1","pa","4","pb","3")));
        rechazoAtomico(filas,"Penales empatados",()->porDisciplina(1),a->a.registrarResultado("T",1,datos("a","2","b","2","pa","4","pb","4")));
        rechazoAtomico(filas,"Empate reglamentario sin penales",()->porDisciplina(1),a->a.registrarResultado("T",1,datos("a","2","b","2")));
        for(String tiempo:List.of("0","-1","NaN","Infinity","50"))
            rechazoAtomico(filas,"Relevos tiempo inválido/repetido: "+tiempo,()->porDisciplina(4),a->a.registrarResultado("T",1,datos("P1","50","P2",tiempo,"P3","51","P4","52")));
        probar(filas,"Registro vacío no crea empresa ni participante","true",()->{ArenaGT a=new ArenaGT();boolean r=rechaza(()->a.registrarParticipante("X"," ","Empresa nueva"));return ""+(r&&a.getEmpresas().isEmpty()&&a.getParticipantes().isEmpty());});
        probar(filas,"Códigos repetidos de participante y torneo","true / true",()->{ArenaGT a=escenario();return rechaza(()->a.registrarParticipante("P1","Otro","Otra"))+" / "+rechaza(()->a.crearTorneo("T","Otro",a.getDisciplinas().get(0)));});
        probar(filas,"Rango de cotización inválido no altera estado","true",()->{ArenaGT a=escenario();String antes=huella(a);return ""+(rechaza(()->a.cotizar(a.getDisciplinas().get(0),3))&&antes.equals(huella(a)));});
        probar(filas,"Cerrar inscripciones dos veces no duplica eventos","6 / true / 6",()->{ArenaGT a=escenario();int antes=a.buscarTorneo("T").getEventos().size();boolean r=rechaza(()->a.cerrarInscripciones("T"));return antes+" / "+r+" / "+a.buscarTorneo("T").getEventos().size();});
        probar(filas,"Campeón antes de finalizar","true",()->""+rechaza(()->escenario().buscarTorneo("T").getCampeon()));
        probar(filas,"Estados completos y premio único en carrera","EN_INSCRIPCION / EN_JUEGO / SIN_PROGRAMAR / PROGRAMADO / FINALIZADO / FINALIZADO / Q600.00 / 1",()->{
            ArenaGT a=new ArenaGT();Torneo t=a.crearTorneo("T","Carrera",a.getDisciplinas().get(4));String inicio=t.getEstado().toString();
            for(int i=1;i<=4;i++){a.registrarParticipante("P"+i,"N"+i,"E"+i);a.inscribir("T","P"+i);}a.cerrarInscripciones("T");String juego=t.getEstado().toString(),sin=t.getEventos().get(0).getEstado().toString();
            a.programar("T",1,LocalDate.of(2027,1,1),8,"A");String programado=t.getEventos().get(0).getEstado().toString();a.registrarResultado("T",1,datos("P1","1","P2","2","P3","3","P4","D"));
            return inicio+" / "+juego+" / "+sin+" / "+programado+" / "+t.getEventos().get(0).getEstado()+" / "+t.getEstado()+" / "+formato(a.getPremios().get(0).getMonto())+" / "+a.getPremios().size();
        });
    }
    private static void rechazoAtomico(List<List<String>> filas,String entrada,Supplier<ArenaGT> fabrica,java.util.function.Consumer<ArenaGT> operacion) {
        ArenaGT sistema=fabrica.get();String antes=huella(sistema);
        probar(filas,entrada,"rechazo; "+antes+" => "+antes,()->{boolean rechazo=rechaza(()->operacion.accept(sistema));return (rechazo?"rechazo":"ACEPTADO")+"; "+antes+" => "+huella(sistema);});
    }
    private static ArenaGT escenario() {
        ArenaGT sistema=new ArenaGT();Torneo torneo=sistema.crearTorneo("T","Prueba",sistema.getDisciplinas().get(0));
        for(int i=1;i<=4;i++){Participante participante=sistema.registrarParticipante("P"+i,"N"+i,"E"+i);torneo.cargarInscritoInicial(participante);}sistema.cerrarInscripciones("T");return sistema;
    }
    private static ArenaGT enInscripcion() {
        ArenaGT sistema=new ArenaGT();sistema.crearTorneo("T","Prueba",sistema.getDisciplinas().get(0));
        sistema.registrarParticipante("P1","A","E");sistema.registrarParticipante("P2","B","E");sistema.inscribir("T","P1");sistema.inscribir("T","P2");return sistema;
    }
    private static ArenaGT programado() {ArenaGT sistema=escenario();sistema.programar("T",1,LocalDate.of(2027,1,1),8,"A");return sistema;}
    private static ArenaGT conDosTorneos() {
        ArenaGT sistema=programado();Torneo torneo=sistema.crearTorneo("U","Otro",sistema.getDisciplinas().get(0));for(Participante p:sistema.getParticipantes())torneo.cargarInscritoInicial(p);sistema.cerrarInscripciones("U");return sistema;
    }
    private static ArenaGT porDisciplina(int indice) {
        ArenaGT sistema=new ArenaGT();Torneo torneo=sistema.crearTorneo("T","Prueba",sistema.getDisciplinas().get(indice));
        for(int i=1;i<=4;i++)torneo.cargarInscritoInicial(sistema.registrarParticipante("P"+i,"N"+i,"E"+i));sistema.cerrarInscripciones("T");sistema.programar("T",1,LocalDate.of(2027,1,1),8,"A");return sistema;
    }
    private static String huella(ArenaGT sistema) {
        StringJoiner resumen=new StringJoiner("; ");resumen.add("ingresos="+formato(sistema.getIngresosTotal()));resumen.add("premios="+sistema.getPremios().size());
        for(Torneo torneo:sistema.getTorneos()){
            resumen.add(torneo.getCodigo()+"="+torneo.getEstado()+" "+torneo.getTabla());
            for(EventoBase evento:torneo.getEventos())if(evento.estaProgramado())resumen.add(evento.getFecha()+"/"+evento.getHora()+"/"+evento.getSede()+"/"+evento.getEstado());
        }
        return resumen.toString();
    }
    private static void probar(List<List<String>> filas,String entrada,String esperado,Supplier<String> operacion) {
        String obtenido;try{obtenido=operacion.get();}catch(RuntimeException ex){obtenido="EXCEPCIÓN: "+ex.getMessage();}
        filas.add(List.of(entrada,esperado,esperado.equals(obtenido)?obtenido:"FALLO: "+obtenido));
    }
    private static boolean rechaza(Runnable operacion) {try{operacion.run();return false;}catch(ArenaException ex){return true;}}
    private static DatosResultado datos(String... pares) {Map<String,String> valores=new LinkedHashMap<>();for(int i=0;i<pares.length;i+=2)valores.put(pares[i],pares[i+1]);return new DatosResultado(valores);}
    private static List<Participante> participantes(int cantidad) {List<Participante> lista=new ArrayList<>();for(int i=1;i<=cantidad;i++)lista.add(new Participante("P"+i,"N"+i,new Empresa("E"+i)));return lista;}
    private static String aportes(Disciplina disciplina,List<Participante> participantes,DatosResultado datos) {return resumen(disciplina.validarResultado(participantes.subList(0,2),datos));}
    private static String resumen(ResultadoEvento resultado) {StringJoiner texto=new StringJoiner(" / ");for(AporteResultado a:resultado.getAportes())texto.add(a.getPuntos()+","+a.getDiferencia()+","+a.getVictorias());return texto.toString();}
    private static LineaTabla linea(String nombre,int puntos,long diferencia,int victorias) {Participante p=new Participante(nombre,nombre,new Empresa("E"));return new LineaTabla(p).sumar(new AporteResultado(p,puntos,diferencia,victorias));}
    private static String orden(LineaTabla... lineas) {List<LineaTabla> lista=new ArrayList<>(List.of(lineas));Collections.sort(lista);return String.join(",",lista.stream().map(l->l.getParticipante().getNombre()).toList());}
    private static int contar(Iterable<Programable> vista) {int total=0;for(Programable evento:vista)total++;return total;}
    private static String formato(double monto) {return String.format(Locale.US,"Q%.2f",monto);}
}
