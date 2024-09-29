package fili5rovic.jni_codeeditor.jni_codeeditor.util;

import fili5rovic.jni_codeeditor.jni_codeeditor.smart_code_area.ProjectManager;

import javax.tools.*;
import java.io.*;
import java.util.Collections;
/**
 * Manages the compilation and execution of Java code in an isolated environment.
 */
public class JavaCodeManager {

    private static String outputDirectoryPath = ".jnice/isolated-classes";
    private static String libraryPath = "D:\\PROJECTS\\JavaCustomProjects\\JNI_Example_Project\\native";

    /**
     * Compiles and runs the code using the specified configuration.
     *
     * @param selectedItem The configuration to use
     */
    public static void runCodeUsingConfig(RunConfigItem selectedItem) {
        if (selectedItem == null) {
            return;
        }
        FileHelper.findAllFilesInDirectoryByExtension(new File(ProjectManager.getSourcesRootPath()), ".java");
        File[] files = FileHelper.findAllFilesInDirectoryByExtension(new File(ProjectManager.getSourcesRootPath()), ".java");
        String[] fileNames = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            fileNames[i] = files[i].getAbsolutePath();
        }
        setLibraryPath(selectedItem.getLibraryPath());
        try {
            compileAndRun(fileNames, selectedItem.getMainClassName());
        } catch (IOException e) {
            System.out.println("[COMPILE_ERROR] Compilation error caught!");
            throw new RuntimeException(e);
        }
    }


    /**
     * Compiles and runs the specified source files in an isolated environment.
     *
     * @param sourceFiles Paths to the source files to compile
     * @throws IOException If an I/O error occurs
     */
    public static void compileAndRun(String[] sourceFiles, String mainClassPath) throws IOException {
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
            runCompiledCode(outputDirectory, mainClassPath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Runs the compiled code in a separate Java process.
     *
     * @param outputDirectory The directory containing the compiled classes
     * @throws Exception If an error occurs while running the code
     */
    private static void runCompiledCode(File outputDirectory, String mainClassPath) throws Exception {
        // Start a new Java process with the specified library path
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-Djava.library.path=" + libraryPath, "-cp", outputDirectory.getPath(), mainClassPath);
        processBuilder.inheritIO(); // Redirect output to the console
        Process process = processBuilder.start();
        int exitCode = process.waitFor(); // Wait for the process to finish
        System.out.println("Process exited with code: " + exitCode);
    }


    /**
     * Deletes the output directory and creates a new one.
     */
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
        compileAndRun(sourceFilesStrings, "fili5rovic.jni_codeeditor.Main");
    }

    public static void setLibraryPath(String libraryPath) {
        JavaCodeManager.libraryPath = libraryPath;
    }

    public static void setOutputDirectoryPath(String outputDirectoryPath) {
        JavaCodeManager.outputDirectoryPath = outputDirectoryPath;
    }
}
