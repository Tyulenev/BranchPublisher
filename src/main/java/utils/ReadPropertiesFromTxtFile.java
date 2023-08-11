package utils;

import lombok.extern.java.Log;

import java.io.*;
import java.net.URL;

@Log
public class ReadPropertiesFromTxtFile {

//    final private String PATH_TO_MESSAGES_FILE = "\\WEB-INF\\classes\\mailMessages.txt";

    private String mailMessagesTemplateFullFile;

    public ReadPropertiesFromTxtFile() throws IOException {
        mailMessagesTemplateFullFile = readFileMessageTemplate();
    }

    private String readFileMessageTemplate() throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream is = classLoader.getResourceAsStream("mailMessages.txt");
        Reader in = new InputStreamReader(is, "UTF-8");
        final StringBuilder out = new StringBuilder();
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        for (; ; ) {
            int rsz = in.read(buffer, 0, buffer.length);
            if (rsz < 0)
                break;
            out.append(buffer, 0, rsz);
        }
        return out.toString();
    }


//    private String readFileMessageTemplate() {
////        String pathToFile = ReadPropertiesFromTxtFile.class.getResource("mailMessages.txt").toString();
////        InputStream is = ReadPropertiesFromTxtFile.class.getResourceAsStream("/mailMessages.txt");
////        URL url = this.getClass().getClassLoader().getResource("mailMessages.txt");
//        String fileContent = "";
//        try (BufferedReader br = new BufferedReader(
//                new InputStreamReader(
//                        new FileInputStream(PATH_TO_MESSAGES_FILE), "UTF-8"))) {
//            String sub;
//            while ((sub = br.readLine()) != null) {
//                fileContent = String.format("%s%s\n", fileContent, sub);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return fileContent;
//    }

    public String getProperties (String nameProp) {
        String[] stringLines = mailMessagesTemplateFullFile.split("\n");
        for (String oneLineProp : stringLines) {
            if (oneLineProp.contains(nameProp)) {
                String[] prop = oneLineProp.split("=");
                if (prop[0].contains(nameProp)) {
                    return prop[1];
                }
            }
        }
        return null;
    }

}
