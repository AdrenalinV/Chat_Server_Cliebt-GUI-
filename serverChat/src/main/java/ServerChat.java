import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ServerChat {
    private static final int DEFAULT_PORT = 8189;
    private final ConcurrentLinkedDeque<ClientHandler> clients;
    private final AuthService authService;

    // аутентификация
    public AuthService getAuthService() {
        return authService;
    }

    // коннекты и бла бла бла
    public ServerChat(int port) throws SQLException {
        authService = new BaseAuthService();
        authService.start();
        clients = new ConcurrentLinkedDeque<>();
        try (ServerSocket server = new ServerSocket(port)) {
            while (true) {
                Socket socket = server.accept();
                System.out.println("[DEBUG] Client accepted");
                ClientHandler handler = new ClientHandler(this, socket);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            System.err.println("Server was broken");
        } finally {
            if (authService != null) {
                authService.stop();
            }
        }
    }

    // проверка свободен ник или нет
    public boolean isNickBusy(String nick) {
        for (ClientHandler o : clients) {
            if (o.getNickName().equals(nick)) {
                return true;
            }
        }
        return false;
    }


    public static void main(String[] args) {
        int port = -1;
        if (args != null && args.length == 1) {
            port = Integer.parseInt(args[0]);
        }
        if (port == -1) {
            port = DEFAULT_PORT;
        }
        try {
            new ServerChat(port);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    // удаление клиента
    public void removeClient(ClientHandler clientHandler) throws IOException {
        clients.remove(clientHandler);
        System.out.println("[DEBUG] client removed from broadcast server");
        getUsersList();

    }

    // добавление клиента
    public void addClient(ClientHandler clientHandler) throws IOException {
        clients.add(clientHandler);
        getUsersList();
    }

    // создание списка активных клиентов
    private void getUsersList() throws IOException {
        ArrayList<String> users = new ArrayList<>();
        for (ClientHandler o : clients) {
            users.add(o.getNickName());
        }
        Collections.sort(users);
        broadCastMessage(new UserListMSG(users));
    }


    // Общие сообщения
    public void broadCastMessage(Object msg) throws IOException {
        for (ClientHandler client : clients) {
            client.sendMsg(msg);
        }
    }

    // Приватные сообщения
    public void privateSendMessage(TextMessage msg) throws IOException {
        for (ClientHandler client : clients) {
            if (client.getNickName().equals(msg.getSender()) || client.getNickName().equals(msg.getRecipient())) {
                client.sendMsg(msg);
            }
        }
    }
}
