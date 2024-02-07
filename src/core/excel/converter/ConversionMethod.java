package core.excel.converter;

import core.excel.AttributeExcelRow;

import java.util.ArrayList;

public interface ConversionMethod
{
    void generateCorrectExcel(ArrayList<AttributeExcelRow> excelRows, String excelName, String directoryPath) throws Exception;
}
