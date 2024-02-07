package ui.views.main_window;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable
{
    private MainWindowPaneHandler paneHandler;

    private boolean sideMenuExpanded;

    @FXML
    private ScrollPane sideMenu;

    @FXML
    private HBox homeHbox;

    @FXML
    private ImageView expandMenuIcon;

    @FXML
    private Button convertMenuButton;

    @FXML
    private Button routeMenuButton;

    @FXML
    private Button productsMenuButton;

    @FXML
    private Button selmiMenuButton;

    @FXML
    public Button igarapeMenuButton;

    @FXML
    public Button frootyMenuButton;

    @FXML
    public Button demarchiMenuButton;

    @FXML
    private BorderPane mainPane;

    @FXML
    private Label menuLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        sideMenuExpanded = true;
        setupListeners();
        paneHandler = new MainWindowPaneHandler(mainPane);
        paneHandler.setPane(XMLPaths.HOME_SCREEN);
    }

    private void setupListeners()
    {
        expandMenuIcon.setOnMouseClicked(this::expandMenuClicked);
        menuLabel.setOnMouseClicked(this::homeMenuButtonPressed);
        convertMenuButton.setOnAction(this::convertMenuButtonPressed);
        productsMenuButton.setOnAction(this::updateProductsButtonPressed);
        routeMenuButton.setOnAction(this::routeMenuButtonPressed);
        selmiMenuButton.setOnAction(this::selmiMenuButtonPressed);
        igarapeMenuButton.setOnAction(this::igarapeMenuButtonPressed);
        frootyMenuButton.setOnAction(this::frootyMenuButtonPressed);
        demarchiMenuButton.setOnAction(this::demarchiMenuButtonPressed);
    }

    private void expandMenuClicked(MouseEvent mouseEvent)
    {
        if (sideMenuExpanded) {
            shrinkSideMenu();
            sideMenuExpanded = false;
        }
        else {
            expandSideMenu();
            sideMenuExpanded = true;
        }
    }

    void homeMenuButtonPressed(MouseEvent mouseEvent) {
        paneHandler.setPane(XMLPaths.HOME_SCREEN);
        setBlankBackgorund ();
        homeHbox.setStyle("-fx-background-color: #f8b100");
    }

    void convertMenuButtonPressed(ActionEvent event) {
        paneHandler.setPane(XMLPaths.CONVERTER_SCREEN);
        setBlankBackgorund ();
        convertMenuButton.setStyle("-fx-background-color: #f8b100");
    }

    void routeMenuButtonPressed(ActionEvent event) {
        paneHandler.setPane(XMLPaths.ROUTE_SCREEN);
        setBlankBackgorund ();
        routeMenuButton.setStyle("-fx-background-color: #f8b100");
    }

    void updateProductsButtonPressed(ActionEvent event) {
        paneHandler.setPane(XMLPaths.UPDATE_PRODUCTS_SCREEN);
        setBlankBackgorund ();
        productsMenuButton.setStyle("-fx-background-color: #f8b100");
    }

    void selmiMenuButtonPressed(ActionEvent event) {
        paneHandler.setPane(XMLPaths.SELMI_SCREEN);
        setBlankBackgorund ();
        selmiMenuButton.setStyle("-fx-background-color: #f8b100");
    }

    void igarapeMenuButtonPressed(ActionEvent event) {
        paneHandler.setPane(XMLPaths.IGARAPE_SCREEN);
        setBlankBackgorund ();
        igarapeMenuButton.setStyle("-fx-background-color: #f8b100");
    }

    void frootyMenuButtonPressed(ActionEvent event) {
        paneHandler.setPane(XMLPaths.FROOTY_SCREEN);
        setBlankBackgorund ();
        frootyMenuButton.setStyle("-fx-background-color: #f8b100");
    }

    void demarchiMenuButtonPressed(ActionEvent event) {
        paneHandler.setPane(XMLPaths.DEMARCHI_SCREEN);
        setBlankBackgorund ();
        demarchiMenuButton.setStyle("-fx-background-color: #f8b100");
    }

    private void expandSideMenu()
    {
        // -1 sets the width to "COMPUTE_SIZE", which means "fit the data"
        sideMenu.setMinWidth(-1);
        sideMenu.setMaxWidth(-1);
        sideMenu.setPrefWidth(-1);
    }

    private void shrinkSideMenu()
    {
        sideMenu.setMinWidth(60);
        sideMenu.setMaxWidth(60);
        sideMenu.setPrefWidth(60);
    }

    private void setBlankBackgorund ()
    {
        String blank = "-fx-background-color: #ffffff";
        homeHbox.setStyle(blank);
        convertMenuButton.setStyle(blank);
        routeMenuButton.setStyle(blank);
        productsMenuButton.setStyle(blank);
        selmiMenuButton.setStyle(blank);
        igarapeMenuButton.setStyle(blank);
        frootyMenuButton.setStyle(blank);
        demarchiMenuButton.setStyle(blank);
    }
}

