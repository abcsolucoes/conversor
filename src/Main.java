import javafx.application.Application;
import javafx.stage.Stage;
import ui.views.login.LoginView;
import ui.views.main_window.MainWindowView;

public class Main extends Application
{
    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        //new LoginView(primaryStage);
        new MainWindowView(primaryStage);
    }
}
