package ui.views.route;

import core.excel.ExcelReader;
import core.excel.AttributeExcelRow;
import core.excel.ExcelRow;
import core.excel.ExcelUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import ui.ui_utils.DirectoryBrowser;
import ui.ui_utils.FileBrowser;
import ui.views.main_window.UserPreferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import static core.excel.route.RouteGenerator.generateRoute;

public class RouteGeneratorController implements Initializable
{
    private java.util.prefs.Preferences prefs;

    private String initialFolderPath;

    private String destinationFolderPath;

    @FXML
    private ImageView fileIcon;

    @FXML
    private Label filenameLabel;

    @FXML
    private ImageView trashIcon;

    @FXML
    private Button generateRouteButton;

    @FXML
    private Button chooseDestinationFolderButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        trashIcon.setVisible(false);

        // Loads the user preferences
        prefs = java.util.prefs.Preferences.userNodeForPackage(this.getClass());
        initialFolderPath = prefs.get(UserPreferences.ROUTES_LAST_OPEN_FOLDER_PATH, null);

        chooseDestinationFolderButton.setTooltip(new Tooltip("Nenhuma pasta destino selecionada"));

        generateRouteButton.setDisable(true);
        addListeners();
    }

    private void addListeners(){
        generateRouteButton.setOnAction(this::generateFilesPressed);
        chooseDestinationFolderButton.setOnAction(this::chooseDestinationFolderButtonPressed);

        fileIcon.setOnMouseClicked(this::fileIconPressed);

        trashIcon.setOnMouseClicked(this::trashIconPressed);
    }

    // ICON

    private void fileIconPressed(MouseEvent event)
    {
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        List<File> chosenFiles = FileBrowser.openMultipleFilesFromSpecificFolder(
                window, "Arquivos excel",initialFolderPath, "*.xlsx");
        if (chosenFiles != null)
        {
            updateDirectoryButtonPath(chosenFiles.get(0));
            changeFileIcon(Paths.get("assets", "images", "excel_icon.png").toString());
            StringBuilder filenames = new StringBuilder();
            for (File file : chosenFiles)
            {
                filenames.append(file.getPath()).append("\n");
            }
            changeLabelText(filenames.toString());
            initialFolderPath = chosenFiles.get(0).getParent();

            prefs.put(UserPreferences.PRODUCTS_LAST_OPEN_FOLDER_PATH, initialFolderPath);
            generateRouteButton.setDisable(false);

            trashIcon.setVisible(true);
        }
    }

    private void trashIconPressed(MouseEvent event)
    {
        trashIcon.setVisible(false);
        String LABEL_STRING = "Escolha um ou mais arquivos";
        changeLabelText(LABEL_STRING);
        changeFileIcon(Paths.get("assets", "images", "upload_excel_icon.png").toString());
        generateRouteButton.setDisable(true);
    }

    private void changeFileIcon(String iconPath)
    {
        Image image = new Image(iconPath);
        fileIcon.setImage(image);
    }

    private void changeLabelText(String filename)
    {
        filenameLabel.setText(filename);
    }

    // DESTINATION

    private void updateDirectoryButtonPath(File chosenFile)
    {
        String folderPath = chosenFile.getParent();
        chooseDestinationFolderButton.setText(folderPath);
        chooseDestinationFolderButton.setTooltip(new Tooltip(folderPath));
    }

    private void chooseDestinationFolderButtonPressed(ActionEvent actionEvent)
    {
        Stage window = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        File path = DirectoryBrowser.open(window);
        if (path != null)
        {
            destinationFolderPath = path.getPath();
            chooseDestinationFolderButton.setText(destinationFolderPath);
        }
    }

    private void generateFilesPressed(ActionEvent event)
    {
        destinationFolderPath = chooseDestinationFolderButton.getText();
        if (destinationFolderPath == null) {
            String message = "Você deve escolher uma pasta destino!";
            Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
            alert.show();
            return;
        }

        // Updates the interface
        this.generateRouteButton.setDisable(true);

        Dialog<String> runningDialog = new Dialog<>();
        runningDialog.setTitle("Progresso");
        runningDialog.setContentText("Lendo os arquivos de rota ...");
        runningDialog.show();

        ArrayList<ExcelRow> allRows = new ArrayList<>();
        boolean read = true;
        String[] filePaths = this.filenameLabel.getText().split("\n");
        for (String path : filePaths)
        {
            try
            {
                ArrayList<ExcelRow> sheetData = readWithoutAttributes(path);
                allRows.addAll(sheetData);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.show();
                this.generateRouteButton.setDisable(false);
                read = false;
            }
        }

        runningDialog.setResult("");
        runningDialog.close();

        if (!read) {
            return;
        }

        // Converting the data
        runningDialog = new Dialog<>();
        runningDialog.setTitle("Progresso");
        runningDialog.setContentText("Convertendo ...");
        runningDialog.show();

        boolean result = true;
        String exceptionMessage = null;

        try
        {
            generateRoute(allRows, destinationFolderPath);
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
            exceptionMessage = e.getMessage();
        }

        runningDialog.setResult("");
        runningDialog.close();
        this.generateRouteButton.setDisable(false);

        String message = String.format("%s\n\n%s\n\n%s", "Arquivo(s) gerado(s) com sucesso!",
                "O arquivo convertido se encontra no local abaixo.", destinationFolderPath);
        Alert.AlertType alertType = Alert.AlertType.INFORMATION;
        if (!result) {
            message = "Não foi possível converter o(s) arquivo(s)\nErro: " + exceptionMessage;
            alertType = Alert.AlertType.ERROR;
        }

        Alert alert = new Alert(alertType, message, ButtonType.OK);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.show();
    }

    public static ArrayList<ExcelRow> readWithoutAttributes(String filePath) throws Exception
    {
        ArrayList<ExcelRow> sheetData = new ArrayList<>();

        FileInputStream fileInputStream = null;
        try
        {
            fileInputStream = new FileInputStream(filePath);
            Workbook workbook = WorkbookFactory.create(fileInputStream);
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 0; i <= sheet.getLastRowNum(); i++)
            {
                Row row = sheet.getRow(i);
                ArrayList<String> values = new ArrayList<>();
                for (int j = 0; j < row.getLastCellNum(); j++)
                {
                    String cellValue = ExcelUtils.getStringCellValue((XSSFCell) row.getCell(j));
                    values.add(cellValue);
                }
                ExcelRow excelRow = new ExcelRow(values);
                sheetData.add(excelRow);
            }
        }
        catch (IOException e)
        {
            String message = e.getMessage();

            try
            {
                assert fileInputStream != null;
                fileInputStream.close();
            }
            catch (IOException ex)
            {
                message += '\n' + ex.getMessage();
            }

            throw new Exception(message);
        }
        catch(IllegalArgumentException e)
        {
            throw new Exception("Não foi possível realizar o processo. Verifique se o MODO DE EDIÇÃO está habilitado.");
        }
        catch(Exception e)
        {
            throw new Exception("Algo deu errado ao ler o arquivo.\n" +
                    "Verifique se a primeira linha do arquivo está preenchida corretamente.\n" + e.getMessage());
        }

        return sheetData;
    }

}