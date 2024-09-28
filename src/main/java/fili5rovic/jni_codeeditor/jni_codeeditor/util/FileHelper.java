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
