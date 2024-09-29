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
        if(rootDir.listFiles() == null) {
            System.out.println("Invalid directory");
            return;
        }
        if (rootDir.listFiles().length > MAX_FILES) {
            System.out.println("Too many files in the directory");
            return;
        }
        TreeItem<String> rootItem = createNode(rootDir);
        rootItem.setExpanded(true);
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
            if (e.getButton() != MouseButton.PRIMARY) {
                if (e.getButton() == MouseButton.SECONDARY)
                    contextMenuRequest(e.getScreenX(), e.getScreenY());
                return;
            }

            if (contextMenu.isShowing())
                contextMenu.hide();
            contextMenu.getItems().clear();
            TreeItem<String> selectedItem = hierarchy.getSelectionModel().getSelectedItem();
            clickNum++;
            if (selectedItem != null && selectedItem.equals(lastSelectedItem) && clickNum == 2) {
                onDoubleClick(selectedItem);
                clickNum = 0;
            } else
                clickNum = 1;

            lastSelectedItem = selectedItem;
        });
        hierarchy.setOnContextMenuRequested(e -> contextMenuRequest(e.getScreenX(), e.getScreenY()));
    }

    private void contextMenuRequest(double x, double y) {
        TreeItem<String> selectedItem = hierarchy.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            contextMenu.show(hierarchy, x, y);
            makeMenuItemsForSelectedItem(selectedItem);
        }
    }

    private void makeMenuItemsForSelectedItem(TreeItem<String> selectedItem) {
        contextMenu.getItems().clear();
        File selectedFile = new File(getPathForTreeItem(selectedItem));

        MenuItem openInExplorerMenuItem = new MenuItem("Open in explorer");
        openInExplorerMenuItem.setOnAction(_ -> {
            try {
                Runtime.getRuntime().exec("explorer.exe /select," + selectedFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        contextMenu.getItems().add(openInExplorerMenuItem);

        if (selectedFile.isFile() && !selectedFile.getName().endsWith(".dll")) {
            MenuItem openItem = new MenuItem("Open");
            openItem.setOnAction(_ -> onDoubleClick(selectedItem));
            contextMenu.getItems().add(openItem);
            String code = FileHelper.readFromFile(selectedFile);
            if (selectedItem.getValue().endsWith(".java")) {
                if (code.matches("(?s).*\\bnative\\b.*") && selectedItem.getParent() != null) {
                    MenuItem generateHeaderItem = menuItemCreateCppFile(selectedFile);
                    contextMenu.getItems().add(generateHeaderItem);
                }
            } else if (selectedItem.getValue().endsWith(".cpp")) {
                if(code.matches("(?s).*\\bJNIEXPORT\\b.*")) {
                    MenuItem generateHeaderItem = menuItemCreateDLL(selectedFile);
                    contextMenu.getItems().add(generateHeaderItem);
                }
            }
        }
    }

    /**
     * Creates java header as well as cpp file
     * @param file file to create header for
     * @return MenuItem
     */
    private MenuItem menuItemCreateCppFile(File file) {
        MenuItem jniMenuItem = new MenuItem("Create native methods");
        jniMenuItem.setOnAction(_ -> {
            try {
                CommandLineUtil.createCppFileFromJavaFile(file.getName(), file.getParentFile());
                refresh();
            } catch (IOException | InterruptedException e) {
                System.out.println("ERROR");
                throw new RuntimeException(e);
            }
        });
        return jniMenuItem;
    }

    private MenuItem menuItemCreateDLL(File file) {
        MenuItem dllMenuItem = new MenuItem("Create DLL from file");
        dllMenuItem.setOnAction(_ -> {
            try {
                ArrayList<String> fileStr = new ArrayList<>();
                fileStr.add(file.getName().replace(".cpp", ""));
                CommandLineUtil.createDLL(fileStr, file.getParentFile());
                refresh();
            } catch (IOException | InterruptedException e) {
                System.out.println("ERROR");
                throw new RuntimeException(e);
            }
        });
        return dllMenuItem;
    }

    private void onDoubleClick(TreeItem<String> selectedItem) {
        if (selectedItem.getParent() == null)
            return;

        File file = new File(getPathForTreeItem(selectedItem));

        if(file.getName().endsWith(".dll") || file.getName().endsWith(".so")) {
            System.out.println("Cannot open this type of file");
            return;
        }
        if (file.isDirectory())
            return;
        dc.addNewTabPane(file);
    }

    private String getPathForTreeItem(TreeItem<String> item) {
        StringBuilder path = new StringBuilder(rootProjectPath + "\\");
        if(item.getParent() == null)
            return path.toString();
        TreeItem<String> currentItem = item;
        ArrayList<String> pathList = new ArrayList<>();
        while (currentItem.getParent() != null) {
            if (currentItem != item)
                pathList.add(currentItem.getValue());
            currentItem = currentItem.getParent();
        }
        for (int i = pathList.size() - 1; i >= 0; i--)
            path.append(pathList.get(i)).append("\\");

        path.append(item.getValue());
        return path.toString();
    }
}
