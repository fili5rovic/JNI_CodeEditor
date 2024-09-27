package fili5rovic.jni_codeeditor.jni_codeeditor.hierarchy;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.io.File;


public class HierarchyManager {
    private TreeView<String> hierarchy;
    private String rootProjectPath;
    public HierarchyManager(TreeView<String> hierarchy) {
        this.hierarchy = hierarchy;
    }

    public void setPath(String path) {
        getFileSystemTree(path);
    }

    private void getFileSystemTree(String directoryPath) {
        File rootDir = new File(directoryPath);
        TreeItem<String> rootItem = createNode(rootDir);
        hierarchy.setRoot(rootItem);
        this.rootProjectPath = directoryPath;
    }


    private static TreeItem<String> createNode(File file) {
        TreeItem<String> treeItem = new TreeItem<>(file.getName());
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                treeItem.getChildren().add(createNode(child));
            }
        }
        return treeItem;
    }

    public String getRootPath() {
        return rootProjectPath;
    }
}
