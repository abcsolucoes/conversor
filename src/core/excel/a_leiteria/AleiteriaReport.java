package core.excel.a_leiteria;

import core.abc.Product;
import core.abc.ProductRequest;
import core.excel.AttributeExcelRow;
import core.excel.ExcelUtils;
import core.excel.ReportConstants;
import core.excel.SortManager;
import core.excel.converter.MandatoryFieldsGoogle;
import core.excel.converter.RequestItem;
import core.utils.Utils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class AleiteriaReport
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
    private static final int DATE_CELL = 2;

    private static Workbook workbook;
    private static FileInputStream inputStream;
    private static FileOutputStream outputStream;

    public static void updateReport(
            ArrayList<AttributeExcelRow> excelRowsGoogle, ArrayList<ProductRequest> requestsInvolves, String reportFilePath)
            throws Exception
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
        if (requestsInvolves != null)
        {
            if (requestsInvolves.size() > 0)
            {
                updateReportFromInvolves(requestsInvolves, reportFilePath);
            }
            else
            {
                String errorMessage = "Nenhum pedido solicitado no arquivo do GIV.";
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
            int storeCodeGoogle = Integer.parseInt(googleRow.getAttributeValue(
                    MandatoryFieldsGoogle.LOJA).trim().split(" ")[1]);

            String[] storeNameList = googleRow.getAttributeValue(
                    MandatoryFieldsGoogle.LOJA).trim().split(" ");

            StringBuilder storeNameGoogle = new StringBuilder();
            for (int index = STORE_NAME_POSITION_FROM_INPUT; index < storeNameList.length; index++)
            {
                storeNameGoogle.append(storeNameList[index]).append(" ");
            }

            storeNameGoogle = new StringBuilder(storeNameGoogle.toString().trim());

            boolean foundStore = false;

            for (int i = FIRST_ROW; i < sheet.getPhysicalNumberOfRows() - SHIFT_ROWS; i++)
            {
                Row row = sheet.getRow(i);

                if (ExcelUtils.isRowEmpty((XSSFRow) row))
                {
                    continue;
                }

                Cell cell = row.getCell(0);

                int storeCodeReport = 0;

                String cellValue = Utils.normalize(ExcelUtils.getStringCellValue((XSSFCell) cell));

                if (!cellValue.isEmpty() &&
                    !cellValue.equalsIgnoreCase(ReportConstants.VALOR_TOTAL_DO_PERIODO) &&
                    !cellValue.equalsIgnoreCase(ReportConstants.VALOR_TOTAL_DE_COMISSAO_DO_PERIODO))
                {
                    storeCodeReport = Integer.parseInt(cellValue);
                }

                String storeNameReport = Utils.normalize(ExcelUtils.getStringCellValue((XSSFCell) row.getCell(1)).trim());

                final String storeNameGoogleReplace = Utils.normalize(storeNameGoogle.toString().replace("  ", " "));

                if (storeCodeGoogle == storeCodeReport && storeNameGoogleReplace.equalsIgnoreCase(storeNameReport))
                {
                    foundStore = true;

                    ArrayList<RequestItem> cells = getCells(googleRow);
                    if (ExcelUtils.getStringCellValue((XSSFCell) row.getCell(DATE_CELL)).trim().isEmpty()) {
                        updateStoreRow(cells, (XSSFRow) row);
                    } else {
                        // Is there another product request?
                        for (int index = i; index < sheet.getPhysicalNumberOfRows() - SHIFT_ROWS; index++) {
                            Row rowAux = sheet.getRow(index);
                            cell = rowAux.getCell(0);
                            storeCodeReport = ExcelUtils.getStringCellValue((XSSFCell) cell).trim().isEmpty() ?
                                    0 : Integer.parseInt(ExcelUtils.getStringCellValue((XSSFCell) cell));

                            storeNameReport = ExcelUtils.getStringCellValue((XSSFCell) row.getCell(1)).trim();

                            if (storeCodeGoogle == storeCodeReport &&
                                    Objects.equals(storeNameGoogleReplace,
                                            storeNameReport.toLowerCase())) {
                                row = rowAux;
                            } else {
                                break;
                            }
                        }
                        insertStoreRow(sheet, cells, row.getRowNum());
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

    private static void updateReportFromInvolves(ArrayList<ProductRequest> requestsInvolves, String reportFilePath) throws Exception
    {
        Sheet sheet = workbook.getSheetAt(0);
        for (ProductRequest request : requestsInvolves)
        {
            int storeCodeInvolves = Integer.parseInt(request.getStore().getName()
                    .split("-")[0].trim().split(" ")[1]);

            String storeNameInvolves = request.getStore().getName()
                    .split("-")[1].trim().replace("  ", " ");

            boolean foundStore = false;

            for (int i = FIRST_ROW; i < sheet.getPhysicalNumberOfRows() - SHIFT_ROWS; i++)
            {
                Row row = sheet.getRow(i);

                if (ExcelUtils.isRowEmpty((XSSFRow) row))
                {
                    continue;
                }

                Cell cell = row.getCell(0);

                int storeCodeReport = 0;

                String cellValue = Utils.normalize(ExcelUtils.getStringCellValue((XSSFCell) cell));

                if (!cellValue.isEmpty() &&
                        !cellValue.equalsIgnoreCase(ReportConstants.VALOR_TOTAL_DO_PERIODO) &&
                        !cellValue.equalsIgnoreCase(ReportConstants.VALOR_TOTAL_DE_COMISSAO_DO_PERIODO))
                {
                    storeCodeReport = Integer.parseInt(cellValue);
                }

                String storeNameReport = Utils.normalize(ExcelUtils.getStringCellValue((XSSFCell) row.getCell(1)).trim());

                if (storeCodeInvolves == storeCodeReport &&
                        storeNameInvolves.equalsIgnoreCase(storeNameReport))
                {
                    foundStore = true;
                    if (ExcelUtils.getStringCellValue((XSSFCell) row.getCell(DATE_CELL)).trim().isEmpty())
                    {
                        updateStoreRow(request.getRequestItems(), (XSSFRow) row);
                    }
                    else
                    {
                        // Is there another product request?
                        for (int index = i; index < sheet.getPhysicalNumberOfRows() - SHIFT_ROWS; index++){
                            Row rowAux = sheet.getRow(index);
                            cell = rowAux.getCell(0);
                            storeCodeReport = ExcelUtils.getStringCellValue((XSSFCell) cell).trim().isEmpty() ?
                                    0 : Integer.parseInt(ExcelUtils.getStringCellValue((XSSFCell) cell));

                            storeNameReport = ExcelUtils.getStringCellValue((XSSFCell) row.getCell(1)).trim();

                            if (storeCodeInvolves == storeCodeReport &&
                                    storeNameInvolves.equals(storeNameReport))
                            {
                                row = rowAux;
                            }else {
                                break;
                            }
                        }
                        insertStoreRow(sheet, request.getRequestItems(), row.getRowNum());
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

    private static void updateStoreRow (ArrayList<RequestItem> cells, XSSFRow row) throws ParseException {

        SimpleDateFormat datetemp = new SimpleDateFormat("dd/MM/yyyy");
        Date cellValue = datetemp.parse(cells.get(0).getDate());

        row.getCell(DATE_CELL).setCellValue(cellValue);

        int cellNum = 3;

        for (RequestItem request : cells)
        {
            row.getCell(cellNum).setCellValue(request.getVolume());
            cellNum++;
        }

    }

    private static void insertStoreRow(Sheet worksheet, ArrayList<RequestItem> cells, int sourceRowNum)
            throws ParseException
    {
        int destinationRowNum = sourceRowNum + 1;

        // Get the source / new row
        Row newRow = worksheet.getRow(destinationRowNum);
        Row sourceRow = worksheet.getRow(sourceRowNum);

        // If the row exist in destination, push down all rows by 1 else create a new row
        if (newRow != null)
        {
            worksheet.shiftRows(destinationRowNum, worksheet.getLastRowNum(), 1);
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

        int cellNum = 3;

        for (RequestItem request : cells)
        {
            newRow.getCell(cellNum).setCellValue(request.getVolume());
            cellNum++;
        }
    }

    private static ArrayList<RequestItem> getCells (AttributeExcelRow googleRow)
            throws Exception
    {
        ArrayList<RequestItem> cells= new ArrayList<>();

        for (String attributeName : googleRow.getRow().keySet())
        {
            if (!attributeName.equals("") &&
                    !MandatoryFieldsGoogle.MANDATORY_FIELDS_GOOGLE.contains(attributeName)) {
                cells.add(new RequestItem(new Product(attributeName,"0", null,null),
                        googleRow.getRow().get(attributeName).equals("") ? 0
                                :Integer.parseInt(googleRow.getRow().get(attributeName)),
                        null, null,
                        Utils.getFormattedDateUSA(googleRow.getRow().get(MandatoryFieldsGoogle.CARIMBO).split(" ")[0]),
                        null, null));
            }
        }

        SortManager.sortItemsById(cells);
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
}
