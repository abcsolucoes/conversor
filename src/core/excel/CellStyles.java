package core.excel;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.HashMap;

public class CellStyles
{
    private final XSSFWorkbook workbook;

    public static final int REGULAR = 0;
    public static final int BOLD = 1;
    public static final int GREY_25_BACKGROUND = 2;
    public static final int TITLE = 3;

    public final HashMap<Integer, CellStyle> styles;

    public CellStyles(XSSFWorkbook workbook)
    {
        this.workbook = workbook;
        this.styles = new HashMap<>() {{
            put(CellStyles.REGULAR, regular());
            put(CellStyles.BOLD, bold());
            put(CellStyles.GREY_25_BACKGROUND, grey_25_background());
            put(CellStyles.TITLE, title());
        }};
    }

    public CellStyle get(int code) throws IllegalArgumentException
    {
        if (code >= 0 && code <= styles.size()) {
            return styles.get(code);
        } else {
            throw new IllegalArgumentException("Invalid Style code: " + code);
        }
    }

    private CellStyle regular()
    {
        return workbook.getCellStyleAt(0);
    }

    private CellStyle bold()
    {
        CellStyle bold = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        bold.setFont(font);

        return bold;
    }

    private CellStyle grey_25_background()
    {
        CellStyle grey_25 = workbook.createCellStyle();
        grey_25.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        grey_25.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return grey_25;
    }

    private CellStyle title()
    {
        CellStyle title = workbook.createCellStyle();
        title.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.index);
        title.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font font = workbook.createFont();
        font.setColor(IndexedColors.BLACK.getIndex());
        font.setFontHeightInPoints((short) 16);
        font.setBold(true);
        title.setFont(font);

        return title;
    }
}
