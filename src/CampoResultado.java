public final class CampoResultado {
    private final String clave;
    private final String pregunta;
    public CampoResultado(String clave, String pregunta) {
        this.clave = DatosResultado.texto(clave, "Clave");
        this.pregunta = DatosResultado.texto(pregunta, "Pregunta");
    }
    public String getClave() { return clave; }
    public String getPregunta() { return pregunta; }
}
