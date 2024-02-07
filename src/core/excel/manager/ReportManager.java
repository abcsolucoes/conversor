package core.excel.manager;

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

public class ReportManager {
    // Position where the store name starts
    private static final int STORE_NAME_POSITION_FROM_INPUT = 3;

    // First row with a product request on the report file
    private static final int FIRST_ROW = 4;

    // The amount that will subtract from the report file to get the last product request
    private static final int SHIFT_ROWS = 0;

    // Position where the row number starts
    private static final int ROW_NUMBER_POSITION = 2;

    // Position where the cell date is
    private static final int DATE_CELL = 4;

    // Position where the cell ordem num is
    private  static  final int ORDER_NUM_CELL = 3;

    // filter for weight column
    private static final String ORDER_TABLE_FILTER_WEIGHT = "Pedidos";

    private static Workbook workbook;
    private static FileInputStream inputStream;
    private static FileOutputStream outputStream;

    public static void updateReport(
            ArrayList<AttributeExcelRow> excelRowsGoogle, ArrayList<ProductRequest> requests, String reportFilePath)
            throws  Exception
    {
        inputStream = new FileInputStream(reportFilePath);
        workbook = WorkbookFactory.create(inputStream);

        if (excelRowsGoogle != null)
        {
            if (excelRowsGoogle.size() > 0)
            {
                updateReportFromGoogle(excelRowsGoogle, reportFilePath);
            }
            else
            {
                String errorMessage = "Nenhum pedido solicitado no arquivo do Google.";
                throw new Exception(errorMessage);
            }
        }
        if(requests != null)
        {
            if(requests.size() > 0)
            {
                updateFromDysrup(requests, reportFilePath);
            }
            else
            {
                String errorMessage = "Nenhum pedido solicitado no arquivo da Dysrup.";
                throw new Exception(errorMessage);
            }
        }

        workbook.close();
    }

    private static void updateReportFromGoogle(ArrayList<AttributeExcelRow> excelRowsGoogle, String reportFilePath) throws Exception
    {
        Sheet sheet = workbook.getSheetAt(0);

        for (AttributeExcelRow googleRow : excelRowsGoogle)
        {
            if (googleRow.getAttributeValue(MandatoryFieldsGoogle.LOJA).equals(""))
            {
                continue;
            }
            String storeNameCompleteGoogle = googleRow.getAttributeValue(MandatoryFieldsGoogle.LOJA).trim();
            boolean foundStore = false;
            for (int i = FIRST_ROW; i < sheet.getPhysicalNumberOfRows() - SHIFT_ROWS; i++)
            {
                Row row = sheet.getRow(i);

                if (ExcelUtils.isRowEmpty((XSSFRow) row))
                {
                    continue;
                }

                String storeNameCompleteReport = Utils.normalize(ExcelUtils.getStringCellValue((XSSFCell) row.getCell(0)));

                final String storeNameCompleteGoogleReplace = Utils.normalize(storeNameCompleteGoogle.replace("  ", " "));

                if (storeNameCompleteGoogleReplace.equalsIgnoreCase(storeNameCompleteReport))
                {
                    foundStore = true;
                    ArrayList<RequestItem> cells = getCells(googleRow);

                    if (ExcelUtils.getStringCellValue((XSSFCell) row.getCell(DATE_CELL)).trim().isEmpty()) {
                        updateStoreRow(cells, (XSSFRow) row, storeNameCompleteReport);
                    } else {
                        // Is there another product request?
                        for (int index = i; index < sheet.getPhysicalNumberOfRows() - SHIFT_ROWS; index++) {
                            Row rowAux = sheet.getRow(index);

                            storeNameCompleteReport = ExcelUtils.getStringCellValue((XSSFCell) row.getCell(0)).trim();

                            if (Objects.equals(storeNameCompleteGoogleReplace,storeNameCompleteReport)) {
                                row = rowAux;
                            } else {
                                break;
                            }
                        }
                        insertStoreRow(sheet, cells, row.getRowNum()-1);
                    }
                    break;
                }
            }
            if (!foundStore)
            {
                String errorMessage = String.format("A loja %s não foi encontrada!", googleRow.getAttributeValue(MandatoryFieldsGoogle.LOJA));
                throw new Exception(errorMessage);
            }
        }

        inputStream.close();
        outputStream = new FileOutputStream(reportFilePath);
        workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();
        workbook.write(outputStream);
        outputStream.close();
    }

