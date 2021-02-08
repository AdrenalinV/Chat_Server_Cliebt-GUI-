import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ServerChat {
    private static final int DEFAULT_PORT = 8189;
    private final ConcurrentLinkedDeque<ClientHandler> clients;

    public ServerChat(int port) {
        clients = new ConcurrentLinkedDeque<>();
        try (ServerSocket server = new ServerSocket(port)) {
            while (true) {
                Socket socket = server.accept();
                System.out.println("[DEBUG] Client accepted");
                ClientHandler handler = new ClientHandler(this, socket);
                addClient(handler);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            System.err.println("Server was broken");
        }
    }


    public static void main(String[] args) {
        int port = -1;
        if (args != null && args.length == 1) {
            port = Integer.parseInt(args[0]);
        }
        if (port == -1) {
            port = DEFAULT_PORT;
        }
        new ServerChat(port);

    }

    // удаление клиента
    public void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        System.out.println("[DEBUG] client removed from broadcast server");
    }

    // добавление клиента
    public void addClient(ClientHandler clientHandler) {
        clients.add(clientHandler);
    }

    // Общие сообщения
    public void broadCastMessage(String msg) throws IOException {
        for (ClientHandler client : clients) {
            client.sendMsg(msg);
        }
    }

    // Приватные сообщения
    public void privateSendMessage(String sender, String recipient, String msg) throws IOException {
        for (ClientHandler client : clients) {
            if (client.getNickName().equals(sender) || client.getNickName().equals(recipient)) {
                client.sendMsg(msg);
            }
        }
    }

}
