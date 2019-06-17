import java.io.*;
import java.net.*;

public class LamportThreadReceive extends Thread {

    private Process process;
    private int tempo;
    private DatagramSocket socket;

    public LamportThreadReceive(Process process) {
        this.process = process;
        this.tempo = 0;
    }

    private void enviaMensagem(DatagramPacket datagramPacket, String mensagem) throws IOException {
        InetAddress IPAddress = datagramPacket.getAddress();
        int port = datagramPacket.getPort();
        byte[] sendData = mensagem.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
        socket.send(sendPacket);
    }

    private void gravarEvento(String evento) throws IOException {
        System.out.println(evento.trim());
        BufferedWriter writer = new BufferedWriter(new FileWriter("eventos.txt", true));
        writer.append("\n");
        writer.append(evento.trim());
        writer.close();
    }

    private String atualizaTempoDoEvento(String [] listEvento) {
        listEvento[2] = tempo+"";
        String mensagem = "";
        for (String evento: listEvento) {
            mensagem += evento + " ";
        }
        return mensagem;
    }

    public void run() {
        try {
            socket = new DatagramSocket(process.getPort());

            while (true) {

                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);
                String evento = new String(receivePacket.getData());
                String[] listEvent = evento.split(" ");

                int idProcessReceive = Integer.parseInt(listEvent[1].trim());
                int tempoReceive = Integer.parseInt(listEvent[2].trim());
                String tpMsg = listEvent[3].trim();

                if (tempoReceive > tempo) {
                    tempo = tempoReceive;
                }

                // mensagem local ou send para o mesmo processo
                if (idProcessReceive == process.getId()) {
                    enviaMensagem(receivePacket, evento);
                }
                if (idProcessReceive != process.getId() && tpMsg.equals("s")) { // mensagem para outro processo
                    String eventoReceive = Evento.receive(
                            Integer.parseInt(listEvent[4].trim()),
                            tempo,
                            Integer.parseInt(listEvent[1].trim()),
                            tempoReceive
                    );
                    gravarEvento(eventoReceive);
                    enviaMensagem(receivePacket, evento);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        socket.close();
    }
}
