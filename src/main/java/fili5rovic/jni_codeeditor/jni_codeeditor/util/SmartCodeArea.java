package fili5rovic.jni_codeeditor.jni_codeeditor.util;

import javafx.scene.input.ScrollEvent;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SmartCodeArea extends CodeArea {

    static class SmartCodeAreaHighlighter {
        private final Pattern PATTERN;

        SmartCodeAreaHighlighter(Language language) {
            String[] KEYWORDS = FileHelper.getKeywordsForLanguage(language);
            String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
            String PAREN_PATTERN = "[()]";
            String BRACE_PATTERN = "[{}]";
            String BRACKET_PATTERN = "[\\[\\]]";
            String SEMICOLON_PATTERN = ";";
            String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
            String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";
            PATTERN = Pattern.compile("(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
            );

            for(String keyword : KEYWORDS) {
                System.out.println(keyword);
            }
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
    }

    private SmartCodeAreaHighlighter highlighter = null;

    private int currentFontSize;
    private static final int DEFAULT_FONT_SIZE = 12;
    private static final int MAX_FONT_SIZE = 40;
    private static final int MIN_FONT_SIZE = 8;

    public SmartCodeArea() {
        super();
        init();
        listeners();
    }

    public SmartCodeArea(Language language) {
        this();
        setLanguage(language);
    }


    public void setLanguage(Language language) {
        highlighter = new SmartCodeAreaHighlighter(language);
    }

    private void init() {
        textProperty().addListener((obs, oldText, newText) -> highlighter.applyHighlighting(this));
        setParagraphGraphicFactory(LineNumberFactory.get(this));
        currentFontSize = DEFAULT_FONT_SIZE;
    }


    private void listeners() {
        this.addEventFilter(ScrollEvent.SCROLL, (ScrollEvent event) -> {
            if (event.isControlDown()) {
                boolean scrollUp = event.getDeltaY() > 0;
                currentFontSize += scrollUp ? 1 : -1;
                currentFontSize = Math.min(Math.max(currentFontSize, MIN_FONT_SIZE), MAX_FONT_SIZE);
                setStyle("-fx-font-size: " + currentFontSize + "px;");
                event.consume();
            }
        });
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