package core.excel.igarape;

import core.abc.Product;
import core.abc.ProductRequest;
import core.excel.AttributeExcelRow;
import core.excel.ExcelUtils;
import core.excel.SortManager;
import core.excel.converter.MandatoryFieldsGoogle;
import core.excel.converter.RequestItem;
import core.utils.Utils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class IgarapeReport
{
    // Position where the store name starts
    private static final int STORE_NAME_POSITION_FROM_INPUT = 3;

    // First row with a product request on the report file
    private static final int FIRST_ROW = 4;

    // The amount that will subtract from the report file to get the last product request
    private static final int SHIFT_ROWS = 2;

    // Position where the row number starts
    private static final int ROW_NUMBER_POSITION = 2;

    // Position where the cell date is
    private static final int DATE_CELL = 4;

    // Position where the order num is
    private static final int ORDER_NUM_CELL = 3;

    private static final String PRODUTO = Utils.normalize("PRODUTO");

    private static final String OBS = Utils.normalize("Obs:");

    private static Workbook workbookUse;
    private static Workbook workbookClient;
    private static FileInputStream inputStream;
    private static FileOutputStream outputStream;

    public static void updateReportUse(
            ArrayList<AttributeExcelRow> excelRowsGoogle, ArrayList<ProductRequest> dysrupRequests, String reportFilePath, ArrayList<String> networkCodesGoogleOrder)
            throws Exception
    {
        inputStream = new FileInputStream(reportFilePath);
        workbookUse = WorkbookFactory.create(inputStream);

        if (excelRowsGoogle != null) {
            if (excelRowsGoogle.size() > 0) {
                updateReportFromGoogleUse(excelRowsGoogle, networkCodesGoogleOrder);
            }
            else {
                String errorMessage = "Nenhum pedido solicitado no arquivo do Google.";
                throw new Exception(errorMessage);
            }
        }
        if(dysrupRequests != null)
        {
            if(dysrupRequests.size() > 0)
            {
                updateReportFromDysrupUse(dysrupRequests);
            }
            else
            {
                String errorMessage = "Nenhum pedido solicitado no arquivo da Dysrup.";
                throw new Exception(errorMessage);
            }
        }
    }

    public static void updateReportClient(ArrayList<AttributeExcelRow> excelRowsGoogle, ArrayList<ProductRequest> dysrupRequests, String reportFilePath)
            throws Exception {

        ArrayList<ClientBlockCells> allStores = new ArrayList<>();
        inputStream = new FileInputStream(reportFilePath);
        workbookClient = WorkbookFactory.create(inputStream);

        if (excelRowsGoogle != null) {
            if (excelRowsGoogle.size() > 0)
                allStores.addAll(updateReportFromGoogleClient(excelRowsGoogle));
            else {
                String errorMessage = "Nenhum pedido solicitado no arquivo do Google.";
                throw new Exception(errorMessage);
            }
        }
        if (dysrupRequests != null) {
            if (dysrupRequests.size() > 0)
                allStores.addAll(updateReportFromDysrupClient(dysrupRequests));
            else {
                String errorMessage = "Nenhum pedido solicitado no arquivo do GIV.";
                throw new Exception(errorMessage);
            }
        }
        if (excelRowsGoogle != null || dysrupRequests != null) {
            SortManager.sortClientBlockCellsArraylistByStoreNumber(allStores);
            int[] coordinates = new int[2];
            for (ClientBlockCells store : allStores) {
                coordinates = findEmptyStore(store, coordinates);
            }
        }
        else
            throw new Exception("O Arquivo pedido não possui nenhum pedido e/ou loja, favor verificar o arquivo inserido");
        inputStream.close();
    }

    private static void updateReportFromGoogleUse(ArrayList<AttributeExcelRow> excelRowsGoogle, ArrayList<String> networkCodesGoogleOrder) throws Exception
    {
        Sheet sheet = workbookUse.getSheetAt(0);

        ArrayList<String> networkCodeFromSheet = getnetworkCodeFromSheet(sheet.getRow(3));

        for (AttributeExcelRow googleRow : excelRowsGoogle) {
            //Caso a loja esteja vazia
            if (googleRow.getAttributeValue(MandatoryFieldsGoogle.LOJA).equals(""))
                continue;

            String storeNameGoogle = googleRow.getAttributeValue(MandatoryFieldsGoogle.LOJA).trim();

            boolean foundStore = false;

            try {
                for (int i = FIRST_ROW; i < sheet.getPhysicalNumberOfRows() - SHIFT_ROWS; i++) {
                    Row row = sheet.getRow(i);

                    if (ExcelUtils.isRowEmpty((XSSFRow) row))
                        continue;

                    String storeNameReport = Utils.normalize(ExcelUtils.getStringCellValue((XSSFCell) row.getCell(0)).trim());

                    final String storeNameGoogleReplace = Utils.normalize(storeNameGoogle.toString().replace("  ", " "));

                    if (storeNameGoogleReplace.equalsIgnoreCase(storeNameReport)) {
                        foundStore = true;

                        ArrayList<RequestItem> cells = getCells(googleRow);
                        if (ExcelUtils.getStringCellValue((XSSFCell) row.getCell(DATE_CELL)).trim().isEmpty()) {
                            updateStoreRow(cells, (XSSFRow) row, networkCodeFromSheet, networkCodesGoogleOrder);
                        } else {
                            // Is there another product request?
                            for (int index = i; index < sheet.getPhysicalNumberOfRows() - SHIFT_ROWS; index++) {
                                Row rowAux = sheet.getRow(index);

                                storeNameReport = ExcelUtils.getStringCellValue((XSSFCell) row.getCell(1)).trim();

                                if (Objects.equals(storeNameGoogleReplace,storeNameReport.toLowerCase())) {
                                    row = rowAux;
                                }
                                else
                                    break;
                            }
                            insertStoreRow(sheet, cells, row.getRowNum(), networkCodeFromSheet, networkCodesGoogleOrder);
                        }
                        break;
                    }
                }
            } catch (NullPointerException erro) {
                String message = "\n\nColuna não encontrada, verifique se você inseriu os arquivos corretos";
                throw new Exception(erro.getMessage()+message);
            }

            if (!foundStore)
            {
                String errorMessage = String.format("A loja %s não foi encontrada!", googleRow.getAttributeValue(MandatoryFieldsGoogle.LOJA));
                throw new Exception(errorMessage);
            }
        }
    }

    private static ArrayList<ClientBlockCells> updateReportFromGoogleClient(ArrayList<AttributeExcelRow> excelRowsGoogle) throws Exception {

        ArrayList<ClientBlockCells> storeInput = new ArrayList<>();
        Sheet sheet = workbookClient.getSheetAt(0);

        for (AttributeExcelRow googleRow : excelRowsGoogle) {

            //Caso a loja esteja vazia
            if (googleRow.getAttributeValue(MandatoryFieldsGoogle.LOJA).equals(""))
                continue;

            ArrayList<RequestItem> items = getCells(googleRow);

            if (verifyIfOnly20L(items)) {
                continue;
            }

            String[] storeNameList = googleRow.getAttributeValue(
                    MandatoryFieldsGoogle.LOJA).trim().split(" ");

            //Pegar nome da loja
            StringBuilder storeNameGoogle = new StringBuilder();
            for (int index = STORE_NAME_POSITION_FROM_INPUT; index < storeNameList.length; index++) {
                storeNameGoogle.append(storeNameList[index]).append(" ");
            }

            String[] importantList = new String[6];

            importantList[0] = storeNameList[1];
            importantList[1] = storeNameGoogle.toString().trim();
            importantList[2] = googleRow.getAttributeValue("N° do Pedido");
            importantList[3] = googleRow.getAttributeValue("Observações");

            storeInput.add(new ClientBlockCells(sheet, importantList, items));
        }
        return storeInput;
    }

    private static void updateReportFromDysrupUse(ArrayList<ProductRequest> requestsGiv) throws Exception
    {
        Sheet sheet = workbookUse.getSheetAt(0);

        ArrayList<String> networkCodeFromSheet = getnetworkCodeFromSheet(sheet.getRow(3));

        for (ProductRequest request : requestsGiv)
        {
            String storeNameDysrup = request.getStore().getName()
                    .trim().replace("  ", " ");

            boolean foundStore = false;

            for (int i = FIRST_ROW; i < sheet.getPhysicalNumberOfRows() - SHIFT_ROWS; i++)
            {
                Row row = sheet.getRow(i);

                if (ExcelUtils.isRowEmpty((XSSFRow) row))
                {
                    continue;
                }

                String storeNameReport = Utils.normalize(ExcelUtils.getStringCellValue((XSSFCell) row.getCell(0)).trim());

                if (storeNameDysrup.equalsIgnoreCase(storeNameReport))
                {
                    foundStore = true;
                    if (ExcelUtils.getStringCellValue((XSSFCell) row.getCell(DATE_CELL)).trim().isEmpty())
                    {
                        // null parameter because networkCode is just google
                        updateStoreRow(request.getRequestItems(), (XSSFRow) row, networkCodeFromSheet, null);
                    }
                    else
                    {
                        // Is there another product request?
                        for (int index = i; index < sheet.getPhysicalNumberOfRows() - SHIFT_ROWS; index++){
                            Row rowAux = sheet.getRow(index);

                            storeNameReport = ExcelUtils.getStringCellValue((XSSFCell) row.getCell(0)).trim();

                            if (storeNameDysrup.equals(storeNameReport))
                            {
                                row = rowAux;
                            }else {
                                break;
                            }
                        }
                        // null parameter because networkCode is just google
                        insertStoreRow(sheet, request.getRequestItems(), row.getRowNum(), networkCodeFromSheet, null);
                    }
                    break;
                }
            }

            if (!foundStore)
            {
                String errorMessage = String.format("A loja %s não foi encontrada!\nDICA:Verifique se o nome da loja contem \" - \" caso sim substitua por \" / \"", request.getStore().getName());
                throw new Exception(errorMessage);
            }

        }

        inputStream.close();
    }

    private static ArrayList<ClientBlockCells> updateReportFromDysrupClient(ArrayList<ProductRequest> dysrupRequests) throws Exception {

        Sheet sheet = workbookClient.getSheetAt(0);
        ArrayList<ClientBlockCells> storeInput = new ArrayList<>();

        for (ProductRequest productRequest : dysrupRequests) {

            String dysrupStoreName = Utils.normalize(productRequest.getStore().getName().trim());

            //Caso a loja esteja vazia
            if (dysrupStoreName.equals(""))
                continue;

            ArrayList<RequestItem> items = productRequest.getRequestItems();

            if (verifyIfOnly20L(items)){
                continue;
            }

            String[] tempString = productRequest.getStore().getName().split(" ");

            String[] importList = new String[6];
            importList[0] = tempString[1];
            importList[1] = dysrupStoreName;
            importList[2] = productRequest.getOrderNum();
            importList[3] = productRequest.getComments();

            storeInput.add(new ClientBlockCells(sheet, importList, items));
        }
        inputStream.close();
        return storeInput;
    }

    private static int[] findEmptyStore(ClientBlockCells store, int[] coordinates) throws Exception {
        Sheet sheet = store.getSheet();
        int i = coordinates[0];
        int collumPointer = coordinates[1];

        //Andar por todo o relatorio
        for(;i < sheet.getPhysicalNumberOfRows() - 1;i++) {

            XSSFRow row = (XSSFRow) sheet.getRow(i);
            XSSFCell cell = row.getCell(collumPointer);
            int tamRow = row.getLastCellNum();

            if (!Utils.normalize(ExcelUtils.getStringCellValue(cell)).equalsIgnoreCase(PRODUTO))
                continue;

            //Andar na horizontal procurando por produtos
            for (;collumPointer < tamRow; cell = row.getCell(++collumPointer)) {

                if (!Utils.normalize(ExcelUtils.getStringCellValue(cell)).equalsIgnoreCase(PRODUTO))
                    continue;
                else {
                    XSSFCell celltemp = (XSSFCell) sheet.getRow(i - 2).getCell(collumPointer);
                    String cellString = Utils.normalize(celltemp.getStringCellValue()).trim();
                    if (!cellString.equals(Utils.normalize("LOJA:")) && !cellString.equals("") && !cellString.equals(Utils.normalize("LOJA: Nº")))
                        continue;
                }

                //Quando achar escrever e sair da função
                insertReportClient(store, i, collumPointer);
                return new int[]{i, collumPointer};
            }
            collumPointer = 0;
        }
        //Caso chegue aqui significa que não foi encontrado um espaço para escrever no arquivo
        throw new Exception("O número de lojas e de espaços no relatório não compatíveis.\nDICA:Adicionar novos espaços no relatório");
    }

    private static void insertReportClient(ClientBlockCells store, int i, int j) {
        Sheet sheet = store.getSheet();

        //Preencher Loja
        XSSFRow row = (XSSFRow) sheet.getRow(i - 2);
        XSSFCell cell = row.getCell(j);
        String cellValue = "Loja: N° " + store.getStoreNumber() + " - " + store.getStoreName();
        cell.setCellValue(cellValue);

        //Preencher numero do pedido
        row = (XSSFRow) sheet.getRow(i - 1);
        cell = row.getCell(j);
        String nPedido = "";
        if (store.getOrderNumber() != null)
            nPedido = store.getOrderNumber();
        cellValue = "N° DO PEDIDO:" + nPedido;
        cell.setCellValue(cellValue);

        int x = 1;

        row = (XSSFRow) sheet.getRow(i + x);
        cell = row.getCell(j);

        //Ir de pedido em pedido preenchendo a quantidade
        while (!Utils.normalize(cell.getStringCellValue()).startsWith(OBS)) {
            //Ir em ordem entre todos os produtos
            RequestItem product = store.getItems().get(x-1);

            String[] productSplit = product.getProduct().getName().split(" - ");
            String productName;

            //Caso venha do google ou do giv, a posição do vetor se altera
            if (productSplit.length > 2)
                productName = Utils.normalize(productSplit[2]);
            else
                productName = Utils.normalize(productSplit[0]);

            //Verifica se o produto tem valor vazio
            if (!productName.equals(""))
                row.getCell(j + 2).setCellValue(product.getVolume());

            row = (XSSFRow) sheet.getRow(i + ++x);
            cell = row.getCell(j);
        }

        //Setar obs
        String obs = "";
        if (store.getStoreObs() != null)
            obs = store.getStoreObs();
        cell.setCellValue(OBS+obs);
    }

    private static boolean verifyIfOnly20L(ArrayList<RequestItem> items){
        //Verify if is only 20L items
        boolean only20L = true;
        final int indexAgua20L = 2;
        final int indexAgua20LV = 10;
        int i = 0;
        RequestItem item;
        do {
            item = items.get(i);
            if (i != indexAgua20L && i != indexAgua20LV) {
                if (item.getVolume() != 0) {
                    only20L = false;
                    break;
                }
            }
            i++;
        } while(i < items.size());
        return only20L;
    }

    public static void updateFile (String reportFile, String type) throws Exception {
        outputStream = new FileOutputStream(reportFile);
        if (type.equals("use")) {
            workbookUse.getCreationHelper().createFormulaEvaluator().evaluateAll();
            workbookUse.write(outputStream);
            outputStream.close();
            workbookUse.close();
        } else if (type.equals("client")) {
            workbookClient.getCreationHelper().createFormulaEvaluator().evaluateAll();
            workbookClient.write(outputStream);
            outputStream.close();
            workbookClient.close();
        }
    }
    private static void updateStoreRow (ArrayList<RequestItem> cells, XSSFRow row, ArrayList<String> networkCodeFromSheet,ArrayList<String> networkCodesGoogleOrder) throws Exception {

        SimpleDateFormat datetemp = new SimpleDateFormat("dd/MM/yyyy");
        Date cellValue = datetemp.parse(cells.get(0).getDate());
        row.getCell(DATE_CELL).setCellValue(cellValue);
        row.getCell(ORDER_NUM_CELL).setCellValue(cells.get(0).getOrderNum());

        int cellNum = 5;
        int networkCodeGoogleIndex = 0;
        //When networkCodesGoogleOrder=null, updateStoreRow called by updateReportDysrup
        if(networkCodesGoogleOrder == null)
        {
            try {
                for (RequestItem request : cells) {
                    if (networkCodeFromSheet.contains(request.getProduct().getNetworkCode())) {
                        row.getCell(cellNum).setCellValue(request.getVolume());
                        cellNum++;
                    }
                }
            }catch (NullPointerException e){
                throw new Exception("Entrada do Dysrup com o número inválido de produtos.");
            }
            catch (Exception ex) {
                throw new Exception(ex.getMessage());
            }
        }else
        {
            try {
                for (RequestItem request : cells) {
                    if(networkCodeFromSheet.contains(networkCodesGoogleOrder.get(networkCodeGoogleIndex++)) /* || networkCodesGoogleOrder == null*/)
                    {
                        row.getCell(cellNum).setCellValue(request.getVolume());
                        cellNum++;
                    }
                }
            }catch (NullPointerException e){
                throw new Exception("Entrada do GOOGLE com o número inválido de produtos.");
            }
            catch (Exception ex) {
                throw new Exception(ex.getMessage());
            }
        }

    }

    private static void insertStoreRow(Sheet worksheet, ArrayList<RequestItem> cells, int sourceRowNum, ArrayList<String> networkCodeFromSheet,ArrayList<String> networkCodesGoogleOrder)
            throws Exception
    {
        int destinationRowNum = sourceRowNum + 1;

        // Get the source / new row
        Row newRow = worksheet.getRow(destinationRowNum);
        Row sourceRow = worksheet.getRow(sourceRowNum);

        // If the row exist in destination, push down all rows by 1 else create a new row
        if (newRow != null)
        {
            try {
                worksheet.shiftRows(destinationRowNum, worksheet.getLastRowNum(), 1);
            } catch (IllegalArgumentException erro) {
                erro.printStackTrace();
                throw new Exception("Erro ao converter. Parece que existem algumas linhas invisíveis no arquivo."+
                        "\n\n"+"DICA: Copiar e colar as linhas para uma nova aba, mantendo as formatações.");
            }
        }
        newRow = worksheet.createRow(destinationRowNum);

        newRow.setHeight(sourceRow.getHeight());

        // Loop through source columns to add to new row
        for (int i = 0; i < sourceRow.getLastCellNum(); i++)
        {
            // Grab a copy of the old/new cell
            Cell oldCell = sourceRow.getCell(i);
            Cell newCell = newRow.createCell(i);

            // If the old cell is null jump to next cell
            if (oldCell == null)
            {
                continue;
            }

            // Use old cell style
            newCell.setCellStyle(oldCell.getCellStyle());

            // "Numero do pedido" can't be duplicate
            if (i != 3) {
                // Set the cell data value
                switch (oldCell.getCellType().getCode()) {
                    case 0:
                        newCell.setCellValue(oldCell.getNumericCellValue());
                        break;
                    case 1:
                        newCell.setCellValue(oldCell.getRichStringCellValue());
                        break;
                    case 2:
                        newCell.setCellFormula(setFormula(oldCell.getCellFormula()));
                        break;
                    case 4:
                        newCell.setCellValue(oldCell.getBooleanCellValue());
                        break;
                    case 5:
                        newCell.setCellErrorValue(oldCell.getErrorCellValue());
                        break;
                    default:
                        break;
                }
            }
        }

        SimpleDateFormat datetemp = new SimpleDateFormat("dd/MM/yyyy");
        Date cellValue = datetemp.parse(cells.get(0).getDate());

        newRow.getCell(DATE_CELL).setCellValue(cellValue);
        newRow.getCell(ORDER_NUM_CELL).setCellValue(cells.get(0).getOrderNum());

        int cellNum = 5;
        int networkCodeGoogleIndex = 0;

        //When networkCodesGoogleOrder=null, updateStoreRow called by updateReportDysrup
        if(networkCodesGoogleOrder == null)
        {
            try {
                for (RequestItem request : cells) {
                    if (networkCodeFromSheet.contains(request.getProduct().getNetworkCode())) {
                        newRow.getCell(cellNum).setCellValue(request.getVolume());
                        cellNum++;
                    }
                }
            }catch (NullPointerException e){
                throw new Exception("Entrada do GOOGLE com o número inválido de produtos.");
            }
            catch (Exception ex) {
                throw new Exception(ex.getMessage());
            }
        }else
        {
            try {
                for (RequestItem request : cells) {
                    if(networkCodeFromSheet.contains(networkCodesGoogleOrder.get(networkCodeGoogleIndex++)) /* || networkCodesGoogleOrder == null*/)
                    {
                        newRow.getCell(cellNum).setCellValue(request.getVolume());
                        cellNum++;
                    }
                }
            }catch (NullPointerException e){
                throw new Exception("Entrada do GOOGLE com o número inválido de produtos.");
            }
            catch (Exception ex) {
                throw new Exception(ex.getMessage());
            }
        }
    }

    private static ArrayList<RequestItem> getCells (AttributeExcelRow googleRow)
            throws Exception
    {
        ArrayList<RequestItem> cells = new ArrayList<>();

        try {

            for (String attributeName : googleRow.getRow().keySet()) {
                if (!attributeName.equals("") &&
                        !MandatoryFieldsGoogle.MANDATORY_FIELDS_GOOGLE.contains(attributeName)) {
                    cells.add(new RequestItem(new Product(attributeName, "0", null, null),
                            googleRow.getRow().get(attributeName).equals("") ? 0
                                    : Integer.parseInt(googleRow.getRow().get(attributeName)),
                            null, null,
                            Utils.getFormattedDateUSA(googleRow.getRow().get(MandatoryFieldsGoogle.CARIMBO).split(" ")[0]),
                            googleRow.getRow().get(MandatoryFieldsGoogle.PEDIDO).trim(), null));
                }
            }
            SortManager.sortItemsById(cells);

        } catch (IndexOutOfBoundsException erro) {
            erro.printStackTrace();
            throw new Exception("Por favor, verifique os campos dos pedidos do Google");
        }
        return (cells);
    }

    private static String setFormula(String oldString){
        StringBuilder newString = new StringBuilder();
        String[] oldStringList = oldString.split("\\+");
        for(String term : oldStringList)
        {
            String[] termList = term.split("\\*");

            String cell = termList[0];
            StringBuilder row = new StringBuilder();

            for(int index = ROW_NUMBER_POSITION; index < cell.length(); index++)
            {
                row.append(cell.charAt(index));
            }

            int rowNumber = Integer.parseInt(row.toString()) + 1;

            newString.append(cell, 0, ROW_NUMBER_POSITION).append(rowNumber).append("*").append(termList[1]).append("+");
        }
        newString = new StringBuilder(newString.substring(0, newString.length() - 1));
        return newString.toString();
    }

    private static ArrayList<String> getnetworkCodeFromSheet(Row row)
    {
        ArrayList<String> ordersNumReport = new ArrayList<>();
        for (int i = 5; i < row.getLastCellNum() - 1; i++)
        {
            ordersNumReport.add(ExcelUtils.getStringCellValue((XSSFCell) row.getCell(i)));
        }

        return ordersNumReport;
    }
}
