
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final ServerChat server;
    private final Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String nickName;

    public ClientHandler(ServerChat server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            Object msg;
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            authentication();
            while (true) {
                msg = in.readObject();
                if (msg instanceof QuitRequest) {              // отправка при закрытие клиента
                    out.writeObject(msg);
                    out.flush();
                } else if (msg instanceof TextMessage) {      //  сообщения
                    TextMessage tMSG = (TextMessage) msg;
                    if (tMSG.getRecipient() != null) {
                        server.privateSendMessage(tMSG);
                    } else {
                        server.broadCastMessage(tMSG);
                    }
                }
            }
        } catch (ClassNotFoundException | IOException ioException) {
            System.err.println("Handled connection was broken");
            try {
                server.removeClient(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void authentication() throws IOException, ClassNotFoundException {
        Object msg = new AuthenticationRequest();
        out.writeObject(msg);
        out.flush();
        while (true) {
            msg = in.readObject();
            if (msg instanceof AuthenticationRequest) {
                AuthenticationRequest aMSG = (AuthenticationRequest) msg;
                String nick = server.getAuthService().getNickByLoginPass(aMSG.getLogin(), aMSG.getPass());
                if (nick != null) {
                    if (!server.isNickBusy(nick)) {
                        aMSG.setNick(nick);
                        aMSG.setStat(true);
                        out.writeObject(aMSG);
                        out.flush();
                        nickName = nick;
                        server.addClient(this);
                        return;
                    } else {
                        sendMsg(TextMessage.of("Service", "Учетная запись уже используется"));
                        sendMsg(aMSG);

                    }
                } else {
                    sendMsg(TextMessage.of("Service", "Неверные логин/пароль"));
                    sendMsg(aMSG);
                }
            }
        }
    }


    // отпрвка сообщения текущему пользователю
    public void sendMsg(Object msg) throws IOException {
        out.writeObject(msg);
        out.flush();
    }

    public String getNickName() {
        return nickName;
    }
}

