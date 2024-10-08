package fili5rovic.jni_codeeditor.jni_codeeditor.smart_code_area;

import fili5rovic.jni_codeeditor.jni_codeeditor.controller.DashboardController;
import fili5rovic.jni_codeeditor.jni_codeeditor.hierarchy.HierarchyManager;
import fili5rovic.jni_codeeditor.jni_codeeditor.window.Window;
import javafx.stage.DirectoryChooser;

import java.io.File;

public class ProjectManager {
    private final HierarchyManager hierarchyManager;

    private static String projectPath;
    private static String sourcesRootPath;

    public ProjectManager(DashboardController dc) {
        this.hierarchyManager = new HierarchyManager(dc);
    }

    public void openProjectAction() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(Window.getWindowAt(Window.WINDOW_DASHBOARD).getStage());
        if(selectedDirectory == null)
            return;

        openProject(selectedDirectory.getAbsolutePath());

    }

    public void openProject(String path) {
        hierarchyManager.setPath(path);
    }
    public void refreshProject() {
        hierarchyManager.refresh();
    }

    public static String getProjectPath() {
        return projectPath;
    }

    public static void setProjectPath(String projectPath) {
        ProjectManager.projectPath = projectPath;
        if(sourcesRootPath == null)
            sourcesRootPath = projectPath;
    }

    public static void setSourcesRootPath(String sourcesRootPath) {
        ProjectManager.sourcesRootPath = sourcesRootPath;
    }

    public static String getSourcesRootPath() {
        return sourcesRootPath;
    }

}
