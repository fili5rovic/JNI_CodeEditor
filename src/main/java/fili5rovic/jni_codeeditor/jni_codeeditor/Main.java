package fili5rovic.jni_codeeditor.jni_codeeditor;

import fili5rovic.jni_codeeditor.jni_codeeditor.window.DashboardWindow;
import fili5rovic.jni_codeeditor.jni_codeeditor.window.NewProjectWindow;
import fili5rovic.jni_codeeditor.jni_codeeditor.window.Window;
import fili5rovic.jni_codeeditor.jni_codeeditor.window.WindowHelper;
import javafx.application.Application;
import javafx.stage.Stage;


public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        Window.setWindowAt(Window.WINDOW_DASHBOARD, new DashboardWindow());
        Window.setWindowAt(Window.WINDOW_NEW_PROJECT, new NewProjectWindow());
        Window.initAllWindows();
        WindowHelper.showOnly(Window.WINDOW_DASHBOARD);
    }

    public static void main(String[] args) {
        launch();
    }
}