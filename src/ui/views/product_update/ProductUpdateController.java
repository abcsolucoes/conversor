package ui.views.product_update;

import core.excel.CellStyles;
import core.excel.ExcelRow;
import core.excel.ExcelUtils;
import core.excel.ExcelWriter;
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
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import ui.ui_utils.DirectoryBrowser;
import ui.ui_utils.FileBrowser;
import ui.views.main_window.UserPreferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static core.excel.products.ProductUpdate.updateProducts;
import static core.system.SystemUtils.deleteFile;
import static core.system.SystemUtils.fileExists;


public class ProductUpdateController implements Initializable
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
    private Button updateProductsButton;

    @FXML
    private Button chooseDestinationFolderButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        trashIcon.setVisible(false);

        // Loads the user preferences
        prefs = java.util.prefs.Preferences.userNodeForPackage(this.getClass());
        initialFolderPath = prefs.get(UserPreferences.PRODUCTS_LAST_OPEN_FOLDER_PATH, null);

        chooseDestinationFolderButton.setTooltip(new Tooltip("Nenhuma pasta destino selecionada"));

        updateProductsButton.setDisable(true);

        addListeners();
    }

    private void addListeners(){
        updateProductsButton.setOnAction(this::updateProductsPressed);
        chooseDestinationFolderButton.setOnAction(this::chooseDestinationFolderButtonPressed);

        fileIcon.setOnMouseClicked(this::fileIconPressed);

        trashIcon.setOnMouseClicked(this::trashIconPressed);
    }

    // ICON

    private void fileIconPressed(MouseEvent event)
    {
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        List<File> chosenFiles = FileBrowser.openMultipleFilesFromSpecificFolder
                (window, "Arquivos excel", initialFolderPath, "*.xls", "*.xlsx");
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

            updateProductsButton.setDisable(false);

            trashIcon.setVisible(true);
        }
    }

    private void trashIconPressed(MouseEvent event)
    {
        trashIcon.setVisible(false);
        String LABEL_STRING = "Escolha um ou mais arquivos";
        changeLabelText(LABEL_STRING);
        changeFileIcon(Paths.get("assets", "images", "upload_excel_icon.png").toString());
        updateProductsButton.setDisable(true);
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

    // FILE

    private void updateProductsPressed(ActionEvent event) {
        destinationFolderPath = chooseDestinationFolderButton.getText();
        if (destinationFolderPath == null) {
            String message = "Você deve escolher uma pasta destino!";
            Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
            alert.show();
            return;
        }

        // Updates the interface
        this.updateProductsButton.setDisable(true);

        Dialog<String> runningDialog = new Dialog<>();
        runningDialog.setTitle("Progresso");
        runningDialog.setContentText("Lendo o(s) arquivo(s) de produtos ...");
        runningDialog.show();

        String finalPath = generateFilePath(destinationFolderPath);

        boolean writerCreated = true;
        ExcelWriter writer = null;
        try
        {
            writer = new ExcelWriter(finalPath, "Produtos");
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.show();
            this.updateProductsButton.setDisable(false);
            writerCreated = false;
        }

        if (!writerCreated)
        {
            return;
        }

        writer.append(
                writer.cellStyles.get(CellStyles.BOLD),
                "Código do fornecedor", "Nome do fornecedor", "Código do Item", "Nome do Item", "SKU"
        );

        boolean everythingOk = true;
        String[] filePaths = this.filenameLabel.getText().split("\n");
        for (String path : filePaths)
        {
            try
            {
                ArrayList<ExcelRow> sheetData = readSheetData(path);
                updateProducts(writer, sheetData);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.show();
                this.updateProductsButton.setDisable(false);
                everythingOk = false;
            }
        }

        runningDialog.setResult("");
        runningDialog.close();

        if (!everythingOk)
        {
            try
            {
                writer.saveFile();
                writer.closeFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            if (fileExists(finalPath))
            {
                try
                {
                    deleteFile(finalPath);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            return;
        }

        this.updateProductsButton.setDisable(false);

        ExcelUtils.autoAdjustColumns(writer.getCurrentSheet());
        String message;
        try
        {
            writer.saveFile();
            writer.closeFile();
            message = String.format("%s\n\n%s\n\n%s", "Arquivo(s) lidos(s) com sucesso!",
                    "O novo arquivo com os dados dos produtos se encontra no local abaixo.", destinationFolderPath);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            message = "Algo deu errado ao salvar o arquivo gerado: " + e.getMessage();
        }

        Alert.AlertType alertType = Alert.AlertType.INFORMATION;
        Alert alert = new Alert(alertType, message, ButtonType.OK);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.show();
    }

    private ArrayList<ExcelRow> readSheetData(String filePath) throws Exception
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
                    String cellValue = ExcelUtils.getStringCellValue((HSSFCell) row.getCell(j));
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
            throw new Exception("Algo deu errado ao ler o arquivo: " + e.getMessage());
        }

        return sheetData;
    }

    private String generateFilePath(String saveDirectoryPath)
    {
        String[] tmp = "LISTAGEM_DE_PRODUTOS.xlsx".split("\\.");
        String name = tmp[0];
        String extension = tmp[1];
        String dateString = Utils.getCurrentDateString();
        String finalName = String.format("%s at %s.%s", name, dateString, extension);
        return Paths.get(saveDirectoryPath, finalName).toString();
    }
}