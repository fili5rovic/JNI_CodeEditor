package fili5rovic.jni_codeeditor.jni_codeeditor.window;


import fili5rovic.jni_codeeditor.jni_codeeditor.controller.ControllerBase;
import javafx.stage.Stage;

public abstract class Window {
    protected Stage stage;
    protected ControllerBase controller;
    public static final int WINDOWS = 1;
    public static final int WINDOW_DASHBOARD = 0;
    private static Window[] windows = new Window[WINDOWS];

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

    public abstract void init(Stage stage);

    //<editor-fold desc="Geteri i Seteri">
    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public ControllerBase getController() {
        return controller;
    }

    public void setController(ControllerBase controller) {
        this.controller = controller;
    }
    //</editor-fold>

}
