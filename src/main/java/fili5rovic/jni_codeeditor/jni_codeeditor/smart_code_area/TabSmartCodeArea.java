package fili5rovic.jni_codeeditor.jni_codeeditor.smart_code_area;

import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;

public class TabSmartCodeArea extends Tab {
    private SmartCodeArea smartCodeArea;

    public TabSmartCodeArea(SmartCodeArea smartCodeArea, String tabName) {
        this.smartCodeArea = smartCodeArea;
        setText(tabName);
        init();
    }

    private void init() {
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(smartCodeArea);
        this.setContent(borderPane);
    }

}
