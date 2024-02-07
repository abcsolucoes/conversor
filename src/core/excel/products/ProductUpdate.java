package core.excel.products;

import core.abc.Provider.ProviderInfo;
import core.abc.Provider.ProviderItem;
import core.excel.*;
import core.utils.Utils;
import org.apache.poi.ss.usermodel.CellStyle;

import java.util.ArrayList;

public class ProductUpdate
{
    private static final int PROVIDER_CODE_POSITION = 2;
    private static final int PROVIDER_NAME_POSITION = 4;

    public static void updateProducts(ExcelWriter writer, ArrayList<ExcelRow> excelRows)
    {
        String providerName = null;
        String providerCode = null;
        ArrayList<ProviderItem> items = new ArrayList<>();

        for (ExcelRow row : excelRows)
        {
            ArrayList<String> values = row.getValues();
            String firstCellValue = values.get(0);
            if (firstCellValue.equals(ProductUpdateMandatoryFields.FORNECEDOR))
            {
                providerCode = values.get(PROVIDER_CODE_POSITION);
                providerName = values.get(PROVIDER_NAME_POSITION);
            }
            else if (Utils.isNumeric(firstCellValue))
            {
                String itemCode = firstCellValue;
                String itemName = values.get(1);
                int skuCode = Integer.parseInt(values.get(values.size() - 1).split(",")[0]);
                ProviderItem item = new ProviderItem(itemCode, itemName, skuCode);
                items.add(item);
            }
        }

        ProviderInfo providerInfo = new ProviderInfo(providerName, providerCode, items);
        writeToSheet(writer, providerInfo);
    }

    private static void writeToSheet(ExcelWriter writer, ProviderInfo providerInfo)
    {
        /* Provider code | Provider Name | Item Code | Item Name | Item SKU Code */
        for (ProviderItem item : providerInfo.getItems())
        {
            CellStyle style = writer.cellStyles.get(CellStyles.REGULAR);
            writer.append(
                    style,
                    providerInfo.getCode(),
                    providerInfo.getName(),
                    item.getCode(),
                    item.getName(),
                    String.valueOf(item.getSkuCode())
            );
        }
    }
}
