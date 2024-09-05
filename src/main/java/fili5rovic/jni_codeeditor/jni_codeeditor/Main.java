package fili5rovic.jni_codeeditor.jni_codeeditor;

import fili5rovic.jni_codeeditor.jni_codeeditor.util.SmartCodeArea;
import fili5rovic.jni_codeeditor.jni_codeeditor.window.DashboardWindow;
import fili5rovic.jni_codeeditor.jni_codeeditor.window.Window;
import fili5rovic.jni_codeeditor.jni_codeeditor.window.WindowHelper;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;


public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        Window.setWindowAt(Window.WINDOW_DASHBOARD, new DashboardWindow());
        Window.initAllWindows();
        WindowHelper.showOnly(Window.WINDOW_DASHBOARD);
//        cum(primaryStage);
    }


    private void cum(Stage primaryStage) {
        // Create a CodeArea
        SmartCodeArea codeArea = new SmartCodeArea();
        codeArea.setLineHighlighterOn(true);
        codeArea.setLineHighlighterFill(Paint.valueOf("#d0e1f9"));
        codeArea.setWrapText(true);

        // Put the CodeArea in a layout (VBox)
        VBox vbox = new VBox(codeArea);

        // Create a Scene and set it on the Stage
        Scene scene = new Scene(vbox, 600, 400);
        scene.getStylesheets().add(getClass().getResource("/fili5rovic/jni_codeeditor/jni_codeeditor/stylesheets/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("RichTextFX CodeArea Example");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}