package core.excel.converter;

import core.excel.ExcelUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import javax.management.AttributeNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;

public class AttributeValidator
{
    public static ArrayList<String> foundAttributes;

    /**
     * Validates the attributes, looking for missing ones
     */
    public static ArrayList<String> validateAttributes(XSSFSheet sheet, ArrayList<String> mandatoryAttributes) throws Exception
    {
        ArrayList<String> tempMandatory = new ArrayList<>(mandatoryAttributes);
        foundAttributes = new ArrayList<>();

        XSSFRow row = sheet.getRow(0);
        Iterator<Cell> it = row.cellIterator();
        while (it.hasNext())
        {
            Cell cell = it.next();
            String attributeName = ExcelUtils.getStringCellValue((XSSFCell) cell);

            foundAttributes.add(attributeName.trim());
        }

        // Removes all found attributes from the mandatory temp.
        tempMandatory.removeAll(foundAttributes);

        // If the mandatory list is not empty, it means that not all
        // mandatory attributes were found.
        if (!tempMandatory.isEmpty())
        {
            StringBuilder fields = new StringBuilder();
            for (String attribute : mandatoryAttributes)
            {
                fields.append(attribute).append('\n');
            }

            String message = String.format(
                    "Coluna \"%s\" faltando.\nSeguem as colunas obrigatórias: \n\n%s", tempMandatory.get(0), fields);
            throw new AttributeNotFoundException(message);
        }

        return foundAttributes;
    }
}
