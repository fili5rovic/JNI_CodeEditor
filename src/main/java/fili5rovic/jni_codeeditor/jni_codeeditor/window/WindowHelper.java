package fili5rovic.jni_codeeditor.jni_codeeditor.window;

public class WindowHelper {
    public static void showWindow(int position) {
        if (position < 0 || position > Window.WINDOWS)
            return;
        Window.getWindowAt(position).getStage().show();
        Window.getWindowAt(position).getController().onShow();
    }

    public static void showOnly(int position) {
        if (position < 0 || position > Window.WINDOWS)
            return;
        for (int i = 0; i < Window.WINDOWS; i++) {
            if (i != position)
                Window.getWindowAt(i).getStage().hide();
            else {
                Window.getWindowAt(i).getStage().show();
                Window.getWindowAt(i).getController().onShow();
            }
        }
    }

    public static void hideWindow(int position) {
        if (position < 0 || position > Window.WINDOWS)
            return;
        Window.getWindowAt(position).getStage().hide();
    }


}
