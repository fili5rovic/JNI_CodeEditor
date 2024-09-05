package fili5rovic.jni_codeeditor.jni_codeeditor.smart_code_area;

import javafx.scene.input.ScrollEvent;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class FontManager {
    private static final int DEFAULT_FONT_SIZE = 18;
    private static final int MAX_FONT_SIZE = 50;
    private static final int MIN_FONT_SIZE = 10;
    private int currentFontSize = DEFAULT_FONT_SIZE;
    private final SmartCodeArea codeArea;

    FontManager(SmartCodeArea codeArea, boolean shouldResizeOnScroll) {
        this.codeArea = codeArea;
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
        codeArea.addEventFilter(ScrollEvent.SCROLL, (ScrollEvent event) -> {
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
        codeArea.setStyle(STR."-fx-font-size: \{currentFontSize}px;");
        CodeSuggestionsPane codeSuggestionsPane = codeArea.getCodeSuggestionsPane();
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

    public int getCurrentFontSize() {
        return currentFontSize;
    }
}
