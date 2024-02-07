package ui.views.login;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import ui.views.main_window.MainWindowView;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable
{
    @FXML
    private Button loginButton, closeButton;

    @FXML
    private Label forgotPasswordLabel, supportLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        loginButton.setOnAction(this::loginButtonClicked);
        closeButton.setOnAction(this::closeButtonClicked);
        forgotPasswordLabel.setOnMouseClicked(this::forgotPasswordLabelClicked);
        supportLabel.setOnMouseClicked(this::supportLabelClicked);
    }

    void loginButtonClicked(ActionEvent event) {
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
        try
        {
            new MainWindowView(window);
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    void closeButtonClicked(ActionEvent event)
    {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    void forgotPasswordLabelClicked(MouseEvent event) {
    }

    void supportLabelClicked(MouseEvent event) {
    }
}
