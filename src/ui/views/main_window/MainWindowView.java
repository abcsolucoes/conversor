package ui.views.main_window;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ui.views.WindowManager;

import java.io.IOException;

public class MainWindowView
{
    public static final String FXML_PATH = "main_window.fxml";

    public MainWindowView(Stage window) throws IOException
    {
        window.setResizable(true);
        WindowManager.setFullScreen(window);
        window.setTitle("Main");
        Parent root = FXMLLoader.load(getClass().getResource(FXML_PATH));
        window.setScene(new Scene(root));
        window.show();
    }

}
