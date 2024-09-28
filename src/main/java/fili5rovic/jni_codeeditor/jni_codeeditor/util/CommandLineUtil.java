package fili5rovic.jni_codeeditor.jni_codeeditor.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class CommandLineUtil {

    private static final String javaHome = System.getenv("JAVA_HOME");

    /**
     * Creates a C++ file from a Java file. Method creates a header file from the Java file and then creates a C++ file. If the C++ file already exists, the method does nothing.
     *
     * @param javaFileName     The name of the Java file with the .java extension
     * @param workingDirectory The directory where the Java file is located
     * @throws IOException          If no files are found in the working directory, if no header file is found in the working directory
     * @throws InterruptedException If the process is interrupted
     */
    public static void createCppFileFromJavaFile(String javaFileName, File workingDirectory) throws IOException, InterruptedException {
        createJavaHeaderFile(javaFileName, workingDirectory);
        removeFilesWithExtension(workingDirectory, ".class");

        File[] files = workingDirectory.listFiles();
        if (files == null || files.length == 0) {
            throw new IOException("No files found in the working directory.");
        }
        Arrays.sort(files, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
        if (!files[0].getName().endsWith(".h")) {
            System.out.println("Most recent file:" + files[0].getName());
            throw new IOException("No header file found in the working directory.");
        }

        createCppFileFromHeader(javaFileName.replace(".java", ""), files[0], workingDirectory);
    }

    /**
     * Creates a shared library from cpp files.
     * @param cppFileNames The names of the cpp files without the extension
     * @param workingDirectory The directory where the Java file is located and where the shared library will be created
     * @throws IOException If the file cannot be created
     * @throws InterruptedException If the process is interrupted
     */
    public static void createDLL(ArrayList<String> cppFileNames, File workingDirectory) throws IOException, InterruptedException {
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


    public static void createSharedLibrary(ArrayList<String> cppFileNames, File workingDirectory) throws IOException, InterruptedException {
        StringBuilder command = new StringBuilder("g++ -I\"" + javaHome + "\\include\" -I\"" + javaHome + "\\include\\win32\" -shared -o native.dll -m64");
        for (String cppFileName : cppFileNames) {
            command.append(" ").append(cppFileName).append(".cpp");
        }
        doCommand(command.toString(), workingDirectory);
        removeFilesWithExtension(workingDirectory, ".o");
    }

    /**
     * Creates a C++ file from a header file. The method reads the header file and creates a C++ file with the same name as the header file. If the C++ file already exists, the method does nothing.
     *
     * @param fileName         The name of the C++ file without the extension
     * @param headerFile       The header file from which the C++ file will be created
     * @param workingDirectory The directory where the C++ file will be created
     * @throws IOException If the file cannot be created
     */
    private static void createCppFileFromHeader(String fileName, File headerFile, File workingDirectory) throws IOException {
        File file = new File(workingDirectory, fileName + ".cpp");
        if (file.exists())
            return;

        if (!file.exists() && !file.createNewFile())
            throw new IOException("Failed to create file: " + file.getName());

        StringBuilder content = new StringBuilder();
        content.append("#include \"").append(headerFile.getName()).append("\"\n");
        content.append("#include <iostream>");

        String headerFileContent = FileHelper.readFromFile(headerFile);
        String methods = headerFileContent.split("#endif")[1].split("#ifdef")[0];
        methods = methods.replaceAll("/\\*\\n.*\\n.*\\n.*\\n.*\\*/", "~");
        for (String method : methods.split("~")) {
            method = method.replace("\n", "").replace("  ", "");
            method = method.replace(";", " {\n\n}\n");
            content.append(method).append("\n\n");
        }
        FileHelper.writeToFile(file, content.toString());
        System.out.println(content);
    }

    private static void addMissingStuffToCppFile(String fileName, File headerFile, File workingDirectory) throws IOException {
        File file = new File(workingDirectory, fileName + ".cpp");
        if (!file.exists())
            throw new IOException("The specified file does not exist.");
    }


    //<editor-fold desc="Process">
    private static void doCommand(String command, File workingDirectory) throws IOException, InterruptedException {
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
        if (!extension.startsWith("."))
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
    //</editor-fold>


    public static void main(String[] args) {
        File workingDirectory = new File("D:\\PROJECTS\\JavaCustomProjects\\JNI_Example_Project\\src\\main\\java\\fili5rovic\\jni_codeeditor");
        try {
//            ArrayList<String> cppFiles = new ArrayList<>();
//            cppFiles.add("Main");
//            createJNI(cppFiles, "Main.java", workingDirectory);
            createCppFileFromJavaFile("Main.java", workingDirectory);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
