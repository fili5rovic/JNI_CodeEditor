package fili5rovic.jni_codeeditor.jni_codeeditor.hierarchy;

import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;

public class MyTreeItem<T> extends TreeItem<T> {
    private int clickCount = 0;


    public MyTreeItem(T value) {
        super(value);
        init();
    }

    private void init() {
        this.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            clickCount++;
            if (clickCount == 2) {
                clickCount = 0;
                System.out.println("Double clicked");
            }
        });
        this.addEventFilter(MouseEvent.MOUSE_EXITED, event -> {
            clickCount = 0;
        });
    }
}
