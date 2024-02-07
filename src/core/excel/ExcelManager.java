package core.excel;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class ExcelManager
{
    private final String filePath;

    private final XSSFWorkbook workbook;
    private XSSFSheet currentSheet;

    public ExcelManager(String filePath) throws IOException
    {
        this.filePath = filePath;

        if (this instanceof ExcelWriter)
        {
            this.workbook = new XSSFWorkbook();
            this.currentSheet = null;
        }
        else
        {
            this.workbook = new
                    XSSFWorkbook(this.filePath);
            this.currentSheet = this.workbook.getSheetAt(0);
        }

        this.setCurrentSheet(this.currentSheet);
    }

    public ExcelManager(String filePath, String sheetName) throws IOException
    {
        this.filePath = filePath;

        if (this instanceof ExcelWriter)
        {
            this.workbook = new XSSFWorkbook();
            this.currentSheet = createSheet(sheetName);
        }
        else
        {
            this.workbook = new
                    XSSFWorkbook(this.filePath);
            this.currentSheet = this.workbook.getSheetAt(0);
        }

        this.setCurrentSheet(this.currentSheet);
    }

    public ArrayList<XSSFSheet> getAllSheets()
    {
        ArrayList<XSSFSheet> sheets = new ArrayList<>();
        Iterator<Sheet> it = this.workbook.sheetIterator();
        while(it.hasNext())
        {
            sheets.add((XSSFSheet)it.next());
        }
        return sheets;
    }

    public XSSFWorkbook getWorkbook()
    {
        return workbook;
    }

    public XSSFSheet getCurrentSheet()
    {
        return currentSheet;
    }

    public void setCurrentSheet(XSSFSheet otherSheet) {
        currentSheet = otherSheet;
    }

    public int getRowCount()
    {
        return this.currentSheet.getPhysicalNumberOfRows();
    }

    public XSSFSheet createSheet(String sheetName) {
        return this.getWorkbook().createSheet(sheetName);
    }

    // The name of the sheet is the same as the PDV_CODE
    public XSSFSheet getSheetByName(String sheetName)
    {
        for (XSSFSheet sheet : this.getAllSheets())
        {
            if (sheet.getSheetName().equals(sheetName))
            {
                return sheet;
            }
        }
        return null;
    }

    public void closeFile() throws IOException
    {
        this.workbook.close();
    }

    public String getFilePath() {
        return filePath;
    }
}