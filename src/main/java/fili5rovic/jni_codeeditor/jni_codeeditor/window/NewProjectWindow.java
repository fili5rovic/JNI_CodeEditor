package fili5rovic.jni_codeeditor.jni_codeeditor.window;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class NewProjectWindow extends Window {
    static String resource = "/fili5rovic/jni_codeeditor/jni_codeeditor/newProject.fxml";
    static String css = "/fili5rovic/jni_codeeditor/jni_codeeditor/stylesheets/newProject.css";
    static String title = "Choose project location";

    @Override
    public void init(Stage stage) {
        stage.initStyle(StageStyle.UNDECORATED);
        load(stage,resource,css,title);
        shortcuts(stage);
    }
    private void shortcuts(Stage stage) {
        stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.ESCAPE), stage::close);
        stage.focusedProperty().addListener((_, _, newValue) -> {
            if (!newValue) {
                stage.close();
            }
        });
    }
}
