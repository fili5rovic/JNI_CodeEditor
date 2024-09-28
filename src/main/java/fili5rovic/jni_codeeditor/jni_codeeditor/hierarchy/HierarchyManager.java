package fili5rovic.jni_codeeditor.jni_codeeditor.hierarchy;

import fili5rovic.jni_codeeditor.jni_codeeditor.controller.DashboardController;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.io.File;


public class HierarchyManager {

    private static final int MAX_FILES = 100;

    private final TreeView<String> hierarchy;
    private String rootProjectPath;

    private int clickNum = 0;
    private TreeItem<String> lastSelectedItem;
    private final DashboardController dc;

    private final ContextMenu contextMenu = new ContextMenu();


    public HierarchyManager(DashboardController dc) {
        this.dc = dc;
        this.hierarchy = dc.getProjectHierarchy();
        addClickEventFilters();
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

    private void addClickEventFilters() {
        hierarchy.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if(e.getButton() != MouseButton.PRIMARY)
                return;

            contextMenu.hide();
            TreeItem<String> selectedItem = hierarchy.getSelectionModel().getSelectedItem();
            clickNum++;
            if (selectedItem != null && selectedItem.equals(lastSelectedItem) && clickNum == 2) {
                onDoubleClick(selectedItem);
                clickNum = 0;
            } else
                clickNum = 1;

            lastSelectedItem = selectedItem;
        });
        hierarchy.setOnContextMenuRequested(event -> {
            TreeItem<String> selectedItem = hierarchy.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                contextMenu.show(hierarchy, event.getScreenX(), event.getScreenY());
                makeMenuItemsForSelectedItem(selectedItem);
            }
        });
    }

    private void makeMenuItemsForSelectedItem(TreeItem<String> selectedItem) {
        contextMenu.getItems().clear();
        MenuItem openItem = new MenuItem("Open");
        openItem.setOnAction(_ -> onDoubleClick(selectedItem));
        contextMenu.getItems().add(openItem);
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

        if(file.isDirectory())
            return;
        dc.addNewTabPane(file);
    }
}
