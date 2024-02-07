package core.excel.converter.conversion_types;

import core.abc.*;
import core.excel.*;
import core.excel.converter.MandatoryFieldsFrootyGIV;
import core.excel.converter.MandatoryFieldsFrootyGoogle;
import core.excel.converter.RequestItem;
import core.utils.Utils;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static core.abc.ProductRequestHandler.getClientList;
import static core.abc.ProductRequestHandler.getListOfProductRequests;
import static core.excel.converter.conversion_types.AbstractExcelConverter.writeProductRequestPage;

public class FrootyPrintConverter
{
    public void generateCorrectExcel(ArrayList<ArrayList<AttributeExcelRow>> googleExcelRowsMatrix,
                                     String[] googleExcelName,
                                     ArrayList<AttributeExcelRow> givExcelRows,
                                     String directoryPath,
                                     ArrayList<ArrayList<String>> googleAttributes) throws Exception
    {
        String dateString = Utils.getCurrentDateString();
        String finalName = String.format("IMPRESSÃO at %s.xlsx", dateString);
        try {
            ExcelWriter excelWriter = new ExcelWriter(Paths.get(directoryPath, finalName).toString());

            // Google
            if (googleExcelRowsMatrix != null && googleExcelRowsMatrix.size() > 0)
            {
                int googleFileIndex = 0;

                try
                {
                    for (ArrayList<AttributeExcelRow> googleExcelRows : googleExcelRowsMatrix) {
                        ArrayList<Product> products = new ArrayList<>();

                        int index = googleAttributes.get(googleFileIndex).indexOf(MandatoryFieldsFrootyGoogle.FOTO) + 1;

                        List<String> productsNames = googleAttributes.get(googleFileIndex).subList(index,
                                googleAttributes.get(googleFileIndex).size());

                        if (productsNames.size() == 0) {
                            int i = 0;
                            StringBuilder columnsOrder = new StringBuilder();
                            for (String columnName : MandatoryFieldsFrootyGoogle.MANDATORY_FIELDS_GOOGLE) {
                                columnsOrder.append(String.format("%d) ", i + 1)).append(columnName).append("\n");
                                i++;
                            }
                            String observation = "OBS: Todos os produtos devem vir APÓS a coluna " + "\"" + MandatoryFieldsFrootyGoogle.FOTO + "\"";
                            throw new Exception("Não foi possível encontrar os produtos no arquivo.\n\nVerifique se todas " +
                                    "as colunas do arquivo seguem a seguinte ordem: \n\n" + columnsOrder + "\n\n" + observation);
                        }

                        String[] tmp = Paths.get(googleExcelName[googleFileIndex]).getFileName().toString().split(" ");

                        String brand, banner;

                        try {
                            brand = tmp[1];
                            banner = tmp[2];
                        } catch (IndexOutOfBoundsException e) {
                            String filenamePattern = "PEDIDOS <MARCA> <BANDEIRA>";
                            String message = String.format(
                                    "Nome do arquivo inválido: %s\n\nO nome correto deve seguir o seguinte padrão:\n\n%s",
                                    googleExcelName[googleFileIndex],
                                    filenamePattern);
                            throw new Exception(message);
                        }

                        googleFileIndex++;

                        for (String productName : productsNames) {

                            int dashIndex = productName.lastIndexOf('-');

                            if (dashIndex != -1) {
                                String productCode = productName.substring(dashIndex + 1).trim();

                                try {
                                    Integer.parseInt(productCode.replace(".", ""));
                                } catch (Exception e) {
                                    throw new Exception("Verifique se todos os produtos possuem o código ao final, após o traço.\n\b" +
                                            "Modelo correto:\n\n<NOME PRODUTO> - <CÓDIGO PRODUTO>");
                                }

                                products.add(new Product(
                                        productName.substring(0, dashIndex).trim(),
                                        productCode,
                                        null,
                                        brand
                                ));
                            }
                        }

                        ArrayList<ProductRequest> storesRequests = new ArrayList<>();

                        for (AttributeExcelRow row : googleExcelRows) {
                            ArrayList<RequestItem> requestItems = new ArrayList<>();
                            Store store = new Store(row.getAttributeValue(MandatoryFieldsFrootyGoogle.LOJA), new Banner(banner));
                            Replenisher replenisher = new Replenisher(row.getAttributeValue(MandatoryFieldsFrootyGoogle.NOME_PROMOTOR));

                            for (Product product : products) {
                                requestItems.add(new RequestItem(
                                        product,
                                        Integer.parseInt(row.getAttributeValue(product.getName() + " - " + product.getNetworkCode())),
                                        store,
                                        replenisher,
                                        Utils.getFormattedDateUSA(row.getAttributeValue(MandatoryFieldsFrootyGoogle.DATA)),
                                        null, null
                                ));
                            }

                            storesRequests.add(new ProductRequest(
                                    brand,
                                    store,
                                    replenisher,
                                    Utils.getFormattedDateUSA(row.getAttributeValue(MandatoryFieldsFrootyGoogle.DATA)),
                                    requestItems,
                                    row.getAttributeValue(MandatoryFieldsFrootyGoogle.PEDIDO),
                                    row.getAttributeValue(MandatoryFieldsFrootyGoogle.OBSERVACOES)
                            ));
                        }

                        SortManager.sortByAlphabeticStoreName(storesRequests);

                        for (ProductRequest storeRequest : storesRequests) {
                            String storeName = storeRequest.getStore().getName().trim();
                            int numberOfSheetsWithSameName = getNumberOfSheetsWithSameName(excelWriter, storeName);

                            if (numberOfSheetsWithSameName > 0) {
                                storeName = String.format("%s (%d)", storeName, numberOfSheetsWithSameName);
                            }

                            XSSFSheet sheet = excelWriter.createSheet(storeName);
                            excelWriter.setCurrentSheet(sheet);
                            writeProductRequestPage(excelWriter, storeRequest);
                            ExcelUtils.autoAdjustColumns(sheet);
                            ExcelUtils.fitToPage(sheet);
                        }
                    }
                } catch (NumberFormatException nfe) {
                    deleteExcelIfExist(directoryPath, finalName);
                    String message = "Erro ao converter. Parece que existe uma linha invisível no arquivo.";
                    throw new Exception(message + "\n\n" + "DICA: Copiar e colar as linhas para uma nova aba, mantendo as formatações.\n\n" + nfe.getMessage());
                } catch (ArrayIndexOutOfBoundsException e) {
                    deleteExcelIfExist(directoryPath, finalName);
                    String message = "Erro ao converter. Verifique se a data está formatada corretamente.\n\nSe você copiou e colou as células por valor, mantenha a formtação da data";
                    throw new Exception(message);
                }
            }

            // GIV
            if (givExcelRows != null && givExcelRows.size() > 0)
            {
                givExcelRows = Filter.filterEquals(MandatoryFieldsFrootyGIV.HAVERA_PEDIDO, "Sim", givExcelRows);

                HashMap<String, ArrayList<RequestItem>> itemsPerClientName = new HashMap<>();

                for (AttributeExcelRow row : givExcelRows)
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

                // We create the client's list of product requests. Each product request is associated to a store
                ArrayList<ProductRequest> clientRequests = getListOfProductRequests(client);
                SortManager.sortByAlphabeticBannerNameAndStoreNameAndOrderNum(clientRequests);
                int i = 1;
                for (ProductRequest productRequest : clientRequests) {
                    String storeName = productRequest.getStore().getName();
                    XSSFSheet clientSheet = excelWriter.createSheet("(" + i++ + ")" + client.getName() + "-" + storeName);
                    excelWriter.setCurrentSheet(clientSheet);
                    writeProductRequestPage(excelWriter, productRequest);
                    ExcelUtils.autoAdjustColumns(excelWriter.getCurrentSheet());
                    ExcelUtils.fitToPage(excelWriter.getCurrentSheet());
                }
            }

            excelWriter.saveFile();
            excelWriter.closeFile();
        } catch (Exception erro) {
            erro.printStackTrace();
            deleteExcelIfExist(directoryPath, finalName);
            throw new Exception(erro.getMessage());
        }
    }

    private void deleteExcelIfExist(String directoryPath, String finalName) {
        String arqPath;
        if (System.getProperty("os.name").equalsIgnoreCase("nix")) {
            arqPath = "/";
        }
        else {
            arqPath = "\\";
        }
        arqPath += finalName;
        File arquivo = new File(directoryPath + arqPath);
        if (arquivo.exists()) {
            arquivo.delete();
        }
    }

    private int getNumberOfSheetsWithSameName(ExcelWriter writer, String sheetName)
    {
        int counter = 0;
        for (XSSFSheet sheet : writer.getAllSheets())
        {
            String name = sheet.getSheetName();
            if (name.contains("("))
            {
                name = name.substring(0, name.indexOf("(") - 1).trim();
            }

            if (name.equals(sheetName))
            {
                counter++;
            }
        }
        return counter;
    }
}
