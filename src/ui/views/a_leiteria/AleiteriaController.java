package ui.views.a_leiteria;

import core.abc.*;
import core.excel.*;
import core.excel.a_leiteria.AleiteriaReport;
import core.excel.converter.MandatoryFieldsGoogle;
import core.excel.converter.MandatoryFieldsGIV;
import core.excel.converter.RequestItem;
import core.utils.Utils;
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
import ui.ui_utils.FileBrowser;
import ui.views.main_window.CheckBoxManager;
import ui.views.main_window.UserPreferences;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class AleiteriaController implements Initializable
{
    private final String GOOGLE_LABEL_STRING = "Google";

    private final String GIV_LABEL_STRING = "GIV";

    private final String REPORT_LABEL_STRING = "Relatório";

    private java.util.prefs.Preferences prefs;

    private String initialFolderPathGoogle;
    private String initialFolderPathInvolves;
    private String initialFolderPathReport;

    private CheckBoxManager checkBoxManager;

    private ArrayList<AttributeExcelRow> googleSheetData;
    private ArrayList<AttributeExcelRow> involvesSheetData;

    private String googleFilePath;
    private String involvesFilePath;
    private String reportFilePath;

    @FXML
    private ImageView googleOriginalIcon;

    @FXML
    private Label googleFileLabel;

    @FXML
    private ImageView googleTrash;

    @FXML
    private ImageView involvesOriginalIcon;

    @FXML
    private Label involvesFileLabel;

    @FXML
    private ImageView involvesTrash;

    @FXML
    private ImageView reportIconUse;

    @FXML
    private Label reportFileLabel;

    @FXML
    private ImageView reportTrashUse;

    @FXML
    private Button updateFileButton;

    @FXML
    private VBox checkBoxVerticalBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        googleTrash.setVisible(false);
        involvesTrash.setVisible(false);
        reportTrashUse.setVisible(false);

        // Loads the user preferences
        prefs = java.util.prefs.Preferences.userNodeForPackage(this.getClass());
        initialFolderPathGoogle = prefs.get(UserPreferences.ALEITERIA_GOOGLE_LAST_OPEN_FOLDER_PATH, null);
        initialFolderPathInvolves = prefs.get(UserPreferences.ALEITERIA_INVOLVES_LAST_OPEN_FOLDER_PATH, null);
        initialFolderPathReport = prefs.get(UserPreferences.ALEITERIA_REPORT_LAST_OPEN_FOLDER_PATH, null);

        googleSheetData = involvesSheetData = null;
        updateFileButton.setDisable(true);
        googleFilePath = "";
        involvesFilePath = "";
        checkBoxManager = new CheckBoxManager(checkBoxVerticalBox);
        addListeners();
    }

    private void addListeners()
    {
        googleOriginalIcon.setOnMouseClicked(this::fileIconPressed);
        involvesOriginalIcon.setOnMouseClicked(this::fileIconPressed);
        reportIconUse.setOnMouseClicked(this::fileIconPressed);
        updateFileButton.setOnAction(this::updateFileButtonPressed);

        googleTrash.setOnMouseClicked(this::googleTrashPressed);
        involvesTrash.setOnMouseClicked(this::involvesTrashPressed);
        reportTrashUse.setOnMouseClicked(this::reportTrashPressed);

        for(Node node : checkBoxVerticalBox.getChildren())
        {
            CheckBox checkBox = (CheckBox) node;
            checkBox.setOnMouseClicked(checkBoxManager::checkBoxClicked);
        }
    }

    // ICON

    private void changeFileIcon(ImageView fileIcon, String iconPath)
    {
        Image image = new Image(iconPath);
        fileIcon.setImage(image);
    }

    private void changeLabelText(Label label, String fileName)
    {
        label.setText(fileName);
    }

    private void fileIconPressed(MouseEvent event)
    {
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
        ImageView imgView = (ImageView)event.getSource();
        File chosenFile = null;
        if (googleOriginalIcon.equals(imgView)) {
            chosenFile = FileBrowser.openFromSpecificFolder(
                    window, "Arquivos excel", initialFolderPathGoogle, "*.xlsx");
        } else if (involvesOriginalIcon.equals(imgView)) {
            chosenFile = FileBrowser.openFromSpecificFolder(
                    window, "Arquivos excel", initialFolderPathInvolves, "*.xlsx");
        } else if (reportIconUse.equals(imgView)) {
            chosenFile = FileBrowser.openFromSpecificFolder(
                    window, "Arquivos excel", initialFolderPathReport, "*.xlsx");
        }

        if (chosenFile != null)
        {
            String fileName = chosenFile.getName();
            String filePath = chosenFile.getPath();

            if (imgView == googleOriginalIcon)
            {
                changeFileIcon(googleOriginalIcon, Paths.get("assets", "images", "excel_icon.png").toString());
                changeLabelText(googleFileLabel, fileName);
                googleFilePath = filePath;
                initialFolderPathGoogle = chosenFile.getParent();
                prefs.put(UserPreferences.ALEITERIA_GOOGLE_LAST_OPEN_FOLDER_PATH, initialFolderPathGoogle);

                googleTrash.setVisible(true);
            }
            else if (imgView == involvesOriginalIcon)
            {
                changeFileIcon(involvesOriginalIcon, Paths.get("assets", "images", "excel_icon.png").toString());
                changeLabelText(involvesFileLabel, fileName);
                involvesFilePath = filePath;
                initialFolderPathInvolves = chosenFile.getParent();
                prefs.put(UserPreferences.ALEITERIA_INVOLVES_LAST_OPEN_FOLDER_PATH, initialFolderPathInvolves);

                involvesTrash.setVisible(true);
            }
            else if (reportIconUse.equals(imgView))
            {
                changeFileIcon(reportIconUse, Paths.get("assets", "images", "excel_icon.png").toString());
                changeLabelText(reportFileLabel, fileName);
                reportFilePath = filePath;
                updateFileButton.setDisable(false);
                initialFolderPathReport = chosenFile.getParent();
                prefs.put(UserPreferences.ALEITERIA_REPORT_LAST_OPEN_FOLDER_PATH, initialFolderPathReport);

                reportTrashUse.setVisible(true);
            }
        }
    }

    private void googleTrashPressed(MouseEvent event){
        googleSheetData = null;
        googleFilePath = "";
        googleTrash.setVisible(false);
        changeLabelText(googleFileLabel, GOOGLE_LABEL_STRING);
        changeFileIcon(googleOriginalIcon, Paths.get("assets", "images", "upload_excel_icon.png").toString());
    }

    private void involvesTrashPressed(MouseEvent event){
        involvesSheetData = null;
        involvesFilePath = "";
        involvesTrash.setVisible(false);
        changeLabelText(involvesFileLabel, GIV_LABEL_STRING);
        changeFileIcon(involvesOriginalIcon, Paths.get("assets", "images", "upload_excel_icon.png").toString());
    }

    private void reportTrashPressed(MouseEvent event){
        reportFilePath = "";
        reportTrashUse.setVisible(false);
        changeLabelText(reportFileLabel, REPORT_LABEL_STRING);
        changeFileIcon(reportIconUse, Paths.get("assets", "images", "upload_excel_icon.png").toString());
        updateFileButton.setDisable(true);
    }

    // FILE

    private void readFiles()
    {
        // Updates the interface
        updateFileButton.setDisable(true);

        boolean readFileGoogle = true;
        if (!googleFilePath.isEmpty()) {
            try {
                googleSheetData = readSheetData(googleFilePath, MandatoryFieldsGoogle.MANDATORY_FIELDS_GOOGLE);
            } catch (Exception e) {
                String message = String.format("Erro na leitura do arquivo Google:\n%s\n\n%s", googleFileLabel.getText(), e.getMessage());
                Alert.AlertType alertType = Alert.AlertType.ERROR;
                Alert alert = new Alert(alertType, message, ButtonType.OK);
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.show();
                readFileGoogle = false;
            }

        }
        if (readFileGoogle) {
            if (!involvesFilePath.isEmpty()) {
                try {
                    involvesSheetData = readSheetData(involvesFilePath, MandatoryFieldsGIV.MANDATORY_FIELDS_GIV);
                } catch (Exception e) {
                    String message = String.format("Erro na leitura do arquivo Involves original:\n%s\n\n%s", involvesFileLabel.getText(), e.getMessage());
                    Alert.AlertType alertType = Alert.AlertType.ERROR;
                    Alert alert = new Alert(alertType, message, ButtonType.OK);
                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    alert.show();
                }
            }
        }

        updateFileButton.setDisable(false);
    }

    private ArrayList<ProductRequest> getProductRequests() throws Exception
    {
        CheckBox selectedCheckBox = getSelectedCheckBox();
        assert selectedCheckBox != null;
        String bandeiraId = selectedCheckBox.getId();

        String bandeira = switch (bandeiraId) {
            case "epaCheckBox" -> "Epa - MG";
            default -> "";
        };

        involvesSheetData = Filter.filterEquals(MandatoryFieldsGIV.BANDEIRA, bandeira, involvesSheetData);
        involvesSheetData = Filter.filterEquals(MandatoryFieldsGIV.MARCA, "A LEITERIA", involvesSheetData);

        HashMap<String, ArrayList<AttributeExcelRow>> rowsByStoreName = ExcelUtils.groupByAttribute(
                MandatoryFieldsGIV.PDV, involvesSheetData);

        ArrayList<RequestItem> requestItems = new ArrayList<>();
        for (String storeName : rowsByStoreName.keySet())
        {
            ArrayList<AttributeExcelRow> rows = rowsByStoreName.get(storeName);
            for (AttributeExcelRow row : rows)
            {
                RequestItem item = new RequestItem(
                        new Product(
                                row.getAttributeValue(MandatoryFieldsGIV.NOME_PRODUTO),
                                row.getAttributeValue(MandatoryFieldsGIV.CODIGO_PRODUTO),
                                null,
                                row.getAttributeValue(MandatoryFieldsGIV.MARCA)
                        ),
                        Integer.parseInt(row.getAttributeValue(MandatoryFieldsGIV.QUANTIDADE)),
                        new Store(storeName, row.getAttributeValue(MandatoryFieldsGIV.CODIGO_PDV), null),
                        new Replenisher(row.getAttributeValue(MandatoryFieldsGIV.NOTIFICANTE)),
                        Utils.getFormattedDateUSA(row.getAttributeValue(MandatoryFieldsGIV.DATA_E_HORA_PESQUISA).split(" ")[0]),
                        null, null
                );
                requestItems.add(item);
            }
        }

        HashMap<String, ArrayList<RequestItem>> requestItemsPerStore = ProductRequestHandler.separateItemsPerStore(requestItems);
        ArrayList<ProductRequest> listOfProductRequests = getListOfProductRequests(requestItemsPerStore);
        SortManager.sortByAlphabeticStoreName(listOfProductRequests);

        return listOfProductRequests;
    }

    public static ArrayList<ProductRequest> getListOfProductRequests(HashMap<String, ArrayList<RequestItem>> itemsPerStore)
    {
        ArrayList<ProductRequest> listOfProductRequests = new ArrayList<>();

        for (String storeName : itemsPerStore.keySet())
        {
            ArrayList<RequestItem> listOfItems = itemsPerStore.get(storeName);
            RequestItem requestInformation = listOfItems.get(0);

            listOfProductRequests.add(new ProductRequest(
                    null,
                    new Store(storeName, requestInformation.getStore().getCode(), null),
                    requestInformation.getReplenisher(),
                    requestInformation.getDate(),
                    listOfItems,
                    null,
                    null
            ));
        }

        return listOfProductRequests;
    }

    private void updateFileButtonPressed(ActionEvent event)
    {
        googleSheetData = involvesSheetData = null;
        CheckBox selectedCheckBox = getSelectedCheckBox();
        if (selectedCheckBox == null)
        {
            String message = "Você deve escolher pelo menos uma bandeira!";
            Alert alert = new Alert(Alert.AlertType.WARNING,message, ButtonType.OK);
            alert.setResizable(true);
            alert.getDialogPane().setMinWidth(500);
            alert.show();
            return;
        }

        readFiles();
        ArrayList<ProductRequest> requests = null;

        if (!involvesFilePath.isEmpty() && involvesSheetData != null) {
            try {
                requests = getProductRequests();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Updating report
        Dialog<String> runningDialog = new Dialog<>();
        runningDialog.setTitle("Progresso");
        runningDialog.setContentText("Atualizando relatório ...");
        runningDialog.show();

        try {
            AleiteriaReport.updateReport(googleSheetData, requests, reportFilePath);
        } catch (Exception e) {
            e.printStackTrace();
            String message = String.format("Erro na atualização do relatório:\n\n%s\n\n%s", reportFilePath, e.getMessage());
            Alert.AlertType alertType = Alert.AlertType.ERROR;
            Alert alert = new Alert(alertType, message, ButtonType.OK);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.show();
            runningDialog.setResult("");
            runningDialog.close();
            updateFileButton.setDisable(false);
            return;
        }

        runningDialog.setResult("");
        runningDialog.close();
        updateFileButton.setDisable(false);

        String message = String.format("%s\n\n%s\n\n%s", "Relatório atualizado com sucesso com sucesso!",
                "O relatório atualizado se encontra no local abaixo.", reportFilePath);
        Alert.AlertType alertType = Alert.AlertType.INFORMATION;
        Alert alert = new Alert(alertType, message, ButtonType.OK);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.show();
    }

    private ArrayList<AttributeExcelRow> readSheetData(String filePath, ArrayList<String> mandatoryFields) throws Exception
    {
        ExcelReader excelReader = null;
        ArrayList<AttributeExcelRow> sheetData;
        Path path = Paths.get(filePath);

        try
        {
            excelReader = new ExcelReader(path.toString(), mandatoryFields);
            sheetData = excelReader.readCurrentSheet();
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
