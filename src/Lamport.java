import java.io.*;
import java.util.*;

public class Lamport {

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("syntax: java Lamport <file> <id>");
            return;
        }
        File file = new File(args[0]);
        Integer id = Integer.parseInt(args[1]);
        Lamport lamport = new Lamport();
        lamport.init(file, id);
    }

    private void init(File file, Integer id) throws IOException {

        BufferedReader br = new BufferedReader(new FileReader(file));

        String st;
        String[] config;
        ArrayList<Process> processes = new ArrayList<>();

        while ((st = br.readLine()) != null) {
            config = st.split(" ");
            Process process = new Process(Integer.parseInt(config[0]), config[1], Integer.parseInt(config[2]));
            processes.add(process.getId() - 1, process);
        }

        Process myProcess = processes.get(id - 1);

        new LamportThreadReceive(myProcess).start();
        new LamportThreadSend(myProcess.getId(), processes, myProcess.getPort()).start();
    }
}