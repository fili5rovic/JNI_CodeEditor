package fili5rovic.jni_codeeditor.jni_codeeditor.util;

import javax.tools.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;

public class CompileAndRunMultiple {

    public static String outputDirectoryPath = ".jnice/isolated-classes";

    public static void compileAndRun(String[] sourceFiles) throws IOException {
        cleanOutputDirectory();
        // Obtain system Java compiler (JDK needs to be installed)
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("Cannot find the system Java compiler. Ensure that you are running a JDK, not a JRE.");
        }
        // Set up a file manager for the compiler
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

        File outputDirectory = new File(outputDirectoryPath);
        fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Collections.singleton(outputDirectory));

        // Only compile the specific source files, isolated from the rest of the project
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(sourceFiles);
        boolean compilationResult = compiler.getTask(null, fileManager, null, null, null, compilationUnits).call();

        fileManager.close();
        if (!compilationResult) {
            System.out.println("Compilation failed.");
            return;
        }

        try {
            runCompiledCode(outputDirectory);
        } catch (InvocationTargetException | IllegalAccessException | ClassNotFoundException | MalformedURLException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static void runCompiledCode(File outputDirectory) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, ClassNotFoundException, MalformedURLException {
        // Only use the compiled output directory in the class loader
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{outputDirectory.toURI().toURL()});

        // Load the specific class by its full name
        Class<?> cls = Class.forName("fili5rovic.jni_codeeditor.jni_codeeditor.Test1", true, classLoader);
        Method main = cls.getMethod("main", String[].class);
        main.invoke(null, (Object) new String[]{});
    }

    private static void cleanOutputDirectory() {
        File outputDirectory = new File(".jnice/isolated-classes");
        FileHelper.deleteDirectory(new File(outputDirectoryPath));

        if (!outputDirectory.exists())
            outputDirectory.mkdirs();
    }

    public static void main(String[] args) throws Exception {
        // Source files to compile (no need for other project dependencies)
        String[] sourceFilesStrings = new String[]{
                "src/main/java/fili5rovic/jni_codeeditor/jni_codeeditor/Test1.java",
                "src/main/java/fili5rovic/jni_codeeditor/jni_codeeditor/Test2.java"
        };
        compileAndRun(sourceFilesStrings);
    }
}
