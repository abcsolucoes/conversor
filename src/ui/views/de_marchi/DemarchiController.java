package ui.views.de_marchi;

import core.abc.*;
import core.excel.*;
import core.excel.converter.MandatoryFieldsDysrup;
import core.excel.converter.MandatoryFieldsGoogle;
import core.excel.converter.RequestItem;
import core.excel.manager.ReportManager;
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

public class DemarchiController implements Initializable
{
    private final String DYSRUP_LABEL_STRING = "Dysrup";

    private final String GOOGLE_LABEL_STRING = "Google";

    private final String REPORT_LABEL_STRING = "Relatório";

    private java.util.prefs.Preferences prefs;

    private String initialFolderPathDysrup;
    private String initialFolderPathGoogle;
    private String initialFolderPathReport;

    private CheckBoxManager checkBoxManager;

    private ArrayList<AttributeExcelRow> dysrupSheetData;
    private ArrayList<AttributeExcelRow> googleSheetData;

    private String dysrupFilePath;
    private String googleFilePath;
    private String reportFilePath;

    @FXML
    private ImageView dysrupOriginalIcon;

    @FXML
    private Label dysrupFileLabel;

    @FXML
    private ImageView dysrupTrash;

    @FXML
    private ImageView googleOriginalIcon;

    @FXML
    private Label googleFileLabel;

    @FXML
    private ImageView googleTrash;

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
        dysrupTrash.setVisible(false);
        googleTrash.setVisible(false);
        reportTrashUse.setVisible(false);

        // Loads the user preferences
        prefs = java.util.prefs.Preferences.userNodeForPackage(this.getClass());
        initialFolderPathDysrup = prefs.get(UserPreferences.DEMARCHI_DYSRUP_LAST_OPEN_FOLDER_PATH, null);
        initialFolderPathGoogle = prefs.get(UserPreferences.DEMARCHI_GOOGLE_LAST_OPEN_FOLDER_PATH, null);
        initialFolderPathReport = prefs.get(UserPreferences.DEMARCHI_REPORT_LAST_OPEN_FOLDER_PATH, null);

