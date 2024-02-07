package core.excel.converter.conversion_types;

import core.abc.*;
import core.excel.converter.ConversionMethod;

import core.excel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.ArrayList;
import static core.abc.ProductRequestHandler.*;


public class SendToClientConverter extends AbstractExcelConverter implements ConversionMethod
{
    @Override
    public void generateCorrectExcel(ArrayList<AttributeExcelRow> excelRows, String excelName, String directoryPath)
            throws Exception
    {
        String generatedExcelPath = generateFilepath(excelName, directoryPath);

        ExcelWriter excelWriter = new ExcelWriter(generatedExcelPath);

        //ArrayList<AttributeExcelRow> listOfRuptures = filterRupturesOnly(excelRows);

        ArrayList<Client> clientList = getClientList(excelRows);
        SortManager.sortClientsByAlphabeticName(clientList);

        // For each client, we create his list of product requests. Each product request is associated to a store
        for (Client client : clientList)
        {
            // Now we write that client's requests in a new sheet, where each sheet has many pages,
            // and each *page* in the sheet is a request.
            writeClientSheet(excelWriter, client);
        }

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
