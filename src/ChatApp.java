import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ChatApp extends Application {

    private boolean isServer = false;
    private TextArea messages = new TextArea();
    private NetworkConnection connection = isServer ? createServer() : createClient();

    private Parent createContent() {

        messages.setPrefHeight(250);
        messages.setText("Welcome to SimpleChat v0.3\n");

        TextField input = new TextField();
        input.setPromptText("print your message here...");

        TextField name = new TextField();
        if (!isServer)
            name.setPromptText("username");
        if (isServer)
            name.setDisable(true);

        Button sendButton = new Button("Send");
        sendButton.setOnAction(event -> {
            String message = isServer ? "Server: " : name.getText() + ": ";
            message += input.getText();
            input.clear();
            messages.appendText(message + "\n");
            try {
                connection.send(message);
            } catch (Exception e) {
                messages.appendText("Failed to send\n");
            }
        });

        HBox bottomBar = new HBox(10, name, sendButton);
        bottomBar.setAlignment(Pos.CENTER);
        bottomBar.setHgrow(name, Priority.ALWAYS);

        VBox root = new VBox(10, messages, input, bottomBar);
        root.setAlignment(Pos.CENTER_RIGHT);
        root.setPadding(new Insets(10, 10, 10, 10));
        root.setPrefSize(300, 300);
        return root;
    }

    @Override
    public void init() throws Exception {
        connection.startConnection();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(createContent()));
        primaryStage.setTitle(isServer ? "Server" : "Client");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        connection.closeConnection();
    }

    private Server createServer() {
        return new Server(55555, data -> {
            Platform.runLater(() -> {
                messages.appendText(data.toString() + "\n");
            });
        });
    }

    private Client createClient() {
        return new Client("127.0.0.1", 55555, data -> {
            Platform.runLater(() -> {
                messages.appendText(data.toString() + "\n");
            });
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

}
