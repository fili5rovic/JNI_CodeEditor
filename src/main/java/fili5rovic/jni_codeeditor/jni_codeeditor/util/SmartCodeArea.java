package fili5rovic.jni_codeeditor.jni_codeeditor.util;

import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SmartCodeArea extends CodeArea {

    class HighLighter {
        private final Pattern PATTERN;
        private final Language language;

        HighLighter(Language language) {
            this.language = language;
            String[] KEYWORDS = FileHelper.getKeywordsForLanguage(language);
            String KEYWORD_PATTERN = STR."\\b(\{String.join("|", KEYWORDS)})\\b";
            String PAREN_PATTERN = "[()]";
            String BRACE_PATTERN = "[{}]";
            String BRACKET_PATTERN = "[\\[\\]]";
            String SEMICOLON_PATTERN = ";";
            String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
            String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";
            PATTERN = Pattern.compile(STR."(?<KEYWORD>\{KEYWORD_PATTERN})|(?<PAREN>\{PAREN_PATTERN})|(?<BRACE>\{BRACE_PATTERN})|(?<BRACKET>\{BRACKET_PATTERN})|(?<SEMICOLON>\{SEMICOLON_PATTERN})|(?<STRING>\{STRING_PATTERN})|(?<COMMENT>\{COMMENT_PATTERN})");

            SmartCodeArea.this.textProperty().addListener((_, _, _) -> highlighter.applyHighlighting(SmartCodeArea.this));
        }


        void applyHighlighting(SmartCodeArea codeArea) {
            codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText()));
        }

        private StyleSpans<Collection<String>> computeHighlighting(String text) {
            Matcher matcher = PATTERN.matcher(text);
            int lastKwEnd = 0;
            StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
            while (matcher.find()) {
                String styleClass = getString(matcher);
                spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
                spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
                lastKwEnd = matcher.end();
            }
            spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
            return spansBuilder.create();
        }

        private static String getString(Matcher matcher) {
            String styleClass = matcher.group("KEYWORD") != null ? "keyword" :
                    matcher.group("PAREN") != null ? "paren" : matcher.group("BRACE") != null ? "brace" :
                            matcher.group("BRACKET") != null ? "bracket" :
                                    matcher.group("SEMICOLON") != null ? "semicolon" :
                                            matcher.group("STRING") != null ? "string" :
                                                    matcher.group("COMMENT") != null ? "comment" :
                                                            null; /* never happens */
            assert styleClass != null;
            return styleClass;
        }
    }

    class FontManager {
        private static final int DEFAULT_FONT_SIZE = 18;
        private static final int MAX_FONT_SIZE = 50;
        private static final int MIN_FONT_SIZE = 10;
        private int currentFontSize = DEFAULT_FONT_SIZE;

        FontManager(boolean shouldResizeOnScroll) {
            if (shouldResizeOnScroll) {
                scrollListener();
            }
            updateFontSize();
        }

        private void increaseFontSize() {
            currentFontSize++;
            currentFontSize = Math.min(currentFontSize, MAX_FONT_SIZE);
        }

        private void decreaseFontSize() {
            currentFontSize--;
            currentFontSize = Math.max(currentFontSize, MIN_FONT_SIZE);
        }

        private void scrollListener() {
            SmartCodeArea.this.addEventFilter(ScrollEvent.SCROLL, (ScrollEvent event) -> {
                if (event.isControlDown()) {
                    changeFontSizeFromScroll(event);
                    event.consume();
                }
            });
        }

        private void changeFontSizeFromScroll(ScrollEvent event) {
            boolean scrollUp = event.getDeltaY() > 0;
            if (scrollUp)
                increaseFontSize();
            else
                decreaseFontSize();
            updateFontSize();
//            SmartCodeArea.this.lookup(".lineno").setStyle(STR."-fx-font-size: \{currentFontSize}px;");
        }

        private void updateFontSize() {
            SmartCodeArea.this.setStyle(STR."-fx-font-size: \{currentFontSize}px;");
            if (codeSuggestionsPane != null) {
                codeSuggestionsPane.updateFontSize(currentFontSize);
                codeSuggestionsPane.clearSuggestions();
            }
        }

        public double getCurrentFontWidth() {
            Font font = Font.font("Monospaced", currentFontSize);
            Text text = new Text("W");
            text.setFont(font);
            return text.getLayoutBounds().getWidth();
        }
    }


    class Suggestion extends Label {
        Suggestion(String text, int id) {
            super(text);
            this.getStyleClass().add("code-suggestion");

            // set pref width according to text
            this.setPrefWidth(text.length() * fontManager.getCurrentFontWidth());

            this.setOnMouseClicked(e -> codeSuggestionsManager.finishSuggestion());

            this.setOnMouseEntered(e -> codeSuggestionsPane.selectSuggestion(id));
        }
    }


    class CodeSuggestionsPane extends BorderPane {
        private final List<String> suggestions = new ArrayList<>();
        private int fontSize = 12;
        private final VBox suggestionsBox = new VBox();

        private int selectedIndex = 0;

        public CodeSuggestionsPane() {
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
                var sug = new SmartCodeArea.Suggestion(suggestionText, i);
                sug.setFont(new javafx.scene.text.Font("monospace", fontSize));
                sug.setCursor(javafx.scene.Cursor.HAND);
                sug.setMinWidth(200);
                suggestionsBox.getChildren().add(sug);
            }
            selectedIndex = 0;
            selectSuggestion(0);
        }
    }


    class CodeSuggestionsManager {

        private String lastWord = "";
        private int paragraphIndex = 0;

        CodeSuggestionsManager() {
            init();
        }

        private void init() {
            listeners();
        }

        private void suggest() {
            codeSuggestionsPane.clearSuggestions();
            String paragraph = SmartCodeArea.this.getParagraph(paragraphIndex).getText().substring(0, SmartCodeArea.this.getCaretColumn());
            if (paragraph.isBlank() || paragraph.endsWith(" ") || paragraph.endsWith("\n"))
                return;
            String[] words = paragraph.split(" ");
            lastWord = words[words.length - 1];
            addKeywordSuggestions();
        }

        private void addKeywordSuggestions() {
            String[] suggestions = FileHelper.getKeywordsForLanguage(SmartCodeArea.this.highlighter.language);
            ArrayList<String> filteredSuggestions = new ArrayList<>();
            for (String suggestion : suggestions) {
                if (suggestion.startsWith(lastWord) && !suggestion.equals(lastWord)) {
                    filteredSuggestions.add(suggestion);
                }
            }
            codeSuggestionsPane.addSuggestions(filteredSuggestions);
        }

        private void listeners() {
            SmartCodeArea.this.caretPositionProperty().addListener(e -> {
                paragraphIndex = SmartCodeArea.this.getCurrentParagraph();
                int currentColumn = SmartCodeArea.this.getParagraph(paragraphIndex).length();

                Text text = new Text("Sample");
                text.setFont(new Font("monospace", fontManager.currentFontSize));

                // Get the height of the text
                double lineHeight = text.getBoundsInLocal().getHeight();
                double xOffset = 0;

                double layoutX = currentColumn * fontManager.getCurrentFontWidth() + xOffset;
                double layoutY = (paragraphIndex + 1) * lineHeight;

                System.out.println("Layout Y " + layoutY);

                System.out.println("Paragraph index: " + paragraphIndex);
                codeSuggestionsPane.setLayoutX(layoutX);
                codeSuggestionsPane.setLayoutY(layoutY);
                suggest();
            });

            SmartCodeArea.this.sceneProperty().addListener((_, _, newScene) -> {
                if (newScene != null && codeSuggestionsPane == null) {
                    if (SmartCodeArea.this.getParent() instanceof Pane parent) {
                        codeSuggestionsPane = new CodeSuggestionsPane();
                        parent.getChildren().add(codeSuggestionsPane);
                    }
                }
            });

            codeCompletionListener();
        }

        private void codeCompletionListener() {
            SmartCodeArea.this.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
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
                SmartCodeArea.this.replaceText(SmartCodeArea.this.getCaretPosition(), SmartCodeArea.this.getCaretPosition(), e.isShiftDown() ? "}" : "]");
                SmartCodeArea.this.moveTo(SmartCodeArea.this.getCaretPosition() - 1);
            } else if (e.getCode() == KeyCode.DIGIT9 && e.isShiftDown()) {
                SmartCodeArea.this.replaceText(SmartCodeArea.this.getCaretPosition(), SmartCodeArea.this.getCaretPosition(), ")");
                SmartCodeArea.this.moveTo(SmartCodeArea.this.getCaretPosition() - 1);
            } else if (e.getCode() == KeyCode.BACK_SPACE) {
                int caretPosition = SmartCodeArea.this.getCaretPosition();
                String bracketCheck = "";
                try {
                    bracketCheck = SmartCodeArea.this.getText(caretPosition - 1, caretPosition + 1);
                } catch (IllegalArgumentException | IndexOutOfBoundsException ex) {
                    return;
                }
                if (bracketCheck.equals("()") || bracketCheck.equals("[]") || bracketCheck.equals("{}")) {
                    SmartCodeArea.this.replaceText(caretPosition - 1, caretPosition + 1, "");
                }
            }
        }

        void finishSuggestion() {
            String suggestion = codeSuggestionsPane.getSelectedSuggestion().getText();
            int caretPosition = SmartCodeArea.this.getCaretPosition();
            SmartCodeArea.this.replaceText(caretPosition - lastWord.length(), caretPosition, suggestion + " ");
            codeSuggestionsPane.clearSuggestions();
        }

        private void doTab() {
            String tab = " ".repeat(tabSize);
            int caretPosition = SmartCodeArea.this.getCaretPosition();
            SmartCodeArea.this.replaceText(caretPosition, caretPosition, tab);
        }
    }

    private HighLighter highlighter = null;
    private FontManager fontManager = null;

    private CodeSuggestionsManager codeSuggestionsManager = null;

    private CodeSuggestionsPane codeSuggestionsPane = null;

    private int tabSize = 3;


    public SmartCodeArea() {
        super();
        init();
        for (String s : getParagraph(0).getParagraphStyle()) {
            System.out.println(s);
        }

    }

    public SmartCodeArea(Language language) {
        this();
        setLanguage(language);
    }


    public void setLanguage(Language language) {
        highlighter = new HighLighter(language);
    }
    private void init() {
        setLineHighlighterOn(true);
        setLineHighlighterFill(Paint.valueOf("#d0e1f9"));
        setWrapText(true);
        this.getStyleClass().add("smart-code-area");
        setParagraphGraphicFactory(LineNumberFactory.get(this));
        fontManager = new FontManager(true);
        codeSuggestionsManager = new CodeSuggestionsManager();
    }

}