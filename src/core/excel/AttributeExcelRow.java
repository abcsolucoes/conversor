package core.excel;


import java.util.HashMap;


public class AttributeExcelRow
{
    private int rowNumber;
    private final HashMap<String, String> row;

    public AttributeExcelRow(HashMap<String, String> row)
    {
        this.row = row;
    }

    public HashMap<String, String> getRow() {
        return row;
    }

    @Override
    public String toString() {
        return "AttributeExcelRow{" +
                "rowNumber=" + rowNumber +
                ", row=" + row +
                '}';
    }

    public boolean isEmpty()
    {
        boolean empty = true;
        for(String value : this.row.values())
        {
            empty = empty && value.isEmpty();
        }
        return empty;
    }

    public String getAttributeValue(String attributeName)
    {
        return row.get(attributeName);
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }
}
