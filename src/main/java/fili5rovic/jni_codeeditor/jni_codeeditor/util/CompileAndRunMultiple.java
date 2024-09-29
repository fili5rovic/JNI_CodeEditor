package fili5rovic.jni_codeeditor.jni_codeeditor.util;

import javax.tools.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;

public class CompileAndRunMultiple {

    private static String outputDirectoryPath = ".jnice/isolated-classes";
    private static String libraryPath = "D:\\PROJECTS\\JavaCustomProjects\\JNI_Example_Project\\native";

    public static void compileAndRun(String[] sourceFiles) throws IOException {
        cleanOutputDirectory();
        // Obtain system Java compiler (JDK needs to be installed)
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null)
            throw new IllegalStateException("Cannot find the system Java compiler. Ensure that you are running a JDK, not a JRE.");

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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    private static void runCompiledCode(File outputDirectory) throws Exception {
        // Start a new Java process with the specified library path
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-Djava.library.path=" + libraryPath, "-cp", outputDirectory.getPath(), "fili5rovic.jni_codeeditor.Main");
        processBuilder.inheritIO(); // Redirect output to the console
        Process process = processBuilder.start();
        int exitCode = process.waitFor(); // Wait for the process to finish
        System.out.println("Process exited with code: " + exitCode);
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
                "D:\\PROJECTS\\JavaCustomProjects\\JNI_Example_Project\\src\\main\\java\\fili5rovic\\jni_codeeditor\\Main.java",
        };


        compileAndRun(sourceFilesStrings);
    }

    public static void setLibraryPath(String libraryPath) {
        CompileAndRunMultiple.libraryPath = libraryPath;
    }

    public static void setOutputDirectoryPath(String outputDirectoryPath) {
        CompileAndRunMultiple.outputDirectoryPath = outputDirectoryPath;
    }
}
