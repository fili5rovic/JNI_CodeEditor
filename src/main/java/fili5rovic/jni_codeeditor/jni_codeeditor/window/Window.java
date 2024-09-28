package fili5rovic.jni_codeeditor.jni_codeeditor.window;


import fili5rovic.jni_codeeditor.jni_codeeditor.controller.ControllerBase;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class Window {
    protected Stage stage;
    protected ControllerBase controller;
    public static final int WINDOWS = 2;
    public static final int WINDOW_DASHBOARD = 0;
    public static final int WINDOW_NEW_PROJECT = 1;
    private final static Window[] windows = new Window[WINDOWS];

    public static void setWindowAt(int position, Window w) {
        windows[position] = w;
    }

    public static Window getWindowAt( int position) {
        return windows[position];
    }

    public static void initAllWindows() {
        for (int i = 0; i < WINDOWS; i++) {
            windows[i].init(new Stage());
        }
    }

    public void load(Stage stage,String resource, String cssPath, String title) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(resource));
            Scene scene = new Scene(fxmlLoader.load());
            scene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
            stage.setTitle(title);
            stage.setScene(scene);
            this.stage = stage;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract void init(Stage stage);


    //<editor-fold desc="Geteri i Seteri">
    public Stage getStage() {
        return stage;
    }

    public ControllerBase getController() {
        return controller;
    }

    public void setController(ControllerBase controller) {
        this.controller = controller;
    }
    //</editor-fold>

}
