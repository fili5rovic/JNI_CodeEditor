package fili5rovic.jni_codeeditor.jni_codeeditor.hierarchy;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.File;


public class HierarchyManager {

    private static final int MAX_FILES = 100;

    private final TreeView<String> hierarchy;
    private String rootProjectPath;

    private int clickNum = 0;
    private TreeItem<String> lastSelectedItem;


    public HierarchyManager(TreeView<String> hierarchy) {
        this.hierarchy = hierarchy;
        addClickEventFilter();
    }

    public void setPath(String path) {
        getFileSystemTree(path);
    }

    private void getFileSystemTree(String directoryPath) {
        File rootDir = new File(directoryPath);
        if(rootDir.listFiles().length > MAX_FILES) {
            System.out.println("Too many files in the directory");
            return;
        }
        TreeItem<String> rootItem = createNode(rootDir);
        hierarchy.setRoot(rootItem);
        this.rootProjectPath = directoryPath;
    }


    private static TreeItem<String> createNode(File file) {
        TreeItem<String> treeItem = new TreeItem<>(file.getName());
        setupIcon(file, treeItem);

        if (file.isDirectory() && file.listFiles() != null) {
            for (File child : file.listFiles()) {
                treeItem.getChildren().add(createNode(child));
            }
        }
        return treeItem;
    }

    private static void setupIcon(File file, TreeItem<String> treeItem) {
        treeItem.setGraphic(new ImageView(IconManager.getIconPathByFileExtension(file)));
    }

    private void addClickEventFilter() {
        hierarchy.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            TreeItem<String> selectedItem = hierarchy.getSelectionModel().getSelectedItem();
            clickNum++;
            if (selectedItem != null && selectedItem.equals(lastSelectedItem) && clickNum == 2) {
                onDoubleClick(selectedItem);
                clickNum = 0;
            } else {
                clickNum = 1;
            }
            lastSelectedItem = selectedItem;
        });
    }

    private void onDoubleClick(TreeItem<String> selectedItem) {
        String path = rootProjectPath + "\\";
        TreeItem<String> item = selectedItem;
        while (item.getParent() != null) {
            path = item.getValue() + "\\" + path;
            item = item.getParent();
        }
        File file = new File(rootProjectPath + "\\" + selectedItem.getValue());
        System.out.println("Double clicked on: " + file.getAbsolutePath());
    }

    public String getRootPath() {
        return rootProjectPath;
    }
}
