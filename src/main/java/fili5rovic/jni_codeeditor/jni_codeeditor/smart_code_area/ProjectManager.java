package fili5rovic.jni_codeeditor.jni_codeeditor.smart_code_area;

import fili5rovic.jni_codeeditor.jni_codeeditor.controller.DashboardController;
import fili5rovic.jni_codeeditor.jni_codeeditor.hierarchy.HierarchyManager;
import fili5rovic.jni_codeeditor.jni_codeeditor.window.Window;
import javafx.stage.DirectoryChooser;

import java.io.File;

public class ProjectManager {
    private final HierarchyManager hierarchyManager;

    public ProjectManager(DashboardController dc) {
        this.hierarchyManager = new HierarchyManager(dc);
    }

    public void openProjectAction() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(Window.getWindowAt(Window.WINDOW_DASHBOARD).getStage());

        openProject(selectedDirectory.getAbsolutePath());

    }

    public void openProject(String path) {
        hierarchyManager.setPath(path);
    }

    public void refreshProject() {
        hierarchyManager.refresh();
    }

}
