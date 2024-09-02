package fili5rovic.jni_codeeditor.jni_codeeditor.window;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DashboardWindow extends Window {
    @Override
    public void init(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fili5rovic/jni_codeeditor/jni_codeeditor/dashboard.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            scene.getStylesheets().add(getClass().getResource("/fili5rovic/jni_codeeditor/jni_codeeditor/stylesheets/styles.css").toExternalForm());
            stage.setResizable(false);
            stage.setTitle("Dashboard");
            stage.setScene(scene);

            this.stage = stage;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}