import java.io.*;
import java.net.*;

public class LamportThreadReceive extends Thread {

    private int port;
    private int tempo;
    private DatagramSocket socket;

    public LamportThreadReceive(int port) {
        this.port = port;
        this.tempo = 0;
    }

    private void gravarEvento(String evento) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("eventos.txt", true));
        writer.append("\n");
        writer.append(evento);
        writer.close();
    }

    private void enviaMensagem(DatagramPacket datagramPacket, String mensagem) throws IOException {
        InetAddress IPAddress = datagramPacket.getAddress();
        int port = datagramPacket.getPort();
        byte[] sendData = mensagem.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
        socket.send(sendPacket);
    }

    private String atualizaTempoDoEvento(String [] listEvento, int tempo) {
        listEvento[2] = tempo+"";
        String mensagem = "";
        for (String evento: listEvento) {
            mensagem += evento;
        }
        return mensagem;
    }

    public void run() {
        try {
            socket = new DatagramSocket(port);

            while (true) {

                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);
                String evento = new String(receivePacket.getData());
                String[] listEvent = evento.split(" ");
                tempo++;

                String tipo = listEvent[3].trim();

                if (tipo.equals("l")) {
                    int tempoReceive = Integer.parseInt(listEvent[2].trim());
                    if (tempoReceive > tempo) {
                        tempo = tempoReceive;
//                        evento = atualizaTempoDoEvento(listEvent, tempoReceive);
                    }
                    enviaMensagem(receivePacket, evento);
                }

                if (tipo.equals("s")) {
                    int tempoReceive = Integer.parseInt(listEvent[2].trim());
                    if (tempoReceive > tempo ) {
                        tempo = tempoReceive;
//                        evento = atualizaTempoDoEvento(listEvent, tempoReceive);
                    }
                    String receive = Evento.receive(
                            Integer.parseInt(listEvent[4].trim()),
                            tempo,
                            Integer.parseInt(listEvent[1].trim()),
                            tempoReceive
                    );
                    gravarEvento(receive);
                    enviaMensagem(receivePacket, evento);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        socket.close();
    }
}
