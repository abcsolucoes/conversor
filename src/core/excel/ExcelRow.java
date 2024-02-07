package core.excel;

import java.util.ArrayList;

public class ExcelRow
{
    private final ArrayList<String> values;

    public ExcelRow(ArrayList<String> values) {
        this.values = values;
    }

    public ArrayList<String> getValues() {
        return values;
    }

    public boolean isEmpty()
    {
        boolean empty = true;
        for(String value : this.values)
        {
            empty = empty && value.isEmpty();
        }
        return empty;
    }

    @Override
    public String toString() {
        return "ExcelRow{" +
                "values=" + values +
                '}';
    }
}
