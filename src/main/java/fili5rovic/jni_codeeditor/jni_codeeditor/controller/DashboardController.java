package fili5rovic.jni_codeeditor.jni_codeeditor.controller;

import fili5rovic.jni_codeeditor.jni_codeeditor.window.Window;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;


import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController extends ControllerBase {

    @FXML
    private TreeView<String> projectHierarchy;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Window.getWindowAt(Window.WINDOW_DASHBOARD).setController(this);
        TreeItem<String> root = new TreeItem<>("Projects");
        TreeItem<String> item1 = new TreeItem<>("Project 1");
        root.getChildren().add(item1);
        projectHierarchy.setRoot(root);
    }


}
