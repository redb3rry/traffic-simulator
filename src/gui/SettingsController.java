package gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class SettingsController {
    public TextField aggressiveMod;
    public TextField standardMod;
    public TextField carefulMod;
    public TextField simLengthField;
    public TextField maxCarsField;
    public ComboBox routerSelect;
    public ComboBox weatherSelect;
    public GridPane settingsGrid;
    public Label cityChooserLabel;
    public Label outputChooserLabel;
    private Properties appProps;
    private String cityPath ="src/city.txt";
    private String outputPath = "src/output.txt";

    public void initialize() {
        appProps = new Properties();
        try {
            appProps.load(new FileInputStream("src/app.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        maxCarsField.setText(appProps.getProperty("carMax"));
        simLengthField.setText(appProps.getProperty("simulationTime"));
        weatherSelect.setValue(appProps.getProperty("weather"));
        routerSelect.setValue(appProps.getProperty("router"));
        carefulMod.setText(appProps.getProperty("carefulDriverModifier"));
        standardMod.setText(appProps.getProperty("standardDriverModifier"));
        aggressiveMod.setText(appProps.getProperty("aggressiveDriverModifier"));
    }

    @FXML
    public void chooseCityFilePathHandle(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose city config");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt"));
        File cityPathFile = fileChooser.showOpenDialog((Stage) settingsGrid.getScene().getWindow());
        cityPath = cityPathFile.getAbsolutePath();
        cityChooserLabel.setText(cityPathFile.getPath());
    }

    @FXML
    public void chooseOutputFilePathHandle(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose report output");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt"));
        File outputPathFile = fileChooser.showSaveDialog((Stage) settingsGrid.getScene().getWindow());
        outputPath = outputPathFile.getAbsolutePath();
        outputChooserLabel.setText(outputPathFile.getPath());
    }

    @FXML
    public void cancelButtonHandle(ActionEvent actionEvent) throws IOException {
        returnToMainMenu();
    }
    @FXML
    public void saveButtonHandle(ActionEvent actionEvent) throws IOException {
        FileInputStream in = new FileInputStream("src/app.properties");
        Properties props = new Properties();
        props.load(in);
        in.close();

        FileOutputStream out = new FileOutputStream("src/app.properties");
        props.setProperty("cityPath", cityPath);
        props.setProperty("carMax", maxCarsField.getText());
        props.setProperty("simulationTime", simLengthField.getText());
        props.setProperty("weather", String.valueOf(weatherSelect.getValue()));
        props.setProperty("router", String.valueOf(routerSelect.getValue()));
        props.setProperty("carefulDriverModifier", carefulMod.getText());
        props.setProperty("standardDriverModifier", standardMod.getText());
        props.setProperty("aggressiveDriverModifier", aggressiveMod.getText());
        props.setProperty("output", outputPath);
        props.store(out, null);
        out.close();
        returnToMainMenu();
    }

    private void returnToMainMenu() throws IOException {
        Stage stage = (Stage) settingsGrid.getScene().getWindow();
        Parent settings = FXMLLoader.load(getClass().getResource("mainMenu.fxml"));
        stage.setScene(new Scene(settings, 1024, 800));
        stage.centerOnScreen();
    }
}