    public static void updateFromDysrup(ArrayList<ProductRequest> requests, String reportFilePath) throws Exception
    {
        Sheet sheet = workbook.getSheetAt(0);
        for (ProductRequest request : requests)
        {
            if(request.getStore().getName().equals(""))
            {
                continue;
            }
            String storeNameDysrup = request.getStore().getName();
            boolean foundStore = false;
            for (int i = FIRST_ROW; i < sheet.getPhysicalNumberOfRows() - SHIFT_ROWS; i++) {
                Row row = sheet.getRow(i);

                if (ExcelUtils.isRowEmpty((XSSFRow) row))
                {
                    continue;
                }

                Cell cell = row.getCell(0);
                String storeNameReport = Utils.normalize(ExcelUtils.getStringCellValue((XSSFCell) row.getCell(0)));
                final String storeNameDysrupReplace = Utils.normalize(storeNameDysrup.replace("  ", " "));

                if (storeNameDysrupReplace.equalsIgnoreCase(storeNameReport))
                {
                    foundStore = true;
                    if (ExcelUtils.getStringCellValue((XSSFCell) row.getCell(DATE_CELL)).trim().isEmpty()) {
                        updateStoreRow(request.getRequestItems(), (XSSFRow) row, storeNameReport);
                    } else {
                        // Is there another product request?
                        for (int index = i; index < sheet.getPhysicalNumberOfRows() - SHIFT_ROWS; index++) {
                            Row rowAux = sheet.getRow(index);

                            storeNameReport = ExcelUtils.getStringCellValue((XSSFCell) row.getCell(0)).trim();

                            if (Objects.equals(storeNameDysrupReplace,storeNameReport)) {
                                row = rowAux;
                            } else {
                                break;
                            }
                        }
                        insertStoreRow(sheet, request.getRequestItems(), row.getRowNum()-1);
                    }
                    break;
                }
            }
            if (!foundStore)
            {
                String errorMessage = String.format("A loja %s não foi encontrada!", request.getStore().getName());
                throw new Exception(errorMessage);
            }
        }

        inputStream.close();
        outputStream = new FileOutputStream(reportFilePath);
        workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();
        workbook.write(outputStream);
        outputStream.close();
    }

    private static ArrayList<RequestItem> getCells (AttributeExcelRow googleRow)
            throws Exception
    {
        ArrayList<RequestItem> cells= new ArrayList<>();

        for (String attributeName : googleRow.getRow().keySet())
        {
            if (!attributeName.equals("") &&
                    !MandatoryFieldsGoogle.MANDATORY_FIELDS_GOOGLE.contains(attributeName) &&
                    !attributeName.equals("Haverá pedido para a loja?") &&
                    !attributeName.equals("Por qual motivo o gerente não quis retirar o pedido?")) {
                cells.add(new RequestItem(new Product(attributeName,"0", null, null),
                        googleRow.getRow().get(attributeName).equals("") ? 0
                                : Integer.parseInt(googleRow.getRow().get(attributeName)),
                        null, null,
                        Utils.getFormattedDateUSA(googleRow.getRow().get(MandatoryFieldsGoogle.CARIMBO).split(" ")[0]),
                        googleRow.getRow().get(MandatoryFieldsGoogle.PEDIDO).trim(), null));
            }
        }
        SortManager.sortItemsById(cells);
        return (cells);
    }

