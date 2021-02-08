import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Client.fxml"));
        primaryStage.setTitle("Чат");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
        primaryStage.setOnCloseRequest(request -> {
            try {
                System.out.println("[DEBUG] disconnect");
                Network network = Network.getInstance();
                if (network != null) {
                    network.writeMessage("/quit");
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
    }
}
