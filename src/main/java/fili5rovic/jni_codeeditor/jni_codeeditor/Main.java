package fili5rovic.jni_codeeditor.jni_codeeditor;

import fili5rovic.jni_codeeditor.jni_codeeditor.window.*;
import javafx.application.Application;
import javafx.stage.Stage;


public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        Window.setWindowAt(Window.WINDOW_DASHBOARD, new DashboardWindow());
        Window.setWindowAt(Window.WINDOW_NEW_PROJECT, new NewProjectWindow());
        Window.setWindowAt(Window.WINDOW_RUN_CONFIG_EDITOR, new RunConfigEditorWindow());

        Window.initAllWindows();
        WindowHelper.showOnly(Window.WINDOW_DASHBOARD);
    }

    public static void main(String[] args) {
        launch();
    }
}