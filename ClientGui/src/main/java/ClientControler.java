import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;


import java.io.IOException;


public class ClientControler {
    public TextField fxServer;
    public TextField textMessag;
    public ListView<String> listView;
    public TextField fxPort;
    public TextField fxNickName;
    public Button fxBtnConnect;

    private Network network;

    // отправка по кнопке Send
    public void sendMsg() throws IOException {
        network.writeMessage(textMessag.getText());
        textMessag.clear();
    }

    // отправка по ENTER
    public void onKeyPress(KeyEvent keyEvent) throws IOException {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            network.writeMessage(textMessag.getText());
            textMessag.clear();
        }
    }

    public void getConnect() {
        fxNickName.setDisable(true);
        fxServer.setDisable(true);
        fxPort.setDisable(true);
        fxBtnConnect.setDisable(true);
        Network.setServer(fxServer.getText());
        Network.setPort(Integer.parseInt(fxPort.getText()));
        network = Network.getInstance();
        new Thread(() -> {
            try {
                while (true) {
                    String msg = network.readMessage();
                    if (msg.equals("/quit")) {          // Сообщение на закрытие клиента
                        network.close();
                        break;
                    } else if (msg.equals("/info")) {   // запрос ника от сервера
                        network.writeMessage(fxNickName.getText());
                    } else {
                        Platform.runLater(() -> listView.getItems().add(msg));
                    }
                }
            } catch (IOException ioException) {
                System.err.println("Server was broken.");
                Platform.runLater(() -> listView.getItems().add("Server was broken."));
            }
        }).start();
    }

    // Добавление отправителя по выделению его сообщения "/w nikName msg"
    public void getNic() {
        String selectedItem = listView.getSelectionModel().getSelectedItem();
        String[] tmp = selectedItem.split("( to )|(: )", 2);
        Platform.runLater(() -> textMessag.insertText(0, "/w " + tmp[0] + " "));
        listView.getSelectionModel().clearSelection();
    }
}
