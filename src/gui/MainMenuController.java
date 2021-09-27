package gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class MainMenuController {
    public Pane mainMenuPane;
    public Button settingsButton;
    public Button exitButton;
    public Button startButton;

    @FXML
    public void startButtonHandle(ActionEvent actionEvent) throws Exception {
        Stage stage = (Stage) mainMenuPane.getScene().getWindow();
        Parent sim = FXMLLoader.load(getClass().getResource("simulator.fxml"));
        stage.setScene(new Scene(sim, 1600, 900));
        stage.centerOnScreen();
    }

    @FXML
    public void settingsButtonHandle(ActionEvent actionEvent) throws Exception {
        Stage stage = (Stage) mainMenuPane.getScene().getWindow();
        Parent settings = FXMLLoader.load(getClass().getResource("settings.fxml"));
        stage.setScene(new Scene(settings, 800, 600));
        stage.centerOnScreen();
    }

    @FXML
    public void exitButtonHandle(ActionEvent actionEvent) {
        Platform.exit();
    }
}
