package core.excel.converter.conversion_types;

import core.abc.*;
import core.excel.converter.ConversionMethod;

import core.excel.*;
import core.excel.converter.MandatoryFieldsFrootyGIV;
import core.excel.converter.RequestItem;
import core.utils.Utils;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.ArrayList;
import java.util.HashMap;

import static core.abc.ProductRequestHandler.*;


public class FrootySendToClientConverter extends AbstractExcelConverter implements ConversionMethod
{
    @Override
    public void generateCorrectExcel(ArrayList<AttributeExcelRow> excelRows, String excelName, String directoryPath)
            throws Exception
    {
        String generatedExcelPath = generateFilepath(excelName, directoryPath);

        ExcelWriter excelWriter = new ExcelWriter(generatedExcelPath);

        HashMap<String, ArrayList<RequestItem>> itemsPerClientName = new HashMap<>();

        excelRows = Filter.filterEquals(MandatoryFieldsFrootyGIV.HAVERA_PEDIDO, "Sim", excelRows);

        for (AttributeExcelRow row : excelRows)
        {
            String productCode = row.getAttributeValue(MandatoryFieldsFrootyGIV.CODIGO_PRODUTO).split("-")[0].trim();
            String productName = row.getAttributeValue(MandatoryFieldsFrootyGIV.PRODUTO);
            String productBrand = "Frooty";
            Product product = new Product(productName, productCode, null, productBrand);

            int volume = Integer.parseInt(row.getAttributeValue(MandatoryFieldsFrootyGIV.QUANTIDADE));

            String storeName = row.getAttributeValue(MandatoryFieldsFrootyGIV.LOJA);
            String storeCode = row.getAttributeValue(MandatoryFieldsFrootyGIV.CODIGO_LOJA);
            String bannerName = row.getAttributeValue(MandatoryFieldsFrootyGIV.BANDEIRA);
            Store store = new Store(storeName, storeCode, new Banner(bannerName));

            String replenisherName = row.getAttributeValue(MandatoryFieldsFrootyGIV.PROMOTOR);
            Replenisher replenisher = new Replenisher(replenisherName);

            String date = row.getAttributeValue(MandatoryFieldsFrootyGIV.DATA);
            date = Utils.getFormattedDateUSA(date);

            String orderNum = row.getAttributeValue(MandatoryFieldsFrootyGIV.NUMERO_PEDIDO);

            String comments = row.getAttributeValue(MandatoryFieldsFrootyGIV.OBSERVACOES);

            RequestItem item = new RequestItem(product, volume, store, replenisher,
                    date, orderNum, comments);

            itemsPerClientName.computeIfAbsent(productBrand, k -> new ArrayList<>());
            itemsPerClientName.get(productBrand).add(item);
        }

        ArrayList<RequestItem> clientItems = itemsPerClientName.get("Frooty");
        Client client = new Client("Frooty", clientItems);
        writeClientSheet(excelWriter, client);

        excelWriter.saveFile();
        excelWriter.closeFile();
    }

    /**
     * Given a client, creates a sheet for that client. Each sheet contains a list of pages, where each
     * page is a product request that contains a list of requested items for a certain store.
     */
    private static void writeClientSheet(ExcelWriter writer, Client client) throws Exception
    {
        XSSFSheet clientSheet = writer.createSheet(client.getName());
        writer.setCurrentSheet(clientSheet);

        writeClientPages(writer, client);

        ExcelUtils.autoAdjustColumns(writer.getCurrentSheet());
        ExcelUtils.fitToPage(writer.getCurrentSheet());
        writer.getCurrentSheet().setAutobreaks(true);
    }

    private static void writeClientPages(ExcelWriter writer, Client client) throws Exception
    {
        ArrayList<ProductRequest> clientRequests = getListOfProductRequests(client);
        SortManager.sortByAlphabeticStoreName(clientRequests);

        for (ProductRequest productRequest : clientRequests)
        {
            writeProductRequestPage(writer, productRequest);
            writer.getCurrentSheet().setRowBreak(writer.getCurrentRowIndex());
            writer.breakLines(5);
        }
    }
}
