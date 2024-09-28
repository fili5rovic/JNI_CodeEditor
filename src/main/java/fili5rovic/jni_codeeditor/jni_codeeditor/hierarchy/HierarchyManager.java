package fili5rovic.jni_codeeditor.jni_codeeditor.hierarchy;

import fili5rovic.jni_codeeditor.jni_codeeditor.controller.DashboardController;
import fili5rovic.jni_codeeditor.jni_codeeditor.util.CommandLineUtil;
import fili5rovic.jni_codeeditor.jni_codeeditor.util.FileHelper;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


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

    public void refresh() {
        getFileSystemTree(rootProjectPath);
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

        if(selectedItem.getValue().endsWith(".java")) {
            File file = new File(getPathForTreeItem(selectedItem));
            String code = FileHelper.readFromFile(file);
            if(code.matches("(?s).*\\bnative\\b.*") && selectedItem.getParent() != null) {
                System.out.println("Native methods found");
                MenuItem generateHeaderItem = menuItemJNI(selectedItem, file);
                contextMenu.getItems().add(generateHeaderItem);
            } else {
                System.out.println("No native methods found");
            }
        }
    }

    private MenuItem menuItemJNI(TreeItem<String> selectedItem, File file) {
        MenuItem jniMenuItem = new MenuItem("Create native methods");
        jniMenuItem.setOnAction(_ -> {
            try {
//                ArrayList<String> cppFiles = new ArrayList<>();
//                for(TreeItem<String> child : selectedItem.getParent().getChildren()) {
//                    if(child.getValue().endsWith(".cpp"))
//                        cppFiles.add(getPathForTreeItem(child));
//                }
                CommandLineUtil.createCppFileFromJavaFile(file.getName(), file.getParentFile());
            } catch (IOException | InterruptedException e) {
                System.out.println("ERROR");
                throw new RuntimeException(e);
            }
        });
        return jniMenuItem;
    }

    private void onDoubleClick(TreeItem<String> selectedItem) {
        if(selectedItem.getParent() == null)
            return;

        File file = new File(getPathForTreeItem(selectedItem));

        if(file.isDirectory())
            return;
        dc.addNewTabPane(file);
    }

    private String getPathForTreeItem(TreeItem<String> item) {
        StringBuilder path = new StringBuilder(rootProjectPath + "\\");
        TreeItem<String> currentItem = item;
        ArrayList<String> pathList = new ArrayList<>();
        while (currentItem.getParent() != null) {
            if(currentItem != item)
                pathList.add(currentItem.getValue());
            currentItem = currentItem.getParent();
        }
        for (int i = pathList.size() - 1; i >= 0; i--)
            path.append(pathList.get(i)).append("\\");

        path.append(item.getValue());
        return path.toString();
    }
}
