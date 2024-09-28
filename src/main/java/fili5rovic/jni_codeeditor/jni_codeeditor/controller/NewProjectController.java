package fili5rovic.jni_codeeditor.jni_codeeditor.controller;

import fili5rovic.jni_codeeditor.jni_codeeditor.util.FileHelper;
import fili5rovic.jni_codeeditor.jni_codeeditor.window.Window;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NewProjectController extends ControllerBase {

    @FXML
    private TextField locationTextField;
    @FXML
    private TextField nameTextField;
    @FXML
    private Button createProjectButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Window.getWindowAt(Window.WINDOW_NEW_PROJECT).setController(this);
        listeners();
    }


    private void listeners() {
        locationTextField.setOnMouseClicked(_ -> {
            Stage stage = Window.getWindowAt(Window.WINDOW_NEW_PROJECT).getStage();
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(stage);
            if(selectedDirectory == null) {
                createProjectButton.setDisable(true);
                return;
            }
            locationTextField.setText(selectedDirectory.getAbsolutePath());
            stage.show();
            updateButton();
        });
        nameTextField.setOnKeyTyped(_ -> updateButton());
    }

    private void updateButton() {
        createProjectButton.setDisable(nameTextField.getText().isEmpty() || locationTextField.getText().isEmpty());
    }

    public void createProjectBtnClicked() {
        String path = locationTextField.getText() + "/" + nameTextField.getText();
        File file = new File(path);
        if(file.exists()) {
            System.out.println("Project already exists");
            return;
        }
        if(!file.mkdir()) {
            System.out.println("Failed to create project");
            return;
        }
        System.out.println("Project created");
        createProjectTemplateFiles(file);
        ((DashboardController) Window.getWindowAt(Window.WINDOW_DASHBOARD).getController()).getProjectManager().openProject(path);
        Window.getWindowAt(Window.WINDOW_NEW_PROJECT).getStage().close();
    }

    private void createProjectTemplateFiles(File root) {
        File src = new File(root.getAbsolutePath() + "/src");
        File nat = new File(root.getAbsolutePath() + "/native");

        if (!src.mkdirs()) {
            System.out.println("Failed to create src directory");
            return;
        }
        if (!nat.mkdirs()) {
            System.out.println("Failed to create native directory");
            return;
        }

        File mainJavaFile = new File(src.getAbsolutePath() + "/Main.java");
        try {
            if (!mainJavaFile.createNewFile()) {
                System.out.println("Failed to create Main.java file");
                return;
            }
            File javaTemplate = new File("src/main/resources/fili5rovic/jni_codeeditor/jni_codeeditor/template/default-java-template.txt");
            String content = FileHelper.readFromFile(javaTemplate);
            FileHelper.writeToFile(mainJavaFile, content);
        } catch (IOException e) {
            System.out.println("An error occurred while creating Main.java file");
        }
    }
}
