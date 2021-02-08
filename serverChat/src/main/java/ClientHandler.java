import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final ServerChat server;
    private final Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private String nickName;
    private static int id = 0;

    public ClientHandler(ServerChat server, Socket socket) {
        this.server = server;
        this.socket = socket;
        id++;
        nickName = "user_" + id;
    }

    @Override
    public void run() {
        try {
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            out.writeUTF("/info");                  //Запрос ника от клиента
            out.flush();
            String tmpNick = in.readUTF();
            if (!tmpNick.equals("nickName")) {          // если не значение по умолчанию
                nickName = tmpNick + "_" + id;
            }
            System.out.printf("[DEBUG] client: %S start processing\n", nickName);
            server.broadCastMessage(nickName + ": entered the chat.");
            while (true) {
                String msg = in.readUTF();
                if (msg.equals("/quit")) {              // отправка при закрытие клиента
                    out.writeUTF(msg);
                    out.flush();
                    server.broadCastMessage(nickName + ": exit chat.");
                } else if (msg.startsWith("/w")) {      // Приватные сообщения
                    String[] tmp = msg.split(" ", 3);
                    server.privateSendMessage(this.nickName, tmp[1], this.nickName + " to " + tmp[1] + ": " + tmp[2]);
                } else {
                    server.broadCastMessage(nickName + ": " + msg);
                }
            }
        } catch (IOException ioException) {
            System.err.println("Handled connection was broken");
            server.removeClient(this);
        }
    }

    // отпрвка сообщения текущему пользователю
    public void sendMsg(String msg) throws IOException {
        out.writeUTF(msg);
        out.flush();
    }

    // Получение ника
    public String getNickName() {
        return nickName;
    }
}

