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
    private static final String ICON_PLUS = ICONS_PATH + "plus.png";
    private static final String ICON_REFRESH = ICONS_PATH + "refresh.png";
    private static final String ICON_RUN = ICONS_PATH + "run.png";


    public static ImageView getImageViewByFileExtension(File file) {
        if (file.isDirectory()) {
            if (file.listFiles() == null || file.listFiles().length == 0) {
                return new ImageView(ICON_FOLDER_EMPTY);
            }
            return new ImageView(ICON_FOLDER);
        }

        String fileName = file.getName();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        String path = switch (extension) {
            case "java" -> ICON_JAVA;
            case "h", "hpp" -> ICON_H;
            case "c", "cpp" -> ICON_C;
            case "txt" -> ICON_TXT;
            case "png" -> ICON_PNG;
            case "dll" -> ICON_DLL;
            default -> ICON_FILE;
        };
        return new ImageView(path);
    }

    public static ImageView getIcon(Icon icon) {
        return switch (icon) {
            case FOLDER -> new ImageView(ICON_FOLDER);
            case FOLDER_EMPTY -> new ImageView(ICON_FOLDER_EMPTY);
            case FILE -> new ImageView(ICON_FILE);
            case JAVA -> new ImageView(ICON_JAVA);
            case C -> new ImageView(ICON_C);
            case H -> new ImageView(ICON_H);
            case TXT -> new ImageView(ICON_TXT);
            case PNG -> new ImageView(ICON_PNG);
            case DLL -> new ImageView(ICON_DLL);
            case PLUS -> new ImageView(ICON_PLUS);
            case REFRESH -> new ImageView(ICON_REFRESH);
            case RUN -> new ImageView(ICON_RUN);
        };
    }

    public static ImageView getCollapseIcon() {
        return new ImageView(ICONS_PATH + "light-theme/collapse.png");
    }

}
