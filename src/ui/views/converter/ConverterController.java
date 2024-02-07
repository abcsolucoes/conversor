package ui.views.converter;

import core.excel.ExcelReader;
import core.excel.AttributeExcelRow;
import core.excel.converter.MandatoryFieldsDysrup;
import core.excel.converter.MandatoryFieldsFrootyGIV;
import core.excel.converter.MandatoryFieldsGoogle;
import core.excel.converter.MandatoryFieldsGIV;
import core.excel.converter.conversion_types.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.openqa.selenium.interactions.Mouse;
import ui.ui_utils.DirectoryBrowser;
import ui.ui_utils.FileBrowser;
import ui.views.main_window.CheckBoxManager;
import ui.views.main_window.UserPreferences;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class ConverterController implements Initializable
{
    public static final String GOOGLE_LABEL_STRING = "Google";

    public static final String GIV_LABEL_STRING = "GIV";

    public static final String DYSRUP_LABEL_STRING = "Dysrup";

    private java.util.prefs.Preferences prefs;

    private String googleInitialFolderPath;

    private String givInitialFolderPath;

    private String dysrupInitialFolderPath;

    private String destinationFolderPath;

    private CheckBoxManager checkBoxManager;

    private boolean isGoogleFile;

    private boolean isInvolvesFile;

    private boolean isDysrupFile;

    private ArrayList<ArrayList<String>> googleAttributes;

    public static String typeFile;

    @FXML
    private VBox checkBoxVerticalBox;

    @FXML
    private Button chooseDestinationFolderButton, generateFilesButton;

    @FXML
    private ImageView googleIcon;

    @FXML
    private Label googleLabel;

    @FXML
    private ImageView googleTrash;

    @FXML
    private ImageView givIcon;

    @FXML
    private Label givLabel;

    @FXML
    private ImageView givTrash;

    @FXML
    private ImageView dysrupIcon;

    @FXML
    private Label dysrupLabel;

    @FXML
    private ImageView dysrupTrash;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        googleAttributes = new ArrayList<>();

        isGoogleFile = false;
        googleTrash.setVisible(false);

        isInvolvesFile = false;
        givTrash.setVisible(false);

        isDysrupFile = false;
        dysrupTrash.setVisible(false);

        // Loads the user preferences
        prefs = java.util.prefs.Preferences.userNodeForPackage(this.getClass());
        googleInitialFolderPath = prefs.get(UserPreferences.CONVERTER_GOOGLE_LAST_OPEN_FOLDER_PATH, null);
        givInitialFolderPath = prefs.get(UserPreferences.CONVERTER_INVOLVES_LAST_OPEN_FOLDER_PATH, null);
        dysrupInitialFolderPath = prefs.get(UserPreferences.CONVERTER_DYSRUP_LAST_OPEN_FOLDER_PATH, null);

        chooseDestinationFolderButton.setTooltip(new Tooltip("Nenhuma pasta destino selecionada"));

        generateFilesButton.setDisable(true);

        checkBoxManager = new CheckBoxManager(checkBoxVerticalBox);
        addListeners();
    }

    private void addListeners()
    {
        generateFilesButton.setOnAction(this::generateFilesPressed);
        chooseDestinationFolderButton.setOnAction(this::chooseDestinationFolderButtonPressed);

        googleIcon.setOnMouseClicked(this::googleFileIconPressed);
        googleTrash.setOnMouseClicked(this::googleTrashPressed);

        givIcon.setOnMouseClicked(this::givFileIconPressed);
        givTrash.setOnMouseClicked(this::givTrashPressed);

        dysrupIcon.setOnMouseClicked(this::dysrupFileIconPressed);
        dysrupTrash.setOnMouseClicked(this::dysrupTrashPressed);

        for(Node node : checkBoxVerticalBox.getChildren())
        {
            CheckBox checkBox = (CheckBox) node;
            checkBox.setOnMouseClicked(checkBoxManager::checkBoxClicked);
        }
    }

    // GOOGLE

    private void googleFileIconPressed(MouseEvent event)
    {
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        List<File> chosenFiles = FileBrowser.openMultipleFilesFromSpecificFolder(
                window, "Arquivos excel",googleInitialFolderPath, "*.xlsx");
        if (chosenFiles != null)
        {
            updateDirectoryButtonPath(chosenFiles.get(0));
            changeGoogleFileIcon(Paths.get("assets", "images", "excel_icon.png").toString());
            StringBuilder filenames = new StringBuilder();
            for (File file : chosenFiles)
            {
                filenames.append(file.getPath()).append("\n");
            }
            googleLabel.setText(filenames.toString());
            googleInitialFolderPath = chosenFiles.get(0).getParent();

            prefs.put(UserPreferences.CONVERTER_GOOGLE_LAST_OPEN_FOLDER_PATH, googleInitialFolderPath);
            generateFilesButton.setDisable(false);

            isGoogleFile = true;
            //Caso os dois icones estejam ativados desativa o botao
            if (isInvolvesFile) {
                generateFilesButton.setDisable(true);
            } else if (isDysrupFile) {
                generateFilesButton.setDisable(true);
            }
            googleTrash.setVisible(true);
        }
    }

    private void googleTrashPressed(MouseEvent event)
    {
        isGoogleFile = false;
        //caso apenas o involve esteja ativo
        if (isInvolvesFile)
            generateFilesButton.setDisable(false);
        googleTrash.setVisible(false);
        googleLabel.setText(GOOGLE_LABEL_STRING);
        changeGoogleFileIcon(Paths.get("assets", "images", "upload_excel_icon.png").toString());
        if (givLabel.getText().equals(GIV_LABEL_STRING))
        {
            generateFilesButton.setDisable(true);
        }
    }

    private ArrayList<AttributeExcelRow> readSheetDataGoogle(String filePath) throws Exception
    {
        ExcelReader excelReader = null;
        ArrayList<AttributeExcelRow> sheetData;
        Path path = Paths.get(filePath);

        try
        {
            excelReader = new ExcelReader(path.toString(), MandatoryFieldsGoogle.MANDATORY_FIELDS_GOOGLE);
            sheetData = excelReader.readCurrentSheet();
            googleAttributes.add(excelReader.getAttributes());
            excelReader.closeFile();
        }
        catch (Exception e)
        {

            e.printStackTrace();

            if (excelReader != null)
            {
                try
                {
                    excelReader.closeFile();
                }
                catch (IOException ioe)
                {
                    ioe.printStackTrace();
                }
            }

            throw new Exception(e.getMessage());
        }

        return sheetData;
    }

    private void changeGoogleFileIcon(String iconPath)
    {
        Image image = new Image(iconPath);
        googleIcon.setImage(image);
    }

    // GIV

    private void givFileIconPressed(MouseEvent event)
    {
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
        File chosenFile = FileBrowser.openFromSpecificFolder(window, "Arquivos excel", givInitialFolderPath,"*.xlsx");
        if (chosenFile != null)
        {
            updateDirectoryButtonPath(chosenFile);
            changeInvolvesFileIcon(Paths.get("assets", "images", "excel_icon.png").toString());
            givLabel.setText(chosenFile.getPath());
            givInitialFolderPath = chosenFile.getParent();
            prefs.put(UserPreferences.CONVERTER_INVOLVES_LAST_OPEN_FOLDER_PATH, givInitialFolderPath);
            generateFilesButton.setDisable(false);

            isInvolvesFile = true;
            //Caso o icone do google tb esteja ativo desativa botao
            if (isGoogleFile)
                generateFilesButton.setDisable(true);
            givTrash.setVisible(true);
        }
    }

    private void givTrashPressed(MouseEvent event)
    {
        isInvolvesFile = false;
        //Caso apenas o google esteja ativado
        if (isGoogleFile || isDysrupFile)
            generateFilesButton.setDisable(false);
        givTrash.setVisible(false);
        givLabel.setText(GIV_LABEL_STRING);
        changeInvolvesFileIcon(Paths.get("assets", "images", "upload_excel_icon.png").toString());
        if (googleLabel.getText().equals(GOOGLE_LABEL_STRING))
        {
            generateFilesButton.setDisable(true);
        }
    }

    private ArrayList<AttributeExcelRow> readSheetDataGIV(String filePath) throws Exception
    {
        ExcelReader excelReader = null;
        ArrayList<AttributeExcelRow> sheetData;
        Path path = Paths.get(filePath);

        ArrayList<String> mandatoryFields = MandatoryFieldsGIV.MANDATORY_FIELDS_GIV;

        if (path.toString().toLowerCase().contains("frooty"))
        {
            mandatoryFields = MandatoryFieldsFrootyGIV.MANDATORY_FIELDS_GIV;
        }

        try
        {
            excelReader = new ExcelReader(path.toString(), mandatoryFields);
            sheetData = excelReader.readCurrentSheet();
            excelReader.closeFile();
        }
        catch (Exception e)
        {
            if (excelReader != null)
            {
                try
                {
                    excelReader.closeFile();
                }
                catch (IOException ioe)
                {
                    ioe.printStackTrace();
                }
            }

            throw new Exception(e.getMessage());
        }

        return sheetData;
    }

    private void changeInvolvesFileIcon(String iconPath)
    {
        Image image = new Image(iconPath);
        givIcon.setImage(image);
    }

    //DYSRUP

    private void dysrupFileIconPressed(MouseEvent event)
    {
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        List<File> chosenFiles = FileBrowser.openMultipleFilesFromSpecificFolder(
                window, "Arquivos excel",dysrupInitialFolderPath, "*.xlsx");
        if (chosenFiles != null)
        {
            updateDirectoryButtonPath(chosenFiles.get(0));
            changeDysrupFileIcon(Paths.get("assets", "images", "excel_icon.png").toString());
            StringBuilder filenames = new StringBuilder();
            for (File file : chosenFiles)
            {
                filenames.append(file.getPath()).append("\n");
            }
            dysrupLabel.setText(filenames.toString());
            dysrupInitialFolderPath = chosenFiles.get(0).getParent();

            prefs.put(UserPreferences.CONVERTER_DYSRUP_LAST_OPEN_FOLDER_PATH, dysrupInitialFolderPath);
            generateFilesButton.setDisable(false);

            isDysrupFile = true;
            //Caso os dois icones estejam ativados desativa o botao
            if (isInvolvesFile) {
                generateFilesButton.setDisable(true);
            } else if (isGoogleFile) {
                generateFilesButton.setDisable(true);
            }
            dysrupTrash.setVisible(true);
        }
    }

    private void changeDysrupFileIcon(String iconPath)
    {
        Image image = new Image(iconPath);
        dysrupIcon.setImage(image);
    }

    private void dysrupTrashPressed(MouseEvent event)
    {
        isDysrupFile = false;
        //Caso apenas o dysrup esteja ativado
        if (isGoogleFile || isInvolvesFile) {
            generateFilesButton.setDisable(false);
        }
        dysrupTrash.setVisible(false);
        dysrupLabel.setText(DYSRUP_LABEL_STRING);
        changeDysrupFileIcon(Paths.get("assets", "images", "upload_excel_icon.png").toString());
        if (dysrupLabel.getText().equals(DYSRUP_LABEL_STRING)) {
            generateFilesButton.setDisable(true);
        }
    }

    private ArrayList<AttributeExcelRow> readSheetDataDysrup(String filePath) throws Exception
    {
        ExcelReader excelReader = null;
        ArrayList<AttributeExcelRow> sheetData;
        Path path = Paths.get(filePath);

        ArrayList<String> mandatoryFields = MandatoryFieldsDysrup.MANDATORY_FIELDS_DYSRUP;

        try
        {
            excelReader = new ExcelReader(path.toString(), mandatoryFields);
            sheetData = excelReader.readCurrentSheet();
            excelReader.closeFile();
        }
        catch (Exception e)
        {
            if (excelReader != null)
            {
                try
                {
                    excelReader.closeFile();
                }
                catch (IOException ioe)
                {
                    ioe.printStackTrace();
                }
            }

            throw new Exception(e.getMessage());
        }

        return sheetData;
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

    // CONVERTER
    private void generateFilesPressed(ActionEvent event){
        destinationFolderPath = chooseDestinationFolderButton.getText();
        if (destinationFolderPath == null)
        {
            String message = "Você deve escolher uma pasta destino!";
            Alert alert = new Alert(Alert.AlertType.WARNING,message, ButtonType.OK);
            alert.show();
            return;
        }

        CheckBox selectedCheckBox = getSelectedCheckBox();
        if (selectedCheckBox == null)
        {
            String message = "Você deve escolher pelo menos um modo de conversão!";
            Alert alert = new Alert(Alert.AlertType.WARNING,message, ButtonType.OK);
            alert.setResizable(true);
            alert.getDialogPane().setMinWidth(500);
            alert.show();
            return;
        }

        // Updates the interface
        this.generateFilesButton.setDisable(true);

        Dialog<String> runningDialog = new Dialog<>();
        runningDialog.setTitle("Progresso");
        runningDialog.setContentText("Lendo o arquivo original ...");
        runningDialog.show();

        boolean read = true;
        chooseDestinationFolderButton.setText(destinationFolderPath);

        String [] googleFilePath = this.googleLabel.getText().split("\n");
        String givFilePath = this.givLabel.getText().split("\n")[0];
        String dysrupFilePath = this.dysrupLabel.getText().split("\n")[0];
        String filePath = null;

        ArrayList<ArrayList<AttributeExcelRow>> sheetDataGoogle = new ArrayList<>();
        ArrayList<AttributeExcelRow> sheetData = null;

        if (isInvolvesFile) {
            try
            {
                sheetData = readSheetDataGIV(givFilePath);
                filePath = givFilePath;
                typeFile = GIV_LABEL_STRING;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.show();
                this.generateFilesButton.setDisable(false);
                read = false;
            }
        }

        if (isGoogleFile) {
            String[] filePaths = this.googleLabel.getText().split("\n");
            typeFile = GOOGLE_LABEL_STRING;
            filePath = filePaths[0];
            for (String path : filePaths) {
                try {
                    ArrayList<AttributeExcelRow> googleSheetData = readSheetDataGoogle(path);
                    sheetDataGoogle.add(googleSheetData);
                } catch (Exception e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    alert.show();
                    this.generateFilesButton.setDisable(false);
                    read = false;
                }
            }
        }

        if (isDysrupFile) {
            try {
                sheetData = readSheetDataDysrup(dysrupFilePath);
                filePath = dysrupFilePath;
                typeFile = DYSRUP_LABEL_STRING;
            }
            catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.show();
                this.generateFilesButton.setDisable(false);
                read = false;
            }
        }

        runningDialog.setResult("");
        runningDialog.close();

        if (!read)
            return;

        // Converting the data
        runningDialog = new Dialog<>();
        runningDialog.setTitle("Progresso");
        runningDialog.setContentText("Convertendo ...");
        runningDialog.show();

        boolean result = true;
        String exceptionMessage = null;

        try
        {
            convertTheData(sheetDataGoogle, sheetData,
                    googleFilePath,
                    Paths.get(filePath).getFileName().toString(),
                    selectedCheckBox);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            result = false;
            exceptionMessage = e.getMessage();
        }

        runningDialog.setResult("");
        runningDialog.close();
        this.generateFilesButton.setDisable(false);

        String message = String.format("%s\n\n%s\n\n%s", "Arquivo(s) gerado(s) com sucesso!",
                    "O arquivo convertido se encontra no local abaixo.", destinationFolderPath);
        Alert.AlertType alertType = Alert.AlertType.INFORMATION;
        if (!result)
        {
            message = "Não foi possível converter o(s) arquivo(s)\nErro: " + exceptionMessage;
            alertType = Alert.AlertType.ERROR;
        }

        Alert alert = new Alert(alertType, message, ButtonType.OK);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.show();
    }

    private void convertTheData(ArrayList<ArrayList<AttributeExcelRow>> sheetDataGoogle,
                                ArrayList<AttributeExcelRow> sheetDataGIV,
                                String[] googleFileName, String givFileName,
                                CheckBox selectedCheckBox)
            throws Exception
    {
        String checkBoxId = selectedCheckBox.getId();

        switch (checkBoxId)
        {
            case "print":
                if (givFileName.toLowerCase().contains("frooty"))
                {
                    FrootyPrintConverter printConverter = new FrootyPrintConverter();
                    printConverter.generateCorrectExcel(sheetDataGoogle, googleFileName,
                            sheetDataGIV, destinationFolderPath, googleAttributes);
                }
                else {
                    PrintConverter printConverter = new PrintConverter();
                    printConverter.generateCorrectExcel(sheetDataGoogle, googleFileName,
                            sheetDataGIV, destinationFolderPath, googleAttributes);
                }
                break;
            case "sendTo":
                if (Objects.requireNonNull(sheetDataGoogle).size() == 0 &&
                    sheetDataGIV != null && sheetDataGIV.size() > 0)
                {
                    if (givFileName.toLowerCase().contains("frooty"))
                    {
                        FrootySendToClientConverter converter = new FrootySendToClientConverter();
                        converter.generateCorrectExcel(sheetDataGIV, givFileName, destinationFolderPath);
                    }
                    else {
                        AbstractExcelConverter converter = new SendToClientConverter();
                        converter.generateCorrectExcel(sheetDataGIV, givFileName, destinationFolderPath);
                    }
                }
                else{
                    String message = "A funcionalidade de ENVIO PARA CLIENTE aceita apenas GIV ORIGINAL.";
                    throw new Exception(message);
                }
                break;
        }
    }

    // CHECKBOX
    private CheckBox getSelectedCheckBox()
    {
        for(Node node : checkBoxVerticalBox.getChildren())
        {
            CheckBox checkBox = (CheckBox) node;
            if (checkBox.isSelected())
            {
                return checkBox;
            }
        }

        return null;
    }

}