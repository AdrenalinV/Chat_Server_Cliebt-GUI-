
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
@Log4j2
public class ClientHandler implements Runnable {
    public static final Logger slog = LogManager.getLogger("Secure");
    private final ServerChat server;
    private final Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String nickName;
    private static final long WAIT_TIME_MS = 120000L;

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

            Thread tA = new Thread(() -> {
                try {
                    authentication();
                } catch (IOException | ClassNotFoundException ioException) {
                }
            });
            tA.start();
            tA.join(WAIT_TIME_MS);
            if (tA.isAlive()) {
                log.trace("timeout expired");
                sendMsg(TextMessage.of("Service", "Соединение разорвано."));
                sendMsg(new QuitRequest());
            }
            org.apache.logging.log4j.ThreadContext.put("user", nickName);
            log.info("authentication completed");
            while (true) {
                msg = in.readObject();
                if (msg instanceof QuitRequest) {              // отправка при закрытие клиента
                    log.trace("request quit");
                    out.writeObject(msg);
                    out.flush();
                } else if (msg instanceof TextMessage) {      //  сообщения
                    TextMessage tMSG = (TextMessage) msg;
                    if (tMSG.getRecipient() != null) {
                        server.privateSendMessage(tMSG);
                    } else {
                        server.broadCastMessage(tMSG);
                    }
                }else if (msg instanceof UpdateNickRequest){
                    UpdateNickRequest uMSG = (UpdateNickRequest) msg;
                    if(!server.isNickBusy(uMSG.getNewNick())){
                        server.getAuthService().updateNickName(uMSG.getNewNick(), this.nickName);
                        this.nickName= uMSG.getNewNick();
                        server.getUsersList();
                        log.info("Update nickName");
                        org.apache.logging.log4j.ThreadContext.put("user", nickName);
                    }
                }
            }
        } catch (ClassNotFoundException | IOException | InterruptedException ioException) {
            log.debug("user connection was broken");
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
            sendMsg(TextMessage.of("Service", "Введите: Логин Пароль."));
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
                        slog.info("authentication completed User: {} nikName: {}",aMSG.getLogin(),nickName);
                        server.addClient(this);
                        return;
                    } else {
                        sendMsg(TextMessage.of("Service", "Учетная запись уже используется"));
                        slog.warn("authentication error User: {} already in use",aMSG.getLogin());
                        sendMsg(aMSG);

                    }
                } else {
                    if (server.getAuthService().existUser(aMSG.getLogin())) {
                        sendMsg(TextMessage.of("Service", "Неверный пароль."));
                        slog.warn("authentication error User: {} password: {}",aMSG.getLogin(),aMSG.getPass());
                    } else {
                        sendMsg(TextMessage.of("Service", "Пользователь не найден."));
                        slog.warn("authentication error User: {} not found",aMSG.getLogin());
                        sendMsg(aMSG);
                        sendMsg(CreateUserRequest.of());
                    }
                }
            } else if (msg instanceof QuitRequest) {
                sendMsg(msg);
            } else if (msg instanceof CreateUserRequest) {
                CreateUserRequest cMSG = (CreateUserRequest) msg;
                if (cMSG.getLogin() != null && cMSG.getPassword() != null) {
                    server.getAuthService().createUser(cMSG.getLogin(), cMSG.getPassword());
                }
                sendMsg(new AuthenticationRequest());
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

