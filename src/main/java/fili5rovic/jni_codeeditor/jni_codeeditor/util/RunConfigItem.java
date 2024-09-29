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

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null) return false;
        if (other instanceof RunConfigItem item) {
            return this.getText().equals(item.getText()) &&
                    this.mainClassPath.equals(item.mainClassPath) &&
                    this.mainClassName.equals(item.mainClassName);
        } else return false;
    }

    public String fileOutput() {
        return String.format("%s,%s,%s", getText(), mainClassPath, mainClassName);
    }
}
