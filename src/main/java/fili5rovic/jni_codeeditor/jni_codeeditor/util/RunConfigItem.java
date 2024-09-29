package fili5rovic.jni_codeeditor.jni_codeeditor.util;


import fili5rovic.jni_codeeditor.jni_codeeditor.smart_code_area.ProjectManager;
import javafx.scene.control.MenuItem;

import java.io.File;
import java.io.IOException;

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
        if (!(other instanceof RunConfigItem item))
            return false;

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
