package ui.views.igarape;

import core.abc.*;
import core.excel.*;
import core.excel.converter.MandatoryFieldsDysrup;
import core.excel.converter.MandatoryFieldsGoogle;
import core.excel.converter.RequestItem;
import core.excel.igarape.IgarapeReport;
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
import javafx.stage.Stage;
import ui.ui_utils.FileBrowser;
import ui.views.main_window.UserPreferences;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class IgarapeController implements Initializable
{
    private final String DYSRUP_LABEL_STRING = "Dysrup";

    private final String GOOGLE_LABEL_STRING = "Google";


    private final String REPORT_USE_LABEL_STRING = "Relatório";

    private final String REPORT_CLIENT_LABEL_STRING = "Envio";

    private java.util.prefs.Preferences prefs;

    private String initialFolderPathDysrup;
    private String initialFolderPathGoogle;
    private String initialFolderPathReportUse;
    private String initialFolderPathReportClient;

    private ArrayList<AttributeExcelRow> dysrupSheetData;
    private ArrayList<AttributeExcelRow> googleSheetData;
    private ArrayList<String> networkCodesGoogle;

    private String dysrupFilePath;
    private String googleFilePath;
    private String reportUseFilePath;
    private String reportClientFilePath;

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
    private Label reportUseFileLabel;

    @FXML
    private Label reportClientFileLabel;

    @FXML
    private ImageView reportTrashUse;

    @FXML
    private ImageView reportIconClient;

    @FXML
    private ImageView reportTrashClient;

    @FXML
    private Button updateFileButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        dysrupTrash.setVisible(false);
        googleTrash.setVisible(false);
        reportTrashUse.setVisible(false);
        reportTrashClient.setVisible(false);

        // Loads the user preferences
        prefs = java.util.prefs.Preferences.userNodeForPackage(this.getClass());
        initialFolderPathDysrup = prefs.get(UserPreferences.IGARAPE_DYSRUP_LAST_OPEN_FOLDER_PATH, null);
        initialFolderPathGoogle = prefs.get(UserPreferences.IGARAPE_GOOGLE_LAST_OPEN_FOLDER_PATH, null);
        initialFolderPathReportUse = prefs.get(UserPreferences.IGARAPE_REPORT_USE_LAST_OPEN_FOLDER_PATH, null);
        initialFolderPathReportClient = prefs.get(UserPreferences.IGARAPE_REPORT_CLIENT_LAST_OPEN_FOLDER_PATH, null);

        dysrupSheetData = googleSheetData = null;
        dysrupFilePath = googleFilePath = null;
        networkCodesGoogle = null;
        updateFileButton.setDisable(true);
        addListeners();
    }

    private void addListeners()
    {
        dysrupOriginalIcon.setOnMouseClicked(this::fileIconPressed);
        googleOriginalIcon.setOnMouseClicked(this::fileIconPressed);
        reportIconUse.setOnMouseClicked(this::fileIconPressed);
        reportIconClient.setOnMouseClicked(this::fileIconPressed);
        updateFileButton.setOnAction(this::updateFileButtonPressed);

        dysrupTrash.setOnMouseClicked(this::dysrupTrashPressed);
        googleTrash.setOnMouseClicked(this::googleTrashPressed);
        reportTrashUse.setOnMouseClicked(this::reportUseTrashPressed);
        reportTrashClient.setOnMouseClicked(this::reportClientTrashPressed);
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
        if (dysrupOriginalIcon.equals(imgView)) {
            chosenFile = FileBrowser.openFromSpecificFolder(
                    window, "Arquivos excel", initialFolderPathDysrup, "*.xlsx");
        } else if (googleOriginalIcon.equals(imgView)) {
            chosenFile = FileBrowser.openFromSpecificFolder(
                    window, "Arquivos excel", initialFolderPathGoogle, "*.xlsx");
        } else if (reportIconUse.equals(imgView)) {
            chosenFile = FileBrowser.openFromSpecificFolder(
                    window, "Arquivos excel", initialFolderPathReportUse, "*.xlsx");
        } else if (reportIconClient.equals(imgView)) {
            chosenFile = FileBrowser.openFromSpecificFolder(
                    window, "Arquivos excel", initialFolderPathReportClient, "*.xlsx");
        }

        if (chosenFile != null) {
            String fileName = chosenFile.getName();
            String filePath = chosenFile.getPath();

            Path of = Path.of("assets", "images", "excel_icon.png");
            if (imgView == dysrupOriginalIcon) {
                changeFileIcon(dysrupOriginalIcon, of.toString());
                changeLabelText(dysrupFileLabel, fileName);
                dysrupFilePath = filePath;
                initialFolderPathDysrup = chosenFile.getParent();
                prefs.put(UserPreferences.IGARAPE_DYSRUP_LAST_OPEN_FOLDER_PATH, initialFolderPathDysrup);

                dysrupTrash.setVisible(true);
            } else if (imgView == googleOriginalIcon) {
                changeFileIcon(googleOriginalIcon, of.toString());
                changeLabelText(googleFileLabel, fileName);
                googleFilePath = filePath;
                initialFolderPathGoogle = chosenFile.getParent();
                prefs.put(UserPreferences.IGARAPE_GOOGLE_LAST_OPEN_FOLDER_PATH, initialFolderPathGoogle);

                googleTrash.setVisible(true);
            } else if (reportIconUse.equals(imgView)) {
                changeFileIcon(reportIconUse, of.toString());
                changeLabelText(reportUseFileLabel, fileName);
                reportUseFilePath = filePath;
                initialFolderPathReportUse = chosenFile.getParent();
                prefs.put(UserPreferences.IGARAPE_REPORT_USE_LAST_OPEN_FOLDER_PATH, initialFolderPathReportUse);

                reportTrashUse.setVisible(true);
            } else if (reportIconClient.equals(imgView)) {
                changeFileIcon(reportIconClient, of.toString());
                changeLabelText(reportClientFileLabel, fileName);
                reportClientFilePath = filePath;
                initialFolderPathReportClient = chosenFile.getParent();
                prefs.put(UserPreferences.IGARAPE_REPORT_CLIENT_LAST_OPEN_FOLDER_PATH, initialFolderPathReportClient);

                reportTrashClient.setVisible(true);
            }

            verifyUpdateButton();
        }
    }

    //function to check if button has to be enable or disable
    private void verifyUpdateButton() {
        updateFileButton.setDisable((dysrupFilePath == null && googleFilePath == null) ||
                (reportUseFilePath == null && reportClientFilePath == null));
    }

    private void dysrupTrashPressed(MouseEvent event)
    {
        dysrupSheetData = null;
        dysrupFilePath = null;
        dysrupTrash.setVisible(false);
        changeLabelText(dysrupFileLabel, DYSRUP_LABEL_STRING);
        changeFileIcon(dysrupOriginalIcon, Paths.get("assets", "images", "upload_excel_icon.png").toString());
        verifyUpdateButton();
    }

    private void googleTrashPressed(MouseEvent event)
    {
        googleSheetData = null;
        googleFilePath = null;
        googleTrash.setVisible(false);
        changeLabelText(googleFileLabel, GOOGLE_LABEL_STRING);
        changeFileIcon(googleOriginalIcon, Paths.get("assets", "images", "upload_excel_icon.png").toString());
        verifyUpdateButton();
    }

    private void reportUseTrashPressed(MouseEvent event){
        reportUseFilePath = null;
        reportTrashUse.setVisible(false);
        changeLabelText(reportUseFileLabel, REPORT_USE_LABEL_STRING);
        changeFileIcon(reportIconUse, Paths.get("assets", "images", "upload_excel_icon.png").toString());
        verifyUpdateButton();
    }

    private void reportClientTrashPressed(MouseEvent event){
        reportClientFilePath = null;
        reportTrashClient.setVisible(false);
        changeLabelText(reportClientFileLabel, REPORT_CLIENT_LABEL_STRING);
        changeFileIcon(reportIconClient, Paths.get("assets", "images", "upload_excel_icon.png").toString());
        verifyUpdateButton();
    }

    private void readFiles()
    {
        // Updates the interface
        boolean readFileGoogle = true;
        if (googleFilePath != null) {
            try {
                googleSheetData = readSheetData(googleFilePath, MandatoryFieldsGoogle.MANDATORY_FIELDS_GOOGLE);
                networkCodesGoogle = getNetworkCodesFromOrder(googleFilePath, MandatoryFieldsGoogle.MANDATORY_FIELDS_GOOGLE);
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
    }

    private ArrayList<ProductRequest> getProductRequestsDysrup() throws Exception
    {
        String errorMessage;
        if (dysrupSheetData.size() == 0)
        {
            errorMessage = "Não foi possível filtrar as linhas do arquivo pela coluna " + "\"" + MandatoryFieldsDysrup.BANDEIRA + "\"\n\n";
            errorMessage = errorMessage + "Verifique se o nome do campo está correto.";
            throw new Exception(errorMessage);
        }

        dysrupSheetData = Filter.filterEquals(MandatoryFieldsDysrup.CLIENTE, "Igarape", dysrupSheetData);

        if (dysrupSheetData.size() == 0)
        {
            errorMessage = "Não foi possível filtrar as linhas do arquivo pela coluna " + "\"" + MandatoryFieldsDysrup.CLIENTE + "\"\n\n";
            errorMessage = errorMessage + "Verifique se o nome da coluna está correto.\nSe o nome da coluna estiver correto, verifique se a marca \"IGARAPÉ\" possui esse mesmo nome (os acentos podem ser desconsiderados) na coluna " + "\"" + MandatoryFieldsDysrup.CLIENTE + "\"";
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

    private void updateFileButtonPressed(ActionEvent event) {
        dysrupSheetData = googleSheetData = null;

        //Verifica qual arquivo está selecionado e já insere nas variáveis
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

        Dialog<String> runningDialog = new Dialog<>();
        runningDialog.setTitle("Progresso");
        runningDialog.setContentText("Atualizando relatório ...");
        runningDialog.show();

        try {
            //Update report who has to be updateded
            if (reportUseFilePath != null) {
                IgarapeReport.updateReportUse(googleSheetData, dysrupRequests, reportUseFilePath,networkCodesGoogle);
            }
            if (reportClientFilePath != null) {
                IgarapeReport.updateReportClient(googleSheetData, dysrupRequests, reportClientFilePath);
            }
        } catch (org.apache.poi.EmptyFileException erro) {
            String message = "Arquivo se encontra vazio\n\nCaminho: ";
            if (reportUseFilePath != null)
                message += reportUseFilePath+"\n\n";
            else if (reportClientFilePath != null)
                message += reportClientFilePath;

            Alert.AlertType alertType = Alert.AlertType.ERROR;
            Alert alert = new Alert(alertType, message, ButtonType.OK);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.show();
            runningDialog.setResult("");
            runningDialog.close();
            return;
        } catch (Exception e) {
            e.printStackTrace();
            String message = String.format("Erro na atualização do relatório:\n\n%s\n\n", e.getMessage());

            if (reportUseFilePath != null)
                message += reportUseFilePath;
            else if (reportClientFilePath != null)
                message += reportClientFilePath;

            Alert.AlertType alertType = Alert.AlertType.ERROR;
            Alert alert = new Alert(alertType, message, ButtonType.OK);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.show();
            runningDialog.setResult("");
            runningDialog.close();
            return;
        }

        runningDialog.setResult("");
        runningDialog.close();

        String message = String.format("%s\n\n%s\n\n", "Relatório atualizado com sucesso!",
                "O relatório atualizado se encontra no local abaixo.");
        try {

            if (reportUseFilePath != null) {
                IgarapeReport.updateFile(reportUseFilePath, "use");
                message += reportUseFilePath;
            }
            if (reportClientFilePath != null) {
                IgarapeReport.updateFile(reportClientFilePath, "client");
                message += reportClientFilePath;
            }

        } catch (Exception erro) {
            message = erro.getMessage()+"\n\nErro ao tentar atualizar o arquivo, verifique se o arquivo correto foi inserido";
        }
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

    private ArrayList<String> getNetworkCodesFromOrder(String filePath, ArrayList<String> mandatoryFields)throws Exception
    {
        ExcelReader excelReader = null;
        ArrayList<String> sheetData;
        Path path = Paths.get(filePath);

        try
        {
            excelReader = new ExcelReader(path.toString(), mandatoryFields);
            sheetData = excelReader.readNetworkCodes();
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
}
