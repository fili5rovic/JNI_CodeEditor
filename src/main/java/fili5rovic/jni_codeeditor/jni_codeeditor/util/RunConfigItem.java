package fili5rovic.jni_codeeditor.jni_codeeditor.util;

import javafx.scene.control.MenuItem;

public class RunConfigItem extends MenuItem {
    private final String mainClassPath;
    private final String mainClassName;

    public RunConfigItem(String name, String mainClassPath, String mainClassName) {
        super(name);
        this.mainClassPath = mainClassPath;
        this.mainClassName = mainClassName;
    }

    @Override
    public String toString() {
        return getText();
    }

    public String fileOutput() {
        return String.format("%s,%s,%s", getText(), mainClassPath, mainClassName);
    }
}
