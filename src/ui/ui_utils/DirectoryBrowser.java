package ui.ui_utils;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.io.File;

public class DirectoryBrowser
{
    public static File open(Stage stage)
    {
        /**
         * Opens a directory browser, allowing the user to pick a folder
         * @param stage - the current window
         *
         * @return a Java File object representing the chosen folder, if the user chose a folder. Else, null.
         */
        DirectoryChooser directoryChooser = new DirectoryChooser();
        return directoryChooser.showDialog(stage);
    }
}
