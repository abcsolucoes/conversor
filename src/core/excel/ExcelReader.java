package core.excel;
import core.utils.Utils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;

import java.util.HashMap;

import core.excel.converter.AttributeValidator;

import java.util.*;

public class ExcelReader extends ExcelManager
{
    private final ArrayList<String> attributes;

    public ExcelReader(String filePath, ArrayList<String> mandatoryAttributes) throws Exception
    {
        super(filePath);

        if (mandatoryAttributes == null)
        {
            this.attributes = null;
        }
        else
        {
            this.attributes = AttributeValidator.validateAttributes(this.getCurrentSheet(), mandatoryAttributes);
        }
    }

    /**
     * Le a planilha inteira
     * @return - Lista de tabelas hash,
     */
    public ArrayList<AttributeExcelRow> readCurrentSheet() throws Exception
    {
        ArrayList<AttributeExcelRow> data = new ArrayList<>();

        // Read each row
        // We start from one because we skip the first row (which contains the attributes)
        for (int i = 1; i < this.getRowCount(); i++)
        {
            HashMap<String, String> excelRow = new HashMap<>();
            XSSFRow row = this.getCurrentSheet().getRow(i);

            if (row == null)
            {
                String message = String.format("Formatação incorreta do arquivo %s.\n" +
                        "Possiveis linhas vazias.\nDica: copiar as linhas em uma nova aba.", this.getFilePath());
                throw new Exception(message);
            }
            try
            {
                // Read each column of the row
                for (int j = 0; j < this.attributes.size(); j++)
                {
                    XSSFCell cell = row.getCell(j);
                    String cellValue = Utils.normalize(ExcelUtils.getStringCellValue(cell));

                    String attributeName = this.attributes.get(j);
                    excelRow.put(attributeName, cellValue);
                }
            }
            catch(IndexOutOfBoundsException e)
            {
                throw new Exception("Algo deu errado ao ler a planilha. Por favor, verifique se não " +
                        "existem linhas ou colunas sobrando. Desative o filtro também. " + e.getMessage());
            }
            catch(Exception e)
            {
                e.printStackTrace();
                throw new Exception("Algo deu errado ao ler a planilha: " + e.getMessage());
            }

            AttributeExcelRow attrRow = new AttributeExcelRow(excelRow);
            attrRow.setRowNumber(i + 1);
            if(!attrRow.isEmpty())
            {
                data.add(attrRow);
            }
        }

        return data;
    }

    public ArrayList<String> readNetworkCodes() throws Exception
    {
        ArrayList<String> data = new ArrayList<>();
        XSSFRow row = this.getCurrentSheet().getRow(0);
        try{
            // Read each column of the row
            for (int j = 7; j < this.attributes.size(); j++)
            {
                XSSFCell cell = row.getCell(j);
                String cellValue = Utils.normalize(ExcelUtils.getStringCellValue(cell));
                String[] productNameList  = cellValue.split(" ");
                String networkCode = productNameList[productNameList.length-1];
                data.add(networkCode);
            }
        }
        catch(IndexOutOfBoundsException e)
        {
            throw new Exception("Algo deu errado ao ler a planilha. Por favor, verifique se não " +
                    "existem linhas ou colunas sobrando. Desative o filtro também. " + e.getMessage());
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new Exception("Algo deu errado ao ler a planilha: " + e.getMessage());
        }

        return data;
    }

    public ArrayList<String> getAttributes() {
        return attributes;
    }

}