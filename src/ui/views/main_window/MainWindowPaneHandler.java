package ui.views.main_window;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.HashMap;

public class MainWindowPaneHandler
{
    private final BorderPane mainPane;

    private BorderPane currentPane;

    // A hashmap that maps the name of the fxml file to its correspondent pane
    private final HashMap<String, BorderPane> paneMap;

    public MainWindowPaneHandler(BorderPane mainPane)
    {
        this.mainPane = mainPane;
        this.currentPane = mainPane;
        this.paneMap = new HashMap<>();
    }

    public void setPane(String xmlPath)
    {
        BorderPane newPane = paneMap.get(xmlPath);

        if (currentPane == newPane)
        {
            return;
        }

        if (newPane == null)
        {
            try
            {
                newPane = createPane(xmlPath);
                paneMap.put(xmlPath, newPane);
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }

        mainPane.setCenter(newPane);
        currentPane = newPane;
    }

    private BorderPane createPane(String xmlPath) throws IOException
    {
        return FXMLLoader.load(getClass().getResource(xmlPath));
    }
}
