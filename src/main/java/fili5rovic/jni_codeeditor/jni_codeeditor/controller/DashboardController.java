package fili5rovic.jni_codeeditor.jni_codeeditor.controller;

import fili5rovic.jni_codeeditor.jni_codeeditor.util.Language;
import fili5rovic.jni_codeeditor.jni_codeeditor.util.SmartCodeArea;
import fili5rovic.jni_codeeditor.jni_codeeditor.window.Window;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;


import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController extends ControllerBase {

    @FXML
    private TreeView<String> projectHierarchy;
    @FXML
    private BorderPane codeAreaPane;
    @FXML
    private SplitPane horizontalSplitPane;
    @FXML
    private Button collapseProjectPaneBtn;

    private SmartCodeArea codeArea;

    private ProjectTreeManager projectTreeManager;
    private DividerManager dividerManager;

    class ProjectTreeManager {
        ProjectTreeManager() {
            init();
        }

        void init() {
            TreeItem<String> root = new TreeItem<>("Projects");
            TreeItem<String> item1 = new TreeItem<>("Project 1");
            root.getChildren().add(item1);
            DashboardController.this.projectHierarchy.setRoot(root);
        }
    }

    class DividerManager {
        private double nextDividerPosition = 0.0;
        private double oldDividerPosition = 0.2;
        private double absoluteDividerPosition = 150;
        DividerManager() {
            initListeners();
        }

        private void initListeners() {
            horizontalSplitPane.widthProperty().addListener((observable, oldValue, newValue) -> {
                horizontalSplitPane.setDividerPositions(absoluteDividerPosition / newValue.doubleValue());
            });
            horizontalSplitPane.getDividers().getFirst().positionProperty().addListener((observable, oldValue, newValue) -> {
                absoluteDividerPosition = newValue.doubleValue() * horizontalSplitPane.getWidth();
            });
            collapseProjectPaneBtn.setOnAction(event -> collapseProjectPaneBtnClicked());
        }

        private void collapseProjectPaneBtnClicked() {
            var divider = horizontalSplitPane.getDividers().getFirst();
            oldDividerPosition = divider.getPosition();

            divider.setPosition(nextDividerPosition);

            nextDividerPosition = oldDividerPosition;
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Window.getWindowAt(Window.WINDOW_DASHBOARD).setController(this);
        init();
    }

    private void init() {
        codeArea = new SmartCodeArea(Language.JAVA);
        codeAreaPane.setCenter(codeArea);

        projectTreeManager = new ProjectTreeManager();
        dividerManager = new DividerManager();
    }



}
