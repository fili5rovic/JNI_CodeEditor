package fili5rovic.jni_codeeditor.jni_codeeditor.smart_code_area;

import fili5rovic.jni_codeeditor.jni_codeeditor.util.FileHelper;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class CodeSuggestionsManager {

    private final int tabSize = 4;

    private String lastWord = "";
    private int paragraphIndex = 0;

    private CodeSuggestionsPane codeSuggestionsPane;
    private final SmartCodeArea codeArea;

    private final HashMap<Integer, Integer> fontLineHeights = new HashMap<>();

    CodeSuggestionsManager(SmartCodeArea codeArea) {
        this.codeArea = codeArea;
        this.codeSuggestionsPane = codeArea.getCodeSuggestionsPane();
        init();
    }

    private void init() {
        listeners();
        fontLineHeights.put(10,12);
        fontLineHeights.put(18,24);
        fontLineHeights.put(19,23);
        fontLineHeights.put(20,24);
        fontLineHeights.put(21,26);
        fontLineHeights.put(22,27);
        fontLineHeights.put(23,28);
        fontLineHeights.put(24,28);
        fontLineHeights.put(25,31);
        fontLineHeights.put(26,31);
        fontLineHeights.put(27,32);
        fontLineHeights.put(28,34);
        fontLineHeights.put(29,35);
        fontLineHeights.put(30,36);
        fontLineHeights.put(31,38);
        fontLineHeights.put(32,39);
        fontLineHeights.put(33,39);
        fontLineHeights.put(34,40);
        fontLineHeights.put(35,42);
        fontLineHeights.put(36,43);
        fontLineHeights.put(37,43);
        fontLineHeights.put(38,46);
        fontLineHeights.put(39,47);
        fontLineHeights.put(40,47);
        fontLineHeights.put(41,50);
        fontLineHeights.put(42,50);
        fontLineHeights.put(43,51);
        fontLineHeights.put(44,51);
        fontLineHeights.put(45,54);
        fontLineHeights.put(46,55);
        fontLineHeights.put(47,55);
        fontLineHeights.put(48,58);
        fontLineHeights.put(49,58);
        fontLineHeights.put(50,59);
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
        codeArea.caretPositionProperty().addListener(_ -> {
            paragraphIndex = codeArea.getCurrentParagraph();
            int currentColumn = codeArea.getParagraph(paragraphIndex).length();

            Text text = new Text("Sample");
            text.setFont(new Font("monospace", codeArea.fontManager.getCurrentFontSize()));

//            double lineHeight = text.getBoundsInLocal().getHeight();
            double xOffset = 0;

            double layoutX = currentColumn * codeArea.fontManager.getCurrentFontWidth() + xOffset;
//            double layoutY = (paragraphIndex + 1) * lineHeight;
            double layoutY = (paragraphIndex + 1) * fontLineHeights.get(codeArea.fontManager.getCurrentFontSize());
            if(codeSuggestionsPane == null)
                return;
            codeSuggestionsPane.setLayoutX(layoutX);
            codeSuggestionsPane.setLayoutY(layoutY);
            suggest();
        });

        codeArea.sceneProperty().addListener((_, _, newScene) -> {
            if (newScene != null && codeSuggestionsPane == null) {
                setupSuggestionsPane();
            }
        });
        codeCompletionListener();
    }

    public void setupSuggestionsPane() {
        Pane parent = null;
        if (codeArea.getParent() instanceof Pane) {
            parent = (Pane) codeArea.getParent();
        } else if(codeArea.getParent().getParent() instanceof Pane) {
            parent = (Pane) codeArea.getParent().getParent();
        } else {
            System.out.println("Could not find parent pane to make code suggestions pane");
            return;
        }
        codeSuggestionsPane = new CodeSuggestionsPane(codeArea);
        codeArea.setCodeSuggestionsPane(codeSuggestionsPane);
        parent.getChildren().add(codeSuggestionsPane);
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
            String bracketCheck;
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