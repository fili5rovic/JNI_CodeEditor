package fili5rovic.jni_codeeditor.jni_codeeditor.hierarchy;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.io.File;


public class HierarchyManager {

    // Method to return TreeView of file system in the given directory
    public TreeView<String> getFileSystemTree(String directoryPath) {
        File rootDir = new File(directoryPath);
        TreeItem<String> rootItem = createNode(rootDir);
        return new TreeView<>(rootItem);
    }

    // Helper method to create TreeItem recursively for directories and files
    private TreeItem<String> createNode(File file) {
        TreeItem<String> treeItem = new TreeItem<>(file.getName());
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                treeItem.getChildren().add(createNode(child));
            }
        }
        return treeItem;
    }
}
