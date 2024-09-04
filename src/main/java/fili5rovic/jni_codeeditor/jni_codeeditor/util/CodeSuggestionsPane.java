package fili5rovic.jni_codeeditor.jni_codeeditor.util;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class CodeSuggestionsPane extends VBox {
    private List<String> suggestions = new ArrayList<>();

    public CodeSuggestionsPane() {
        this.getStyleClass().add("code-suggestions-pane");
    }
    public void addSuggestions(List<String> suggestions) {
        this.suggestions.addAll(suggestions);
        updateChildren();
    }

    public void clearSuggestions() {
        this.suggestions.clear();
        updateChildren();
    }

    private void updateChildren() {
        this.getChildren().clear();
        double layoutY = 0;
        for (String suggestion : suggestions) {
            Button btn = new Button(suggestion);
            btn.getStyleClass().add("code-suggestion");
            btn.setLayoutY(layoutY);
            this.getChildren().add(btn);
            layoutY += btn.getHeight()*2 + 5; // Adjust the spacing as needed
        }
    }
}
