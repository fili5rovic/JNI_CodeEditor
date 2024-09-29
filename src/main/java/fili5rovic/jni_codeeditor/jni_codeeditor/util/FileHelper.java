package fili5rovic.jni_codeeditor.jni_codeeditor.util;

import fili5rovic.jni_codeeditor.jni_codeeditor.smart_code_area.Language;

import java.io.*;

public class FileHelper {

    public static String readFromInputStream(InputStream inputStream) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            sb.append(br.readLine());
            while ((line = br.readLine()) != null) {
                sb.append('\n').append(line);
            }
        } catch (IOException e) {
            System.out.println("Couldn't read file");
        }
        return sb.toString();
    }

    public static String readFromFile(File file) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            sb.append(br.readLine());
            while ((line = br.readLine()) != null) {
                sb.append('\n').append(line);
            }
        } catch (IOException e) {
            System.out.println("Couldn't read file");
        }
        return sb.toString();
    }

    public static void writeToFile(File file, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
        } catch (IOException e) {
            System.out.println("Couldn't write to file");
        }
    }

    /**
     * Deletes a directory and all of its contents
     * @param directory The directory to delete
     */
    public static void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            directory.delete();
        }
    }

    public static String[] getKeywordsForLanguage(Language language) {
        String fileName = "";
        switch (language) {
            case JAVA -> fileName = "java-keywords.txt";
            case CPP ->  fileName = "cpp-keywords.txt";
            case CSS -> fileName = "css-keywords.txt";
        }
        String path = "/fili5rovic/jni_codeeditor/jni_codeeditor/keywords/" + fileName;
        InputStream inputStream = FileHelper.class.getResourceAsStream(path);
        String content = FileHelper.readFromInputStream(inputStream);
        String[] keywords = content.split(",");
        for(int i = 0; i < keywords.length; i++) {
            keywords[i] = keywords[i].trim();
        }
        return keywords;
    }


}
