module fili5rovic.jni_codeeditor.jni_codeeditor {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.fxmisc.richtext;
    requires java.compiler;
    requires org.fxmisc.flowless;


    exports fili5rovic.jni_codeeditor.jni_codeeditor;
    exports fili5rovic.jni_codeeditor.jni_codeeditor.controller;

    opens fili5rovic.jni_codeeditor.jni_codeeditor.controller to javafx.fxml;
}
