package fili5rovic.jni_codeeditor.jni_codeeditor.hierarchy;

import fili5rovic.jni_codeeditor.jni_codeeditor.controller.DashboardController;
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
    private final DashboardController dc;


    public HierarchyManager(DashboardController dc) {
        this.dc = dc;
        this.hierarchy = dc.getProjectHierarchy();
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
        hierarchy.addEventFilter(MouseEvent.MOUSE_CLICKED, _ -> {
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
        if(selectedItem.getParent() == null)
            return;

        StringBuilder path = new StringBuilder(rootProjectPath + "\\");
        TreeItem<String> item = selectedItem;
        while (item.getParent() != null) {
            if(item != selectedItem)
                path.append(item.getValue()).append("\\");
            item = item.getParent();
        }
        path.append(selectedItem.getValue());
        File file = new File(path.toString());

        System.out.println("Double clicked on: " + path);
        if(file.isDirectory())
            return;
        dc.addNewTabPane(file);
    }

    public String getRootPath() {
        return rootProjectPath;
    }
}
