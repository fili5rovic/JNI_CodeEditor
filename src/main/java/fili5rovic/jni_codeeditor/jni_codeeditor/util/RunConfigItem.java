package fili5rovic.jni_codeeditor.jni_codeeditor.util;


import javafx.scene.control.MenuItem;

public class RunConfigItem extends MenuItem {
    private final String libraryPath;
    private final String mainClassName;


    public RunConfigItem(String name, String libraryPath, String mainClassName) {
        super(name);
        this.libraryPath = libraryPath;
        this.mainClassName = mainClassName;
        System.out.println("RunConfigItem: " + name + " " + libraryPath + " " + mainClassName);
    }

    @Override
    public String toString() {
        return getText();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof RunConfigItem))
            return false;

        RunConfigItem item = (RunConfigItem) other;
        return this.getText().equals(item.getText()) &&
                this.libraryPath.equals(item.libraryPath) &&
                this.mainClassName.equals(item.mainClassName);
    }

    public String fileOutput() {
        return String.format("%s,%s,%s", getText(), libraryPath, mainClassName);
    }

    public String getLibraryPath() {
        return libraryPath;
    }

    public String getMainClassName() {
        return mainClassName;
    }
}
