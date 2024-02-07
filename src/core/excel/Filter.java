package core.excel;

import core.utils.Utils;

import java.util.ArrayList;

public class Filter
{
    /**
     * Given an attribute, filters the excel retrieving only items that have that certain attribute value
     */
    public static ArrayList<AttributeExcelRow> filterEquals(
            String attributeName, String attributeValue, ArrayList<AttributeExcelRow> excelRows)
    {
        ArrayList<AttributeExcelRow> filteredData = new ArrayList<>();

        for (AttributeExcelRow row : excelRows)
        {
            if (Utils.normalize(attributeValue).equalsIgnoreCase(row.getAttributeValue(Utils.normalize(attributeName))))
            {
                filteredData.add(row);
            }
        }

        return filteredData;
    }


    /**
     * Given an attribute, filters the excel retrieving only items that have a different attribute value
     */
    public static ArrayList<AttributeExcelRow> filterNotEquals(
            String attributeName, String attributeValue, ArrayList<AttributeExcelRow> excelRows)
    {
        ArrayList<AttributeExcelRow> filteredData = new ArrayList<>();

        for (AttributeExcelRow row : excelRows)
        {
            if (!attributeValue.equals(row.getAttributeValue(attributeName)))
            {
                filteredData.add(row);
            }
        }

        return filteredData;
    }
}
