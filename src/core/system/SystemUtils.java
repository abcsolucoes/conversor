package core.system;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class SystemUtils
{
    public static boolean fileExists(String filePath)
    {
        File f = new File(filePath);
        return f.exists();
    }

    public static void deleteFile(String filePath) throws Exception
    {
        Files.deleteIfExists(Path.of(filePath));
    }
}
