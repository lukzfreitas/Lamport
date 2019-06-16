
public class Evento {

    public static String local(int idOrigem, int relogioLocal) {
        return System.currentTimeMillis() + " " + idOrigem + " " + relogioLocal + " l";
    }

    public static String send(int idOrigem, int idDestino, int relogioLocal) {
        return System.currentTimeMillis() + " " + idOrigem + " " + relogioLocal + " s " + idDestino;
    }

    public static String receive(int idDestino, int tempoDestino, int idOrigem, int tempoOrigem) {
        return System.currentTimeMillis() + " " + idDestino + " " + tempoDestino + " r " + idOrigem + " " + tempoOrigem;
    }
}
