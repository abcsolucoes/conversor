package ui.views.login;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginView
{
    private static final String FXML_PATH = "login.fxml";

    public LoginView(Stage window) throws IOException
    {
        window.setTitle("Login");
        window.setResizable(false);
        Parent root = FXMLLoader.load(getClass().getResource(FXML_PATH));
        window.setScene(new Scene(root));
        window.show();
    }
}
