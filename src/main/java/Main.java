import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import Chatterbox.Chatterbox;

/**
 * A GUI for Duke using FXML.
 */
public class Main extends Application {

    private Chatterbox chatterbox = new Chatterbox("./data/chatterbox.txt");

    @Override
    public void start(Stage stage) {
        assert stage != null : "Stage must be provided by JavaFX runtime";
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/View/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setMinHeight(220);
            stage.setMinWidth(417);
            stage.setMaxWidth(417);
            MainWindow controller = fxmlLoader.getController();
            assert controller != null : "MainWindow controller must be available";
            controller.setChatterbox(chatterbox);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
