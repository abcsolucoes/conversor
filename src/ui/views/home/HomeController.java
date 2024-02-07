package ui.views.home;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML
    private ImageView abcLogo;

    @FXML
    private Pane topPane;

    @FXML
    private Label currentScreenNameLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        changeFileIcon(Paths.get("assets", "images", "abc_solucoes_logo_site.png").toString());

    }

    private void changeFileIcon(String iconPath)
    {
        Image image = new Image(iconPath);
        abcLogo.setImage(image);
    }

}
