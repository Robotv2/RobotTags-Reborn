package fr.robotv2.robottags.util;

import java.io.File;
import java.io.IOException;

public class FileUtil {

    public static File createFile(String path, String fileName) {
        return createFile(new File(path, fileName));
    }

    public static File createFile(File file) {

        if(file.exists()) {
            return file;
        }

        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }
}
