package fili5rovic.jni_codeeditor.jni_codeeditor.controller;

import fili5rovic.jni_codeeditor.jni_codeeditor.smart_code_area.ProjectManager;
import fili5rovic.jni_codeeditor.jni_codeeditor.util.RunConfigItem;
import fili5rovic.jni_codeeditor.jni_codeeditor.window.Window;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class RunConfigEditorController extends ControllerBase {

    @FXML
    private Button createBtn;
    @FXML
    private TextField libraryPathTextField;
    @FXML
    private TextField mainClassTextField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Window.getWindowAt(Window.WINDOW_RUN_CONFIG_EDITOR).setController(this);
        listeners();
    }

    private void listeners() {
        libraryPathTextField.setOnKeyTyped(_ -> updateButton());
        mainClassTextField.setOnKeyTyped(_ -> updateButton());
        updateButton();

        libraryPathTextField.setOnMouseClicked(_ -> {
            Stage stage = Window.getWindowAt(Window.WINDOW_RUN_CONFIG_EDITOR).getStage();
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setInitialDirectory(new File(ProjectManager.getProjectPath()));
            File selectedDirectory = directoryChooser.showDialog(stage);
            if(selectedDirectory == null) {
                createBtn.setDisable(true);
                return;
            }
            libraryPathTextField.setText(selectedDirectory.getAbsolutePath());
            updateButton();
        });
        mainClassTextField.setOnMouseClicked(_ -> {
            Stage stage = Window.getWindowAt(Window.WINDOW_RUN_CONFIG_EDITOR).getStage();
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Java files", "*.java"));            fileChooser.setInitialDirectory(new File(ProjectManager.getProjectPath()));
            File selectedFile = fileChooser.showOpenDialog(stage);
            if(selectedFile == null) {
                createBtn.setDisable(true);
                return;
            }
            mainClassTextField.setText(selectedFile.getAbsolutePath());
            updateButton();
        });

//        Window.getWindowAt(Window.WINDOW_RUN_CONFIG_EDITOR).getStage().setOnShown(_->{
//            mainClassTextField.clear();
//            libraryPathTextField.clear();
//            createBtn.setDisable(true);
//        });

    }

    private void updateButton() {
        createBtn.setDisable(libraryPathTextField.getText().isEmpty() || mainClassTextField.getText().isEmpty());
    }

    public void createConfigAction() {
        DashboardController dashboardController = (DashboardController) Window.getWindowAt(Window.WINDOW_DASHBOARD).getController();
        ChoiceBox<RunConfigItem> runConfig = dashboardController.getRunConfig();
        String name = mainClassTextField.getText().split("\\.")[0];
        name = name.substring(name.lastIndexOf("\\") + 1);
        RunConfigItem newItem = new RunConfigItem(name,libraryPathTextField.getText(), mainClassTextField.getText());
        if(runConfig.getItems().contains(newItem)) {
            System.out.println("Config already exists");
            return;
        }
        runConfig.getItems().addFirst(newItem);
        runConfig.getSelectionModel().selectFirst();
    }
}
