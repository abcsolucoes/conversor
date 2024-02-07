package core.excel;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ExcelUtils
{
    public static boolean isRowEmpty(XSSFRow row)
    {
        if (row == null || row.getLastCellNum() <= 0)
        {
            return true;
        }

        XSSFCell cell = row.getCell(row.getFirstCellNum());
        return cell == null;
    }

    public static void copyCell(XSSFCell originalCell, XSSFCell newCell)
    {
        newCell.setCellStyle(originalCell.getCellStyle());
        switch (originalCell.getCellType())
        {
            case STRING -> newCell.setCellValue(originalCell.getStringCellValue());
            case NUMERIC -> newCell.setCellValue(originalCell.getNumericCellValue());
            case BLANK -> newCell.setCellType(CellType.BLANK);
            case BOOLEAN -> newCell.setCellValue(originalCell.getBooleanCellValue());
        }
    }

    public static String getStringCellValue(XSSFCell cell)
    {
        return new DataFormatter().formatCellValue(cell);
    }

    public static String getStringCellValue(HSSFCell cell)
    {
        return new DataFormatter().formatCellValue(cell);
    }

    public static int getMaxColumnCount(XSSFSheet sheet)
    {
        int maxColumnCount = 0;
        for (Iterator<Row> it = sheet.rowIterator(); it.hasNext(); )
        {
            XSSFRow row = (XSSFRow) it.next();
            int columns = row.getPhysicalNumberOfCells();

            if (columns > maxColumnCount)
            {
                maxColumnCount = columns;
            }
        }
        return maxColumnCount;
    }

    public static void printRow(XSSFRow row)
    {
        Iterator<Cell> it = row.cellIterator();
        while(it.hasNext())
        {
            System.out.print(it.next() + " | ");
        }
        System.out.println();
    }

    public static void autoAdjustColumns(XSSFSheet sheet)
    {
        int columnCount = ExcelUtils.getMaxColumnCount(sheet);
        for (int col = 0; col < columnCount; col++)
        {
            sheet.autoSizeColumn(col);
        }
    }

    public static void fitToPage(XSSFSheet sheet)
    {
        sheet.setFitToPage(true);
    }

    /**
     * Given a list of rows, groups each sublist of rows according to one attribute in common
     * @return - a hashmap where the key is the attribute in common and the value is a list of rows
     * that have the attribute in common
     */
    public static HashMap<String, ArrayList<AttributeExcelRow>> groupByAttribute(
            String attribute, ArrayList<AttributeExcelRow> data)
    {
        HashMap<String, ArrayList<AttributeExcelRow>> groupedData = new HashMap<>();

        for (AttributeExcelRow row : data)
        {
            String attributeValue = row.getAttributeValue(attribute);
            groupedData.putIfAbsent(attributeValue, new ArrayList<>());
            groupedData.get(attributeValue).add(row);
        }

        return groupedData;
    }
}
