package fili5rovic.jni_codeeditor.jni_codeeditor.controller;

import fili5rovic.jni_codeeditor.jni_codeeditor.util.Language;
import fili5rovic.jni_codeeditor.jni_codeeditor.util.SmartCodeArea;
import fili5rovic.jni_codeeditor.jni_codeeditor.window.Window;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.fxmisc.richtext.CodeArea;


import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController extends ControllerBase {

    @FXML
    private TreeView<String> projectHierarchy;
    @FXML
    private BorderPane codeAreaPane;

    private SmartCodeArea codeArea;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Window.getWindowAt(Window.WINDOW_DASHBOARD).setController(this);
        init();
        initProjectTree();
    }

    private void init() {
        codeArea = new SmartCodeArea(Language.JAVA);
        codeAreaPane.setCenter(codeArea);
    }

    private void initProjectTree() {
        TreeItem<String> root = new TreeItem<>("Projects");
        TreeItem<String> item1 = new TreeItem<>("Project 1");
        root.getChildren().add(item1);
        projectHierarchy.setRoot(root);
    }


}
