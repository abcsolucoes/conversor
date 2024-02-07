package core.excel.converter.conversion_types;

import core.abc.ProductRequest;
import core.excel.*;
import core.excel.converter.ConversionMethod;
import core.excel.converter.RequestItem;
import core.utils.Utils;
import org.apache.poi.ss.usermodel.CellStyle;
import ui.views.converter.ConverterController;

import java.nio.file.Paths;

public abstract class AbstractExcelConverter implements ConversionMethod
{
    protected String generateFilepath(String excelName, String saveDirectoryPath)
    {
        String[] tmp = excelName.split("\\.");
        String name = tmp[0];
        String extension = tmp[1];
        String dateString = Utils.getCurrentDateString();

        String finalName = String.format("%s_CONVERTIDO at %s.%s", name, dateString, extension);
        return Paths.get(saveDirectoryPath, finalName).toString();
    }

//    public static ArrayList<AttributeExcelRow> filterRupturesOnly(ArrayList<AttributeExcelRow> excelRows)
//    {
//        return Filter.filter(MandatoryFieldsInvolves.SERA_REALIZADO_PEDIDO, "Sim", excelRows);
//    }

    public static void writeProductRequestPage(ExcelWriter writer, ProductRequest productRequest)
    {
        //SortManager.sortItemsById(productRequest.getRequestItems());

        String titulo = productRequest.generateTitle();
        String storeName = productRequest.getStore().getName();

        writer.append(writer.cellStyles.get(CellStyles.TITLE), "", titulo, "", "");
        writer.append(writer.cellStyles.get(CellStyles.BOLD), "Loja", "Código Loja", "Promotor", "Data");
        writer.append(
                writer.cellStyles.get(CellStyles.REGULAR),
                storeName,
                productRequest.getStore().getCode(),
                productRequest.getReplenisher().getName(),
                productRequest.getDate().split(" ")[0]);

        writer.append(writer.cellStyles.get(CellStyles.BOLD), "N° do Pedido", "Observações");
        writer.append(writer.cellStyles.get(CellStyles.REGULAR),
                productRequest.getOrderNum(), productRequest.getComments());

        writer.append(writer.cellStyles.get(CellStyles.TITLE), "", "PRODUTOS", "", "");
        writer.append(writer.cellStyles.get(CellStyles.BOLD), "Código Produto", "Produto", "Volume");

        int[] styleCodes = {CellStyles.REGULAR, CellStyles.GREY_25_BACKGROUND};
        boolean bit = false;
        for (RequestItem item : productRequest.getRequestItems())
        {
            String code = item.getProduct().getNetworkCode();
            if (Utils.normalize(productRequest.getBrand()).equalsIgnoreCase("Selmi") && ConverterController.typeFile.equals(ConverterController.DYSRUP_LABEL_STRING)) {
                code = item.getProduct().getIndustryCode();
            }
            int index = bit ? 1 : 0;
            int currentStyleCode = styleCodes[index];
            CellStyle currentStyle = writer.cellStyles.get(currentStyleCode);
            writer.append(
                    currentStyle,
                    code,
                    item.getProduct().getName(),
                    String.valueOf(item.getVolume()), "");
            bit = !bit;
        }
    }
}
