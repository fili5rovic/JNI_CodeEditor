package fili5rovic.jni_codeeditor.jni_codeeditor.window;

import javafx.stage.Stage;

public class DashboardWindow extends Window {

    static String resource = "/fili5rovic/jni_codeeditor/jni_codeeditor/dashboard.fxml";
    static String title = "Dashboard";

    @Override
    public void init(Stage stage) {
        load(stage,resource,title);
    }
}