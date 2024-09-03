package fili5rovic.jni_codeeditor.jni_codeeditor.util;

import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.PopupControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
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

        HighLighter(Language language) {
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
        private static final int DEFAULT_FONT_SIZE = 12;
        private static final int MAX_FONT_SIZE = 40;
        private static final int MIN_FONT_SIZE = 8;
        private int currentFontSize = DEFAULT_FONT_SIZE;

        FontManager(boolean shouldResizeOnScroll) {
            if (shouldResizeOnScroll) {
                scrollListener();
            }
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
            SmartCodeArea.this.setStyle(STR."-fx-font-size: \{currentFontSize}px;");
//            SmartCodeArea.this.lookup(".lineno").setStyle(STR."-fx-font-size: \{currentFontSize}px;");
        }
    }

    static class TabManager {
        private static EventHandler<KeyEvent> tabEventHandler;

        static void activateTabListener(SmartCodeArea codeArea, int tabSize) {
            if (tabEventHandler != null) {
                codeArea.removeEventFilter(KeyEvent.KEY_PRESSED, tabEventHandler);
            }

            tabEventHandler = (KeyEvent event) -> {
                if (event.getCode() == KeyCode.TAB) {
                    event.consume();
                    String tab = " ".repeat(tabSize);
                    int caretPosition = codeArea.getCaretPosition();
                    codeArea.replaceText(caretPosition, caretPosition, tab);
                }
            };

            codeArea.addEventFilter(KeyEvent.KEY_PRESSED, tabEventHandler);
        }
    }

    static class CodeSuggestionsManager {
        private final PopupControl suggestionsPopup;
        private final VBox suggestionBox; // You can use a ListView or other container

        CodeSuggestionsManager(CodeArea codeArea) {
            suggestionBox = new VBox();
            suggestionsPopup = new PopupControl();
            suggestionsPopup.getScene().setRoot(suggestionBox);

            addSuggestionsForCurrentLine();

            codeArea.addEventHandler(KeyEvent.KEY_TYPED, event -> showSuggestions(codeArea));
        }

        private void addSuggestionsForCurrentLine() {
            suggestionBox.getChildren().add(new Label("Suggestion 1"));
            suggestionBox.getChildren().add(new Label("Suggestion 2"));
        }

        private void showSuggestions(CodeArea codeArea) {
            Window window = codeArea.getScene().getWindow();
            if (window != null) {
                double x = codeArea.getCaretBounds().map(bounds -> bounds.getMinX() + codeArea.getLayoutX()).orElse(0.0);
                double y = codeArea.getCaretBounds().map(bounds -> bounds.getMinY() + codeArea.getLayoutY()).orElse(0.0);

                suggestionsPopup.show(window, x, y);
            }
        }

        public void hideSuggestions() {
            suggestionsPopup.hide();
        }
    }

    private HighLighter highlighter = null;
    private FontManager fontManager = null;

    private CodeSuggestionsManager codeSuggestionsManager = null;

    private int tabSize = 3;

    public SmartCodeArea() {
        super();
        init();
    }

    public SmartCodeArea(Language language) {
        this();
        setLanguage(language);
    }


    public void setLanguage(Language language) {
        highlighter = new HighLighter(language);
    }

    private void init() {
        setParagraphGraphicFactory(LineNumberFactory.get(this));
        fontManager = new FontManager(true);
        codeSuggestionsManager = new CodeSuggestionsManager(this);
        TabManager.activateTabListener(this, tabSize);
    }

}