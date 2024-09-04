package fili5rovic.jni_codeeditor.jni_codeeditor.util;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class CodeSuggestionsPane extends BorderPane {
    private final List<String> suggestions = new ArrayList<>();
    private int fontSize = 12;
    private VBox suggestionsBox;
    public CodeSuggestionsPane() {
        this.getStyleClass().clear();
        suggestionsBox = new VBox();
        suggestionsBox.getStyleClass().add("code-suggestions-pane");
        setCenter(suggestionsBox);
    }
    public void addSuggestions(List<String> suggestions) {
        this.suggestions.addAll(suggestions);
        this.setVisible(!suggestions.isEmpty());
        updateChildren();
    }

    public void updateFontSize(int fontSize) {
        this.fontSize = fontSize;
    }


    public void clearSuggestions() {
        this.suggestions.clear();
        this.setVisible(false);
        updateChildren();
    }

    public boolean hasSuggestions() {
        return !suggestions.isEmpty();
    }

    private void updateChildren() {
        suggestionsBox.getChildren().clear();
//        double layoutY = 0;
        for (String suggestion : suggestions) {
            Label sug = new Label(suggestion);
            sug.setFont(new javafx.scene.text.Font("monospace", fontSize));
            sug.setCursor(javafx.scene.Cursor.HAND);
            sug.setMinWidth(200);
            sug.getStyleClass().add("code-suggestion");
//            sug.setLayoutY(layoutY);
            suggestionsBox.getChildren().add(sug);
//            layoutY += sug.getHeight()*2 + 20;
        }
    }

    public Label getSuggestion(int index) {
        return (Label) suggestionsBox.getChildren().get(index);
    }
}
