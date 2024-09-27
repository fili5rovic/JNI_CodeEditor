package fili5rovic.jni_codeeditor.jni_codeeditor.smart_code_area;

import fili5rovic.jni_codeeditor.jni_codeeditor.controller.DashboardController;
import fili5rovic.jni_codeeditor.jni_codeeditor.hierarchy.HierarchyManager;
import fili5rovic.jni_codeeditor.jni_codeeditor.window.Window;
import javafx.stage.DirectoryChooser;

import java.io.File;

public class ProjectManager {
    private HierarchyManager hierarchyManager;

    public ProjectManager(DashboardController dc) {
        this.hierarchyManager = new HierarchyManager(dc.getProjectHierarchy());
    }

    public void openProjectAction() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(Window.getWindowAt(Window.WINDOW_DASHBOARD).getStage());

        if (selectedDirectory != null)
            hierarchyManager.setPath(selectedDirectory.getAbsolutePath());

    }

    public String getRootPath() {
        return hierarchyManager.getRootPath();
    }
}
