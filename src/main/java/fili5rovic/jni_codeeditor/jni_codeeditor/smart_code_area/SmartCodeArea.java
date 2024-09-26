package fili5rovic.jni_codeeditor.jni_codeeditor.smart_code_area;

import fili5rovic.jni_codeeditor.jni_codeeditor.util.FileHelper;
import javafx.scene.paint.Paint;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SmartCodeArea extends CodeArea {

    static class HighLighter {
        private final Pattern PATTERN;
        private Language language ;

        HighLighter(SmartCodeArea codeArea) {
            this.language = codeArea.getLanguage();
            String[] KEYWORDS = FileHelper.getKeywordsForLanguage(language);
            String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
            String PAREN_PATTERN = "[()]";
            String BRACE_PATTERN = "[{}]";
            String BRACKET_PATTERN = "[\\[\\]]";
            String SEMICOLON_PATTERN = ";";
            String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
            String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";
            PATTERN = Pattern.compile("(?<KEYWORD>" + KEYWORD_PATTERN + ")|(?<PAREN>" + PAREN_PATTERN + ")|(?<BRACE>" + BRACE_PATTERN + ")|(?<BRACKET>" + BRACKET_PATTERN + ")|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")|(?<STRING>" + STRING_PATTERN + ")|(?<COMMENT>" + COMMENT_PATTERN + ")");

            codeArea.textProperty().addListener((_, _, _) -> applyHighlighting(codeArea));
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

    private HighLighter highlighter = null;
    public FontManager fontManager = null;

    public CodeSuggestionsManager codeSuggestionsManager = null;

    private CodeSuggestionsPane codeSuggestionsPane = null;

    private Language language = Language.JAVA;


    public SmartCodeArea() {
        init();
    }

    public SmartCodeArea(Language language) {
        this();
        setLanguage(language);
    }

    private void init() {
        setLineHighlighterOn(true);
        setLineHighlighterFill(Paint.valueOf("#d0e1f9"));
        setWrapText(true);
        this.getStyleClass().add("smart-code-area");
        setParagraphGraphicFactory(LineNumberFactory.get(this));

        highlighter = new HighLighter(this);
        fontManager = new FontManager(this,true);
        codeSuggestionsManager = new CodeSuggestionsManager(this);
    }

    public CodeSuggestionsPane getCodeSuggestionsPane() {
        return codeSuggestionsPane;
    }
    public void setCodeSuggestionsPane(CodeSuggestionsPane codeSuggestionsPane) {
        this.codeSuggestionsPane = codeSuggestionsPane;
    }

    public Language getLanguage() {
        return language;
    }
    public void setLanguage(Language language) {
        this.language = language;
    }
}