import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Random;

public class LamportThreadSend extends Thread {

    private int idOrigem;
    private ArrayList<Process> processes;
    private int tempo;
    private int port;

    public LamportThreadSend(int idOrigem, ArrayList<Process> processes, int port) {
        this.idOrigem = idOrigem;
        this.processes = processes;
        this.port = port;
        this.tempo = 0;
    }

    private void gravarEvento(String evento) throws IOException {
        System.out.println(evento.trim());
        BufferedWriter writer = new BufferedWriter(new FileWriter("eventos.txt", true));
        writer.append("\n");
        writer.append(evento.trim());
        writer.close();
    }

    private String atualizaTempoDoEvento(String [] listEvento, int tempo) {
        this.tempo = tempo;
        listEvento[2] = tempo+"";
        String mensagem = "";
        for (String evento: listEvento) {
            mensagem += evento + " ";
        }
        return mensagem;
    }

    public void run() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            while (tempo < 1000) {
                try {
                    tempo++;
                    Random gerador = new Random();
                    int indexDestino = gerador.nextInt(processes.size());
                    Process processDestino = processes.get(indexDestino);
                    InetAddress IPAddress = InetAddress.getByName(processDestino.getHost());
                    while (!IPAddress.isReachable(1000)) {
                        indexDestino = gerador.nextInt(processes.size());
                        processDestino = processes.get(indexDestino);
                        IPAddress = InetAddress.getByName(processDestino.getHost());
                    }

                    String evento = "";

                    // Envia Mensagem
                    if (idOrigem == processDestino.getId()) {
                        evento = Evento.local(idOrigem, tempo);
                    } else {
                        evento = Evento.send(idOrigem, processDestino.getId(), tempo);
                    }
                    byte[] sendData = evento.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                    socket.send(sendPacket);


                    // Recebe Mensagem
                    byte[] receiveData = new byte[1024];
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    socket.receive(receivePacket);
                    String mensagem = new String(receivePacket.getData());
                    String[] listEvent = mensagem.split(" ");
                    int tempoReceive = Integer.parseInt(listEvent[2].trim());
                    mensagem = atualizaTempoDoEvento(listEvent, tempoReceive);
                    gravarEvento(mensagem);

                    try {
                        Thread.sleep(2000);
                    } catch(InterruptedException ex) {

                    }

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        socket.close();
    }

}
