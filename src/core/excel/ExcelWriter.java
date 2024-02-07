package core.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelWriter extends ExcelManager implements Writable
{
    private int currentRowIndex;

    private final FileOutputStream outputStream;

    public final CellStyles cellStyles;

    public ExcelWriter(String filePath) throws IOException
    {
        super(filePath);
        this.currentRowIndex = 0;
        this.outputStream = new FileOutputStream(filePath);
        this.cellStyles = new CellStyles(this.getWorkbook());
    }

    public ExcelWriter(String filePath, String sheetName) throws IOException
    {
        super(filePath, sheetName);
        this.currentRowIndex = 0;
        this.outputStream = new FileOutputStream(filePath);
        this.cellStyles = new CellStyles(this.getWorkbook());
    }

    /**
     * Appends a row to the file
     * @param cellStyle - the style that will be applied
     * @param values - a list of values that compose a row
     */
    public void append(CellStyle cellStyle, String... values)
    {
        int lastRowNum = this.getCurrentSheet().getLastRowNum();
        int newRowNum = lastRowNum + 1;

        XSSFRow row = this.getCurrentSheet().createRow(newRowNum);

        // If we are appending a title, center it. Else, align to the left
        HorizontalAlignment horizontalAlignment = HorizontalAlignment.LEFT;
        VerticalAlignment verticalAlignment = VerticalAlignment.CENTER;
        if (cellStyle.getFillForegroundColor() == IndexedColors.GREY_40_PERCENT.index)
        {
            row.setHeightInPoints((2 * this.getCurrentSheet().getDefaultRowHeightInPoints()));
            horizontalAlignment = HorizontalAlignment.CENTER;
            verticalAlignment = VerticalAlignment.BOTTOM;
        }
        else
        {
            row.setHeightInPoints((1.5f * this.getCurrentSheet().getDefaultRowHeightInPoints()));
        }

        int columnCount = 0;
        for (String val : values)
        {
            XSSFCell cell = row.createCell(columnCount);
            cell.setCellStyle(cellStyle);
            cellStyle.setWrapText(true);
            cellStyle.setAlignment(horizontalAlignment);
            cellStyle.setVerticalAlignment(verticalAlignment);
            cell.setCellValue(val);
            columnCount++;
        }

        this.currentRowIndex++;
    }

    public void breakLines(int numberOfLines)
    {
        for (int i = 0; i < numberOfLines; i++)
        {
            this.append(this.cellStyles.get(CellStyles.REGULAR), "");
        }
    }

    public int getCurrentRowIndex()
    {
        return this.currentRowIndex;
    }

    @Override
    public void closeFile() throws IOException
    {
        this.outputStream.flush();
        this.outputStream.close();
        this.getWorkbook().close();
    }

    @Override
    public void saveFile() throws IOException {
        this.getWorkbook().write(this.outputStream);
    }
}
