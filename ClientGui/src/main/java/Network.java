

import java.io.*;
import java.net.Socket;

public class Network {
    private static int port = 8189;
    private static String server = "localhost";
    private final Socket socket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private static Network instance;

    public static Network getInstance() {
        if (instance == null) {
            System.out.println("[DEBUG] new Network");
            try {
                instance = new Network();
            } catch (IOException ioException) {
                System.err.println("Problem server");
                instance = null;
            }
        }
        return instance;
    }

    public static void setPort(int port) {
        Network.port = port;
    }

    public static void setServer(String server) {
        Network.server = server;
    }


    private Network() throws IOException {
        socket = new Socket(server, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }

    public void sending(AbstractMSG msg) throws IOException {
        out.writeObject(msg);
        out.flush();
    }

    public AbstractMSG readMessage() throws ClassNotFoundException, IOException {
        return (AbstractMSG) in.readObject();

    }

    public void close() throws IOException {
        out.close();
        in.close();
        socket.close();
    }
}
