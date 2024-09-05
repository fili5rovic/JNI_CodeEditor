package fili5rovic.jni_codeeditor.jni_codeeditor.smart_code_area;

import fili5rovic.jni_codeeditor.jni_codeeditor.util.FileHelper;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class CodeSuggestionsManager {

    private int tabSize = 4;

    private String lastWord = "";
    private int paragraphIndex = 0;

    private CodeSuggestionsPane codeSuggestionsPane;
    private SmartCodeArea codeArea;

    CodeSuggestionsManager(SmartCodeArea codeArea) {
        this.codeArea = codeArea;
        this.codeSuggestionsPane = codeArea.getCodeSuggestionsPane();
        init();
    }

    private void init() {
        listeners();
    }

    private void suggest() {
        codeSuggestionsPane.clearSuggestions();
        String paragraph = codeArea.getParagraph(paragraphIndex).getText().substring(0, codeArea.getCaretColumn());
        if (paragraph.isBlank() || paragraph.endsWith(" ") || paragraph.endsWith("\n"))
            return;
        String[] words = paragraph.split(" ");
        lastWord = words[words.length - 1];
        addKeywordSuggestions();
    }

    private void addKeywordSuggestions() {
        String[] suggestions = FileHelper.getKeywordsForLanguage(codeArea.getLanguage());
        ArrayList<String> filteredSuggestions = new ArrayList<>();
        for (String suggestion : suggestions) {
            if (suggestion.startsWith(lastWord) && !suggestion.equals(lastWord)) {
                filteredSuggestions.add(suggestion);
            }
        }
        codeSuggestionsPane.addSuggestions(filteredSuggestions);
    }

    private void listeners() {
        codeArea.caretPositionProperty().addListener(e -> {
            paragraphIndex = codeArea.getCurrentParagraph();
            int currentColumn = codeArea.getParagraph(paragraphIndex).length();

            Text text = new Text("Sample");
            text.setFont(new Font("monospace", codeArea.fontManager.getCurrentFontSize()));

            double lineHeight = text.getBoundsInLocal().getHeight();
            double xOffset = 0;

            double layoutX = currentColumn * codeArea.fontManager.getCurrentFontWidth() + xOffset;
            double layoutY = (paragraphIndex + 1) * lineHeight;

//            System.out.println("Layout Y " + layoutY);
//            System.out.println("Paragraph index: " + paragraphIndex);
            codeSuggestionsPane.setLayoutX(layoutX);
            codeSuggestionsPane.setLayoutY(layoutY);
            suggest();
        });

        codeArea.sceneProperty().addListener((_, _, newScene) -> {
            if (newScene != null && codeSuggestionsPane == null) {
                if (codeArea.getParent() instanceof Pane parent) {
                    codeSuggestionsPane = new CodeSuggestionsPane(codeArea);
                    codeArea.setCodeSuggestionsPane(codeSuggestionsPane);
                    parent.getChildren().add(codeSuggestionsPane);
                }
            }
        });

        codeCompletionListener();
    }

    private void codeCompletionListener() {
        codeArea.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            bracketListener(event);
            if (codeSuggestionsPane.hasSuggestions()) {
                boolean shouldConsume = true;
                switch (event.getCode()) {
                    case TAB -> finishSuggestion();
                    case DOWN -> codeSuggestionsPane.selectNext();
                    case UP -> codeSuggestionsPane.selectPrevious();
                    case ESCAPE -> codeSuggestionsPane.clearSuggestions();
                    default -> shouldConsume = false;
                }
                if (shouldConsume)
                    event.consume();
            } else {
                if (event.getCode() == KeyCode.TAB) {
                    doTab();
                    event.consume();
                }
            }
        });
    }

    private void bracketListener(KeyEvent e) {
        if (e.getCode() == KeyCode.OPEN_BRACKET) {
            codeArea.replaceText(codeArea.getCaretPosition(), codeArea.getCaretPosition(), e.isShiftDown() ? "}" : "]");
            codeArea.moveTo(codeArea.getCaretPosition() - 1);
        } else if (e.getCode() == KeyCode.DIGIT9 && e.isShiftDown()) {
            codeArea.replaceText(codeArea.getCaretPosition(), codeArea.getCaretPosition(), ")");
            codeArea.moveTo(codeArea.getCaretPosition() - 1);
        } else if (e.getCode() == KeyCode.BACK_SPACE) {
            int caretPosition = codeArea.getCaretPosition();
            String bracketCheck = "";
            try {
                bracketCheck = codeArea.getText(caretPosition - 1, caretPosition + 1);
            } catch (IllegalArgumentException | IndexOutOfBoundsException ex) {
                return;
            }
            if (bracketCheck.equals("()") || bracketCheck.equals("[]") || bracketCheck.equals("{}")) {
                codeArea.replaceText(caretPosition - 1, caretPosition + 1, "");
            }
        }
    }

    void finishSuggestion() {
        String suggestion = codeSuggestionsPane.getSelectedSuggestion().getText();
        int caretPosition = codeArea.getCaretPosition();
        codeArea.replaceText(caretPosition - lastWord.length(), caretPosition, suggestion + " ");
        codeSuggestionsPane.clearSuggestions();
    }

    private void doTab() {
        String tab = " ".repeat(tabSize);
        int caretPosition = codeArea.getCaretPosition();
        codeArea.replaceText(caretPosition, caretPosition, tab);
    }
}