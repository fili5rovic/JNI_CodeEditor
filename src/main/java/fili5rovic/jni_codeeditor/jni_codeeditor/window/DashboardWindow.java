package fili5rovic.jni_codeeditor.jni_codeeditor.window;

import fili5rovic.jni_codeeditor.jni_codeeditor.controller.DashboardController;
import javafx.stage.Stage;

public class DashboardWindow extends Window {

    static String resource = "/fili5rovic/jni_codeeditor/jni_codeeditor/dashboard.fxml";
    static String css = "/fili5rovic/jni_codeeditor/jni_codeeditor/stylesheets/styles.css";
    static String title = "Dashboard";

    @Override
    public void init(Stage stage) {
        load(stage,resource,css,title);
        ((DashboardController) Window.getWindowAt(Window.WINDOW_DASHBOARD).getController()).initShortcuts(stage.getScene());
    }
}