        dysrupSheetData = googleSheetData = null;
        updateFileButton.setDisable(true);
        googleFilePath = "";
        checkBoxManager = new CheckBoxManager(checkBoxVerticalBox);
        addListeners();
    }

    private void addListeners()
    {
        dysrupOriginalIcon.setOnMouseClicked(this::fileIconPressed);
        googleOriginalIcon.setOnMouseClicked(this::fileIconPressed);
        reportIconUse.setOnMouseClicked(this::fileIconPressed);
        updateFileButton.setOnAction(this::updateFileButtonPressed);

        dysrupTrash.setOnMouseClicked(this::dysrupTrashPressed);
        googleTrash.setOnMouseClicked(this::googleTrashPressed);
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

    private void fileIconPressed(MouseEvent event) {
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        ImageView imgView = (ImageView) event.getSource();
        File chosenFile = null;
        if (dysrupOriginalIcon.equals(imgView)) {
            chosenFile = FileBrowser.openFromSpecificFolder(
                    window, "Arquivos excel", initialFolderPathDysrup, "*.xlsx");
        } else if (googleOriginalIcon.equals(imgView)) {
            chosenFile = FileBrowser.openFromSpecificFolder(
                    window, "Arquivos excel", initialFolderPathGoogle, "*.xlsx");
        } else if (reportIconUse.equals(imgView)) {
            chosenFile = FileBrowser.openFromSpecificFolder(
                    window, "Arquivos excel", initialFolderPathReport, "*.xlsx");
        }

        if (chosenFile != null) {
            String fileName = chosenFile.getName();
            String filePath = chosenFile.getPath();
            if (imgView == dysrupOriginalIcon) {
                changeFileIcon(dysrupOriginalIcon, Paths.get("assets", "images", "excel_icon.png").toString());
                changeLabelText(dysrupFileLabel, fileName);
                dysrupFilePath = filePath;
                initialFolderPathDysrup = chosenFile.getParent();
                prefs.put(UserPreferences.DEMARCHI_DYSRUP_LAST_OPEN_FOLDER_PATH, initialFolderPathDysrup);

                dysrupTrash.setVisible(true);
            } else if (imgView == googleOriginalIcon) {
                changeFileIcon(googleOriginalIcon, Paths.get("assets", "images", "excel_icon.png").toString());
                changeLabelText(googleFileLabel, fileName);
                googleFilePath = filePath;
                initialFolderPathGoogle = chosenFile.getParent();
                prefs.put(UserPreferences.DEMARCHI_GOOGLE_LAST_OPEN_FOLDER_PATH, initialFolderPathGoogle);

                googleTrash.setVisible(true);
            } else if (reportIconUse.equals(imgView)) {
                changeFileIcon(reportIconUse, Paths.get("assets", "images", "excel_icon.png").toString());
                changeLabelText(reportFileLabel, fileName);
                reportFilePath = filePath;
                updateFileButton.setDisable(false);
                initialFolderPathReport = chosenFile.getParent();
                prefs.put(UserPreferences.DEMARCHI_REPORT_LAST_OPEN_FOLDER_PATH, initialFolderPathReport);

                reportTrashUse.setVisible(true);
            }
        }
    }

    private void dysrupTrashPressed(MouseEvent event){
        dysrupSheetData = null;
        dysrupFilePath = "";
        dysrupTrash.setVisible(false);
        changeLabelText(dysrupFileLabel, DYSRUP_LABEL_STRING);
        changeFileIcon(dysrupOriginalIcon, Paths.get("assets", "images", "upload_excel_icon.png").toString());
    }

    private void googleTrashPressed(MouseEvent event){
        googleSheetData = null;
        googleFilePath = "";
        googleTrash.setVisible(false);
        changeLabelText(googleFileLabel, GOOGLE_LABEL_STRING);
        changeFileIcon(googleOriginalIcon, Paths.get("assets", "images", "upload_excel_icon.png").toString());
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
            if (dysrupFilePath != null) {
                try {
                    dysrupSheetData = readSheetData(dysrupFilePath, MandatoryFieldsDysrup.MANDATORY_FIELDS_DYSRUP);
                } catch (Exception e) {
                    String message = String.format("Erro na leitura do arquivo Dysrup:\n%s\n\n%s", dysrupFileLabel.getText(), e.getMessage());
                    Alert.AlertType alertType = Alert.AlertType.ERROR;
                    Alert alert = new Alert(alertType, message, ButtonType.OK);
                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    alert.show();
                }
            }
        }

        updateFileButton.setDisable(false);
    }

    private ArrayList<ProductRequest> getProductRequestsDysrup() throws Exception
    {
        String errorMessage;
        CheckBox selectedCheckBox = getSelectedCheckBox();
        assert selectedCheckBox != null;
        String bandeiraId = selectedCheckBox.getId();

        String bandeira = "";
        if (bandeiraId.equals("bhCheckBox")){
            bandeira = "Supermercados BH";
        }

        if (dysrupSheetData.size() == 0)
        {
            errorMessage = "Não foi possível filtrar as linhas do arquivo pela coluna " + "\"" + MandatoryFieldsDysrup.BANDEIRA + "\"\n\n";
            errorMessage = errorMessage + "Verifique se o nome do campo está correto.";
            throw new Exception(errorMessage);
        }

        dysrupSheetData = Filter.filterEquals(MandatoryFieldsDysrup.BANDEIRA, bandeira, dysrupSheetData);
        dysrupSheetData = Filter.filterEquals(MandatoryFieldsDysrup.CLIENTE, "De Marchi", dysrupSheetData);

        if (dysrupSheetData.size() == 0)
        {
            errorMessage = "Não foi possível filtrar as linhas do arquivo pela coluna " + "\"" + MandatoryFieldsDysrup.CLIENTE + "\"\n\n";
            errorMessage = errorMessage + "Verifique se o nome da coluna está correto.\nSe o nome da coluna estiver correto, verifique se a marca \"De Marchi\" possui esse mesmo nome (os acentos podem ser desconsiderados) na coluna " + "\"" + MandatoryFieldsDysrup.CLIENTE + "\"";
            throw new Exception(errorMessage);
        }

        HashMap<String, ArrayList<AttributeExcelRow>> rowsByNumberOrder = ExcelUtils.groupByAttribute(
                MandatoryFieldsDysrup.PEDIDO, dysrupSheetData);

        ArrayList<String[]> listWithOrderNumberAndObs = new ArrayList<>();

        ArrayList<RequestItem> requestItems = new ArrayList<>();
        for (String numberOrder : rowsByNumberOrder.keySet())
        {
            ArrayList<AttributeExcelRow> rows = rowsByNumberOrder.get(numberOrder);
            boolean listFlag = false;

            for (AttributeExcelRow row : rows)
            {
                String[] dataSplit = row.getAttributeValue(MandatoryFieldsDysrup.DATA_E_HORA_PESQUISA).split(" ")[0].split("/");
                String data= dataSplit[1]+"/"+dataSplit[0]+"/"+dataSplit[2];
                RequestItem item = new RequestItem(
                        new Product(
                                row.getAttributeValue(MandatoryFieldsDysrup.NOME_PRODUTO),
                                row.getAttributeValue(MandatoryFieldsDysrup.CODIGO_PRODUTO_REDE),
                                null,
                                row.getAttributeValue(MandatoryFieldsDysrup.CLIENTE)
                        ),
                        Integer.parseInt(row.getAttributeValue(MandatoryFieldsDysrup.QUANTIDADE)),
                        new Store(row.getAttributeValue(MandatoryFieldsDysrup.PDV), null, null),
                        new Replenisher(row.getAttributeValue(MandatoryFieldsDysrup.REPOSITOR)),
                        Utils.getFormattedDateUSA(data),
                        numberOrder, null);
                requestItems.add(item);
                if (!listFlag) {
                    listWithOrderNumberAndObs.add(new String[] {row.getAttributeValue(MandatoryFieldsDysrup.OBSERVACOES), numberOrder});
                    listFlag = true;
                }
            }
        }

        HashMap<String, ArrayList<RequestItem>> requestItemsPerStore = ProductRequestHandler.separateItemsPerOrderNumber(requestItems);

        ArrayList<ProductRequest> listOfProductRequests = getListOfProductRequests(requestItemsPerStore, listWithOrderNumberAndObs);
        SortManager.sortByAlphabeticStoreName(listOfProductRequests);

        return listOfProductRequests;
    }

    public static ArrayList<ProductRequest> getListOfProductRequests(HashMap<String, ArrayList<RequestItem>> itemsPerStore, ArrayList<String[]> orderObsArray) throws Exception
    {
        ArrayList<ProductRequest> listOfProductRequests = new ArrayList<>();

        //Count for orderNumber anda Observation

        for (String orderNum : itemsPerStore.keySet())
        {
            ArrayList<RequestItem> listOfItems = itemsPerStore.get(orderNum);
            //Pegar o orderNumber e obs for store
            RequestItem requestInformation = listOfItems.get(0);
            String obs = null;
            for (int i = 0; i < orderObsArray.size();i++){
                String[] orderAndObs = orderObsArray.get(i);
                String ArrayOrderNum = orderAndObs[1];
                if (ArrayOrderNum.equals(orderNum)){
                    obs = orderAndObs[0];
                    break;
                }
            }

            //Not found obs
            if (obs == null)
                throw new Exception("Observação não encontrado, favor verificar o pedido novamente");

            listOfProductRequests.add(new ProductRequest(
                    null,
                    new Store(requestInformation.getStore().getName(), requestInformation.getStore().getCode(), null),
                    requestInformation.getReplenisher(),
                    requestInformation.getDate(),
                    listOfItems,
                    orderNum,
                    obs
            ));
        }

        return listOfProductRequests;
    }

    private void updateFileButtonPressed(ActionEvent event)
    {
        dysrupSheetData = googleSheetData = null;
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

        ArrayList<ProductRequest> dysrupRequests = null;
        if (dysrupFilePath != null && dysrupSheetData != null) {
            try {
                dysrupRequests = getProductRequestsDysrup();
            } catch (Exception e) {
                e.printStackTrace();
                Alert.AlertType alertType = Alert.AlertType.ERROR;
                Alert alert = new Alert(alertType, e.getMessage(), ButtonType.OK);
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.show();
                return;
            }
        }

        // Updating report
        Dialog<String> runningDialog = new Dialog<>();
        runningDialog.setTitle("Progresso");
        runningDialog.setContentText("Atualizando relatório ...");
        runningDialog.show();

        try {
            ReportManager.updateReport(googleSheetData, dysrupRequests, reportFilePath);
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
