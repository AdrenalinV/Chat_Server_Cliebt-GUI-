import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;


import java.io.IOException;


public class ClientControler {
    public TextField fxServer;
    public TextField textMessag;
    public ListView listView;
    public TextField fxPort;
    public Label fxNickName;
    public Button fxBtnConnect;
    public ListView fxUsersList;
    public Button fxSendBtn;
    private Status Autent = Status.notAuthorized;
    private Network network;

    enum Status {notAuthorized, requestNewUser, createUser, Authorized}

    ;

    // отправка по кнопке Send
    public void sendMsg() throws IOException {
        setMessage(textMessag.getText());
    }

    // отправка по ENTER
    public void onKeyPress(KeyEvent keyEvent) throws IOException {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            setMessage(textMessag.getText());

        }
    }

    private void setMessage(String msg) throws IOException {
        switch (Autent) {
            case notAuthorized:
                String[] arr = msg.split(" ", 2);
                if (arr.length == 2) {
                    network.sending(new AuthenticationRequest(arr[0], arr[1]));
                } else {
                    Platform.runLater(() -> listView.getItems().add("Введите : Логин Пароль"));
                }
                break;
            case requestNewUser:
                if (msg.startsWith("Yes")) {
                    Autent = Status.createUser;
                    Platform.runLater(() -> listView.getItems().add("Создание пользователя! \nВведите: Логин Пароль"));
                } else {
                    Autent = Status.notAuthorized;
                }
                break;
            case createUser:
                String[] arrUser = msg.split(" ", 2);
                if (arrUser.length == 2) {
                    network.sending(CreateUserRequest.of(arrUser[0], arrUser[1]));
                    Autent = Status.notAuthorized;
                } else {
                    Platform.runLater(() -> listView.getItems().add("Введите : Логин Пароль"));
                }
                break;
            case Authorized:
                String recipient = (String) fxUsersList.getSelectionModel().getSelectedItem();
                network.sending(TextMessage.of(fxNickName.getText(), recipient, msg));
                fxUsersList.getSelectionModel().clearSelection();
                break;
        }
        textMessag.clear();
    }

    public void getConnect() {
        Network.setServer(fxServer.getText());
        Network.setPort(Integer.parseInt(fxPort.getText()));
        network = Network.getInstance();
        if (network == null) {
            Platform.runLater(() -> listView.getItems().add("Service: " + "Проблемы с подключением!"));
            return;
        }
        fxServer.setDisable(true);
        fxPort.setDisable(true);
        fxBtnConnect.setDisable(true);
        fxBtnConnect.setTextFill(Color.valueOf("GREEN"));
        textMessag.setDisable(false);
        fxSendBtn.setDisable(false);
        Autent = Status.notAuthorized;
        new Thread(() -> {
            try {
                while (true) {
                    Object msg = network.readMessage();
                    if (msg instanceof QuitRequest) {          // Сообщение на закрытие клиента
                        network.close();
                        setInitialState();
                        break;
                    } else if (msg instanceof AuthenticationRequest) {   // запрос ника от сервера
                        AuthenticationRequest aMSG = (AuthenticationRequest) msg;
                        if (aMSG.isStat()) {
                            Autent = Status.Authorized;
                            fxNickName.setTextFill(Color.valueOf("LIME"));
                            Platform.runLater(() -> fxNickName.setText(aMSG.getNick()));
                            Platform.runLater(() -> listView.getItems().clear());
                        }
                    } else if (msg instanceof TextMessage) {
                        TextMessage tMSG = (TextMessage) msg;
                        Platform.runLater(() -> listView.getItems().add(tMSG.getSender() + ": " + tMSG.getMessage()));
                    } else if (msg instanceof UserListMSG) {
                        Platform.runLater(() -> fxUsersList.getItems().clear());
                        UserListMSG uMSG = (UserListMSG) msg;
                        Platform.runLater(() -> fxUsersList.getItems().addAll(uMSG.getUsers()));
                    } else if (msg instanceof CreateUserRequest) {
                        CreateUserRequest cMSG = (CreateUserRequest) msg;
                        Autent = Status.requestNewUser;
                        Platform.runLater(() -> listView.getItems().add("Service: " + "Создать пользователя? Yes/No"));
                    }
                }
            } catch (IOException | ClassNotFoundException ioException) {
                System.err.println("Server was broken.");
                Platform.runLater(() -> listView.getItems().add("Server was broken."));

            }
        }).start();
    }

    public void setInitialState() {
        textMessag.clear();
        fxServer.setDisable(false);
        fxPort.setDisable(false);
        fxBtnConnect.setDisable(false);
        fxBtnConnect.setTextFill(Color.valueOf("Red"));
        textMessag.setDisable(true);
        fxSendBtn.setDisable(true);
    }
}
