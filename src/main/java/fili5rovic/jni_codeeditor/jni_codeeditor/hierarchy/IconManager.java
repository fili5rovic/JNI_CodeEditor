package fili5rovic.jni_codeeditor.jni_codeeditor.hierarchy;

import javafx.scene.image.ImageView;

import java.io.File;

public class IconManager {
    private static final String ICONS_PATH = "file:src/main/resources/fili5rovic/jni_codeeditor/jni_codeeditor/pictures/";

    private static final String ICON_FOLDER = ICONS_PATH + "folder.png";
    private static final String ICON_FOLDER_EMPTY = ICONS_PATH + "folder-empty.png";
    private static final String ICON_FILE = ICONS_PATH + "file.png";
    private static final String ICON_JAVA = ICONS_PATH + "java-file.png";
    private static final String ICON_C = ICONS_PATH + "c-file.png";
    private static final String ICON_H = ICONS_PATH + "h-file.png";
    private static final String ICON_TXT = ICONS_PATH + "txt.png";
    private static final String ICON_PNG = ICONS_PATH + "png-file.png";
    private static final String ICON_DLL = ICONS_PATH + "dll-file.png";


    public static String getIconPathByFileExtension(File file) {
        if (file.isDirectory()) {
            if(file.listFiles() == null || file.listFiles().length == 0) {
                return ICON_FOLDER_EMPTY;
            }
            return ICON_FOLDER;
        }

        String fileName = file.getName();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        switch (extension) {
            case "java":
                return ICON_JAVA;
            case "h":
            case "hpp":
                return ICON_H;
            case "c":
            case "cpp":
                return ICON_C;
            case "txt":
                return ICON_TXT;
            case "png":
                return ICON_PNG;
            case "dll":
                return ICON_DLL;
            default:
                return ICON_FILE;
        }
    }


    public static ImageView getCollapseIcon() {
        return new ImageView(ICONS_PATH + "light-theme/collapse.png");
    }
}
