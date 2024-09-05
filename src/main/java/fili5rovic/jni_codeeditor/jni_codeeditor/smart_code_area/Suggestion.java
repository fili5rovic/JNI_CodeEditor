package fili5rovic.jni_codeeditor.jni_codeeditor.smart_code_area;

import javafx.scene.control.Label;

public class Suggestion extends Label {
    Suggestion(String text, int id, SmartCodeArea codeArea) {
        super(text);
        this.getStyleClass().add("code-suggestion");


        this.setPrefWidth(text.length() * codeArea.fontManager.getCurrentFontWidth());

        this.setOnMouseClicked(e -> codeArea.codeSuggestionsManager.finishSuggestion());

        this.setOnMouseEntered(e -> codeArea.getCodeSuggestionsPane().selectSuggestion(id));
    }
}
