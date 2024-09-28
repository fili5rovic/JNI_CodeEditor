package fili5rovic.jni_codeeditor.jni_codeeditor.controller;

import fili5rovic.jni_codeeditor.jni_codeeditor.smart_code_area.Language;
import fili5rovic.jni_codeeditor.jni_codeeditor.smart_code_area.ProjectManager;
import fili5rovic.jni_codeeditor.jni_codeeditor.smart_code_area.SmartCodeArea;
import fili5rovic.jni_codeeditor.jni_codeeditor.smart_code_area.TabSmartCodeArea;
import fili5rovic.jni_codeeditor.jni_codeeditor.window.Window;
import javafx.fxml.FXML;
import javafx.scene.control.*;


import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController extends ControllerBase {

    @FXML
    private TreeView<String> projectHierarchy;
    @FXML
    private TabPane mainTabPane;
    @FXML
    private SplitPane horizontalSplitPane;
    @FXML
    private Button collapseProjectPaneBtn;

    private DividerManager dividerManager;
    private ProjectManager projectManager;

    static class DividerManager {
        private double nextDividerPosition = 0.0;
        private double oldDividerPosition = 0.2;
        private double absoluteDividerPosition = 150;

        private SplitPane splitPane;
        private Button collapseBtn;

        DividerManager(SplitPane splitPane, Button collapseBtn) {
            this.splitPane = splitPane;
            this.collapseBtn = collapseBtn;
            initListeners();
        }

        private void initListeners() {
            splitPane.widthProperty().addListener((observable, oldValue, newValue) -> {
                splitPane.setDividerPositions(absoluteDividerPosition / newValue.doubleValue());
            });
            splitPane.getDividers().getFirst().positionProperty().addListener((observable, oldValue, newValue) -> {
                absoluteDividerPosition = newValue.doubleValue() * splitPane.getWidth();
            });
            collapseBtn.setOnAction(event -> collapseProjectPaneBtnClicked());
        }

        private void collapseProjectPaneBtnClicked() {
            var divider = splitPane.getDividers().getFirst();
            oldDividerPosition = divider.getPosition();

            divider.setPosition(nextDividerPosition);

            nextDividerPosition = oldDividerPosition;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Window.getWindowAt(Window.WINDOW_DASHBOARD).setController(this);
        addNewTabPane("default");
        initManagers();
    }

    private void initManagers() {
        dividerManager = new DividerManager(horizontalSplitPane, collapseProjectPaneBtn);
        projectManager = new ProjectManager(this);
    }

    private void addNewTabPane(String name) {
        mainTabPane.getTabs().add(new TabSmartCodeArea(new SmartCodeArea(Language.JAVA), name));
    }

    @FXML
    private void openProjectAction() {
        projectManager.openProjectAction();
    }

    public TreeView<String> getProjectHierarchy() {
        return projectHierarchy;
    }

}
