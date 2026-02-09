import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import Chatterbox.Chatterbox;
/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Chatterbox chatterbox;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private Image dukeImage = new Image(this.getClass().getResourceAsStream("/images/DaDuke.png"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the Chatterbox instance */
    public void setChatterbox(Chatterbox cb) {
        chatterbox = cb;
        dialogContainer.getChildren().addAll(
                DialogBox.getDukeDialog(chatterbox.getWelcomeMessage(), dukeImage)
        );
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Duke's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = chatterbox.getResponse(input);
        System.out.println("User input: " + input);
        System.out.println("Chatterbox response: " + response);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
            DialogBox.getDukeDialog(response, dukeImage, chatterbox.getCommandType())
        );
        userInput.clear();
        if (chatterbox.isExit()) {
            Platform.exit();
        }
    }
}
