package ui.ui_utils;

import core.utils.Utils;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class FileBrowser
{
    /**
     * Opens a file browser, allowing the user to pick a file
     * @param stage - the current window
     * @param allowedFiles - the file extension that will be allowed
     * @return a Java File object representing the chosen file, if the user chose a file. Else, null.
     */
    public static File openFromSpecificFolder(Stage stage, String tip, String initialFolder, String ... allowedFiles)
    {
        FileChooser fileChooser = new FileChooser();
        if (initialFolder != null && Utils.folderExist(initialFolder)){
            fileChooser.setInitialDirectory(new File(initialFolder));
        }

        fileChooser.setTitle("Buscar arquivo(s)");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(tip, allowedFiles));
        return fileChooser.showOpenDialog(stage);
    }

    public static List<File> openMultipleFilesFromSpecificFolder(
            Stage stage, String tip, String initialFolder, String ... allowedFiles)
    {
        FileChooser fileChooser = new FileChooser();
        if (initialFolder != null && Utils.folderExist(initialFolder)){
            fileChooser.setInitialDirectory(new File(initialFolder));
        }

        fileChooser.setTitle("Buscar arquivo(s)");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(tip, allowedFiles));
        return fileChooser.showOpenMultipleDialog(stage);
    }

    public static List<File> openMultipleFiles(Stage stage, String tip, String ... allowedFiles)
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Buscar arquivo(s)");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(tip, allowedFiles));
        return fileChooser.showOpenMultipleDialog(stage);
    }

}
