

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Network {
    private static int port = 8189;
    private static String server = "localhost";
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;
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
        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());
    }

    public void writeMessage(String msg) throws IOException {
        out.writeUTF(msg);
        out.flush();
    }

    public String readMessage() throws IOException {
        return in.readUTF();

    }

    public void close() throws IOException {
        out.close();
        in.close();
        socket.close();
    }
}
