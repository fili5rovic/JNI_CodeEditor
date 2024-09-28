package fili5rovic.jni_codeeditor.jni_codeeditor.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandLineUtil {

    private static final String javaHome = System.getenv("JAVA_HOME");


    public static void doCommand(String command, File workingDirectory) throws IOException, InterruptedException {
        if (javaHome == null)
            throw new RuntimeException("[RUNTIME_ERROR] JAVA_HOME environment variable is not set.");

        String[] args = command.split(" ");
        Process process = getProcess(args, workingDirectory);

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        int exitCode = process.waitFor();
        System.out.println("Compilation finished with exit code: " + exitCode);

    }

    public static void createJNI(String[] cppFileNames, String javaFileName, File workingDirectory) throws IOException, InterruptedException {
        createJavaHeaderFile(javaFileName, workingDirectory);
        removeFilesWithExtension(workingDirectory, ".class");
        for (String cppFileName : cppFileNames)
            createObjectFile(cppFileName, workingDirectory);
        createSharedLibrary(cppFileNames, workingDirectory);
    }

    public static void createJavaHeaderFile(String className, File workingDirectory) throws IOException, InterruptedException {
        doCommand("javac -h . " + className, workingDirectory);
    }

    public static void createObjectFile(String cppFileName, File workingDirectory) throws IOException, InterruptedException {
        doCommand("g++ -c -I\"" + javaHome + "\\include\" -I\"" + javaHome + "\\include\\win32\" " + cppFileName + ".cpp -o " + cppFileName + ".o -m64", workingDirectory);
    }

    public static void createSharedLibrary(String[] cppFileNames, File workingDirectory) throws IOException, InterruptedException {
        StringBuilder command = new StringBuilder("g++ -I\"" + javaHome + "\\include\" -I\"" + javaHome + "\\include\\win32\" -shared -o native.dll -m64");
        for (String cppFileName : cppFileNames) {
            command.append(" ").append(cppFileName).append(".cpp");
        }
        doCommand(command.toString(), workingDirectory);
        removeFilesWithExtension(workingDirectory, ".o");
    }


    private static Process getProcess(String[] args, File workingDirectory) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(args);

        processBuilder.directory(workingDirectory);

        processBuilder.redirectErrorStream(true);

        return processBuilder.start();
    }

    private static void removeFilesWithExtension(File workingDirectory, String extension) {
        if (!workingDirectory.exists() || !workingDirectory.isDirectory()) {
            throw new IllegalArgumentException("The specified working directory does not exist or is not a directory.");
        }
        if(!extension.startsWith("."))
            extension = "." + extension;

        String finalExtension = extension;
        File[] files = workingDirectory.listFiles((dir, name) -> name.endsWith(finalExtension));

        if (files == null || files.length == 0) {
            System.out.println("No files (*" + extension + ") found in the working directory.");
            return;
        }

        for (File file : files) {
            if (file.delete()) {
                System.out.println("Deleted: " + file.getName());
            } else {
                System.out.println("Failed to delete: " + file.getName());
            }
        }
    }



    public static void main(String[] args) {
        File workingDirectory = new File("D:\\PROJECTS\\JavaCustomProjects\\JNI_Example_Project\\src\\main\\java\\fili5rovic\\jni_codeeditor");
        try {
            createJNI(new String[]{"Main"}, "Main.java", workingDirectory);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
