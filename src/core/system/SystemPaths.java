package core.system;


import java.nio.file.Paths;

public class SystemPaths
{
    public static final String PROJECT_PATH = Paths.get(System.getProperty("user.dir")).toString();
}
