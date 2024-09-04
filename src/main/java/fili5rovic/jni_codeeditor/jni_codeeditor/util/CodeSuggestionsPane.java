package fili5rovic.jni_codeeditor.jni_codeeditor.util;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class CodeSuggestionsPane extends VBox {
    private List<String> suggestions = new ArrayList<>();
    private int fontSize = 12;
    public CodeSuggestionsPane() {
        this.getStyleClass().clear();
        this.getStyleClass().add("code-suggestions-pane");
    }
    public void addSuggestions(List<String> suggestions) {
        this.suggestions.addAll(suggestions);
        this.setMinWidth(200);
        updateChildren();
    }

    public void updateFontSize(int fontSize) {
        this.fontSize = fontSize;
    }


    public void clearSuggestions() {
        this.suggestions.clear();
        updateChildren();
    }

    public boolean hasSuggestions() {
        return !suggestions.isEmpty();
    }

    private void updateChildren() {
        this.getChildren().clear();
//        double layoutY = 0;
        for (String suggestion : suggestions) {
            Label sug = new Label(suggestion);
            sug.setFont(new javafx.scene.text.Font("monospace", fontSize));
            sug.setCursor(javafx.scene.Cursor.HAND);
            sug.setMinWidth(200);
            sug.getStyleClass().add("code-suggestion");
//            sug.setLayoutY(layoutY);
            this.getChildren().add(sug);
//            layoutY += sug.getHeight()*2 + 20;
        }
    }
}
