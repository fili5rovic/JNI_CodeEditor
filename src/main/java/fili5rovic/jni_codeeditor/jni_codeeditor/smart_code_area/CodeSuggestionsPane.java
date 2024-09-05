package fili5rovic.jni_codeeditor.jni_codeeditor.smart_code_area;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class CodeSuggestionsPane extends BorderPane {
    private final List<String> suggestions = new ArrayList<>();
    private int fontSize = 12;
    private final VBox suggestionsBox = new VBox();

    private int selectedIndex = 0;

    private SmartCodeArea codeArea;

    public CodeSuggestionsPane(SmartCodeArea codeArea) {
        this.codeArea = codeArea;
        this.getStyleClass().clear();
        suggestionsBox.getStyleClass().add("code-suggestions-pane");
        setCenter(suggestionsBox);
        clearSuggestions();
    }


    public void updateFontSize(int fontSize) {
        this.fontSize = fontSize;
    }


    public boolean hasSuggestions() {
        return !suggestions.isEmpty();
    }


    public void addSuggestions(List<String> suggestions) {
        this.suggestions.addAll(suggestions);
        if (suggestions.isEmpty())
            this.setVisible(false);
        else {
            this.setVisible(true);
            updateChildren();
        }
    }

    public void clearSuggestions() {
        this.suggestions.clear();
        this.setVisible(false);
        updateChildren();
    }

    public Label getSuggestion(int index) {
        return (Label) suggestionsBox.getChildren().get(index);
    }

    public Label getSelectedSuggestion() {
        return getSuggestion(selectedIndex);
    }

    public void selectNext() {
        getSuggestion(selectedIndex).getStyleClass().remove("code-suggestion-selected");
        selectedIndex++;
        if (selectedIndex >= suggestions.size())
            selectedIndex = 0;
        getSuggestion(selectedIndex).getStyleClass().add("code-suggestion-selected");
    }

    public void selectPrevious() {
        getSuggestion(selectedIndex).getStyleClass().remove("code-suggestion-selected");
        selectedIndex--;
        if (selectedIndex < 0)
            selectedIndex = suggestions.size() - 1;
        getSuggestion(selectedIndex).getStyleClass().add("code-suggestion-selected");
    }

    public void selectSuggestion(int index) {
        if (index >= 0 && index < suggestions.size()) {
            getSuggestion(selectedIndex).getStyleClass().remove("code-suggestion-selected");
            selectedIndex = index;
            getSuggestion(selectedIndex).getStyleClass().add("code-suggestion-selected");
        }
    }

    private void updateChildren() {
        suggestionsBox.getChildren().clear();
        for (int i = 0; i < suggestions.size(); i++) {
            String suggestionText = suggestions.get(i);
            var sug = new Suggestion(suggestionText, i, codeArea);
            sug.setFont(new javafx.scene.text.Font("monospace", fontSize));
            sug.setCursor(javafx.scene.Cursor.HAND);
            sug.setMinWidth(200);
            suggestionsBox.getChildren().add(sug);
        }
        selectedIndex = 0;
        selectSuggestion(0);
    }
}