    private static void updateStoreRow (ArrayList<RequestItem> requestItems, XSSFRow row, String storeName) throws Exception
    {
        SimpleDateFormat dateTemp = new SimpleDateFormat("dd/MM/yyyy");
        Date date = dateTemp.parse(requestItems.get(0).getDate());
        row.getCell(DATE_CELL).setCellValue(date);
        row.getCell(ORDER_NUM_CELL).setCellValue(requestItems.get(0).getOrderNum());

        int cellNum = 5;

        try {
            for (RequestItem request : requestItems) {
                XSSFCell reportCell = row.getCell(cellNum);
                int volume = request.getVolume();
                reportCell.setCellValue(volume);
                cellNum++;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            String msg = "Arquivo do GOOGLE ou do GIV com o número inválido de produtos na loja:\n\n" + storeName;
            msg += "\n\nVerifique, por gentileza, se todas as lojas no pedido possuem a mesma quantidade de produtos.";
            throw new Exception(msg);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception(ex.getMessage());
        }
    }

    private static void insertStoreRow(Sheet worksheet, ArrayList<RequestItem> cells, int sourceRowNum) throws Exception
    {
        int destinationRowNum = sourceRowNum + 1;
        // Get the source / new row
        Row newRow = worksheet.getRow(destinationRowNum);
        Row sourceRow = worksheet.getRow(sourceRowNum);

        // If the row exist in destination, push down all rows by 1 else create a new
        if (newRow != null)
        {
            worksheet.shiftRows(destinationRowNum, worksheet.getLastRowNum(), 1);
        }
        newRow = worksheet.createRow(destinationRowNum);

        newRow.setHeight(sourceRow.getHeight());

        // Loop through source columns to add to new row
        for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
            // Grab a copy of the old/new cell
            Cell oldCell = sourceRow.getCell(i);
            Cell newCell = newRow.createCell(i);

            // If the old cell is null jump to next cell
            if (oldCell == null) {
                continue;
            }

            // Use old cell style
            newCell.setCellStyle(oldCell.getCellStyle());

            // "Numero do pedido" can't be duplicate
            if (i != 3) {
                // Set the cell data value
                switch (oldCell.getCellType().getCode()) {
                    case 0 -> newCell.setCellValue(oldCell.getNumericCellValue());
                    case 1 -> newCell.setCellValue(oldCell.getRichStringCellValue());
                    case 2 -> newCell.setCellFormula(formatFormula(oldCell.getCellFormula()));
                    case 4 -> newCell.setCellValue(oldCell.getBooleanCellValue());
                    case 5 -> newCell.setCellErrorValue(oldCell.getErrorCellValue());
                }
            }
        }

        SimpleDateFormat datetemp = new SimpleDateFormat("dd/MM/yyyy");
        Date cellValue = datetemp.parse(cells.get(0).getDate());

        newRow.getCell(DATE_CELL).setCellValue(cellValue);
        newRow.getCell(ORDER_NUM_CELL).setCellValue(cells.get(0).getOrderNum());

        int cellNum = 5;
        for (RequestItem item : cells) {
            Cell newRowCell = newRow.getCell(cellNum);
            if (newRowCell == null) {
                String errorMessage = "Verifique se a quantidade de produtos na loja\n\n";
                errorMessage += "\"" + item.getStore().getName() + "\"\n\nnão ultrapassa a quantidade de colunas do relatório";
                throw new Exception(errorMessage);
            }
            newRowCell.setCellValue(item.getVolume());
            cellNum++;
        }
    }
    private static String formatFormula(String oldString) {
        StringBuilder newString = new StringBuilder();

        if (oldString.contains(ORDER_TABLE_FILTER_WEIGHT)) {
            newString = new StringBuilder(oldString.replaceAll("'Relatório de Pedidos'!", ""));
            String[] newStringList = newString.toString().split("\\+");
            for (String term : newStringList) {
                int position = term.indexOf(':');
                term = term.substring(position+1, term.length()-1);
                newString = new StringBuilder();
                newString.append("(").append(term).append(")").append("+");
            }

            newString = new StringBuilder(newString.substring(0, newString.length()-1));
        }
            String[] oldStringList = oldString.split("\\+");
            for (String term : oldStringList) {
                String[] termList = term.split("\\*");

                String cell = termList[0];
                StringBuilder row = new StringBuilder();

                for (int index = ROW_NUMBER_POSITION; index < cell.length(); index++) {
                    row.append(cell.charAt(index));
                }
                int rowNumber = Integer.parseInt(row.toString()) + 1;

                newString
                        .append(cell, 0, ROW_NUMBER_POSITION)
                        .append(rowNumber)
                        .append("*")
                        .append(termList[1])
                        .append("+");
            }
            newString = new StringBuilder(newString.substring(0, newString.length() - 1));
            return newString.toString();
    }
}
