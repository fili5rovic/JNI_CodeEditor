package fili5rovic.jni_codeeditor.jni_codeeditor.smart_code_area;

import fili5rovic.jni_codeeditor.jni_codeeditor.util.FileHelper;

import java.io.File;

public class SavingManager {
    private final SmartCodeArea codeArea;
    private final File file;
    boolean hasUnsavedChanges = false;

    public SavingManager(SmartCodeArea codeArea, File file) {
        this.codeArea = codeArea;
        this.file = file;
        listeners();
    }

    private void listeners() {
        codeArea.focusedProperty().addListener((_, _, isFocused) -> {
            if(!isFocused && hasUnsavedChanges) {
                FileHelper.writeToFile(file, codeArea.getText());
                System.out.println("Wrote to file.");
                hasUnsavedChanges = false;
            }
        });
        codeArea.textProperty().addListener((_, a, b) -> {
            hasUnsavedChanges = !a.equals(b);
        });
    }
}
