package fili5rovic.jni_codeeditor.jni_codeeditor.controller;

import fili5rovic.jni_codeeditor.jni_codeeditor.hierarchy.IconManager;
import fili5rovic.jni_codeeditor.jni_codeeditor.smart_code_area.Language;
import fili5rovic.jni_codeeditor.jni_codeeditor.smart_code_area.ProjectManager;
import fili5rovic.jni_codeeditor.jni_codeeditor.smart_code_area.SmartCodeArea;
import fili5rovic.jni_codeeditor.jni_codeeditor.smart_code_area.TabSmartCodeArea;
import fili5rovic.jni_codeeditor.jni_codeeditor.util.FileHelper;
import fili5rovic.jni_codeeditor.jni_codeeditor.util.ShortcutKeys;
import fili5rovic.jni_codeeditor.jni_codeeditor.window.Window;
import fili5rovic.jni_codeeditor.jni_codeeditor.window.WindowHelper;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController extends ControllerBase {

    @FXML
    private TreeView<String> projectHierarchy;
    @FXML
    private TabPane mainTabPane;
    @FXML
    private Pane paneBehindTabPane;
    @FXML
    private SplitPane horizontalSplitPane;
    @FXML
    private Button collapseProjectPaneBtn;

    private ProjectManager projectManager;


    static class DividerManager {
        private double nextDividerPosition = 0.0;
        private double absoluteDividerPosition = 150;

        private final SplitPane splitPane;
        private final Button collapseBtn;

        DividerManager(SplitPane splitPane, Button collapseBtn) {
            this.splitPane = splitPane;
            this.collapseBtn = collapseBtn;
            initListeners();
        }

        private void initListeners() {
            splitPane.widthProperty().addListener((_, _, newValue) -> {
                splitPane.setDividerPositions(absoluteDividerPosition / newValue.doubleValue());
            });
            splitPane.getDividers().getFirst().positionProperty().addListener((_, _, newValue) -> {
                absoluteDividerPosition = newValue.doubleValue() * splitPane.getWidth();
            });
            collapseBtn.setOnAction(_ -> collapseProjectPaneBtnClicked());
        }

        private void collapseProjectPaneBtnClicked() {
            var divider = splitPane.getDividers().getFirst();
            double oldDividerPosition = divider.getPosition();

            divider.setPosition(nextDividerPosition);

            nextDividerPosition = oldDividerPosition;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Window.getWindowAt(Window.WINDOW_DASHBOARD).setController(this);
        initManagers();
        initIcons();
        projectManager.openProject("D:\\PROJECTS\\JavaCustomProjects\\JNI_Example_Project");
    }

    private void initManagers() {
        DividerManager dividerManager = new DividerManager(horizontalSplitPane, collapseProjectPaneBtn);
        projectManager = new ProjectManager(this);
    }

    public void addNewTabPane(File file) {
        for (Tab tab : mainTabPane.getTabs())
            if (tab.getText().equals(file.getName())) {
                mainTabPane.getSelectionModel().select(tab);
                return;
            }

        Language language = Language.JAVA;
        if (file.getName().endsWith(".cpp") || file.getName().endsWith(".h")) {
            language = Language.CPP;
        } else if (file.getName().endsWith(".css")) {
            language = Language.CSS;
        }
        SmartCodeArea smartCodeArea = new SmartCodeArea(language, file);
        smartCodeArea.replaceText(FileHelper.readFromFile(file));
        TabSmartCodeArea tabSmartCodeArea = new TabSmartCodeArea(smartCodeArea, file.getName(), this);
        tabSmartCodeArea.setOnClosed(_ -> {
            if (mainTabPane.getTabs().isEmpty())
                paneBehindTabPane.setVisible(true);
        });
        mainTabPane.getTabs().add(tabSmartCodeArea);
        mainTabPane.getSelectionModel().select(tabSmartCodeArea);
        if(paneBehindTabPane.isVisible())
            paneBehindTabPane.setVisible(false);
    }


    private void initIcons() {
        collapseProjectPaneBtn.setGraphic(IconManager.getCollapseIcon());
    }

    public void initShortcuts(Scene scene) {
        scene.getAccelerators().put(ShortcutKeys.OPEN_PROJECT, this::openProjectAction);
        scene.getAccelerators().put(ShortcutKeys.NEW_PROJECT, this::newProjectAction);
    }

    @FXML
    public void openProjectAction() {
        projectManager.openProjectAction();
    }

    @FXML
    private void newProjectAction() {
        WindowHelper.showWindow(Window.WINDOW_NEW_PROJECT);
    }

    public TreeView<String> getProjectHierarchy() {
        return projectHierarchy;
    }

    public ProjectManager getProjectManager() {
        return projectManager;
    }



}
