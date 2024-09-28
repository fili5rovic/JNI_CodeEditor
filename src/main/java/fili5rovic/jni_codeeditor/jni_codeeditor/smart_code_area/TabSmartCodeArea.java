package fili5rovic.jni_codeeditor.jni_codeeditor.smart_code_area;

import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import org.fxmisc.flowless.VirtualizedScrollPane;

public class TabSmartCodeArea extends Tab {
    private final SmartCodeArea smartCodeArea;

    public TabSmartCodeArea(SmartCodeArea smartCodeArea, String tabName) {
        this.smartCodeArea = smartCodeArea;
        setText(tabName);
        init();
    }

    private void init() {
        BorderPane borderPane = new BorderPane();
        VirtualizedScrollPane<SmartCodeArea> virtualizedScrollPane = new VirtualizedScrollPane<>(smartCodeArea);
        borderPane.setCenter(virtualizedScrollPane);
        this.setContent(borderPane);
    }

}
