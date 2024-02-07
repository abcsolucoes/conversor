package core.abc;

import core.excel.AttributeExcelRow;
import core.excel.converter.MandatoryFieldsGIV;
import core.excel.converter.MandatoryFieldsDysrup;
import core.excel.converter.RequestItem;
import core.utils.Utils;
import ui.views.converter.ConverterController;

import java.util.ArrayList;
import java.util.HashMap;

public class ProductRequestHandler
{
    /*
     * Gets a list of clients, where each client already has a list of items associated to him
     */
    public static ArrayList<Client> getClientList(ArrayList<AttributeExcelRow> rows)
    {
        HashMap<String, ArrayList<RequestItem>> itemsPerClientName = separateItemsPerClient(rows);

        // Creating a list of clients where each client has a list of requested items
        ArrayList<Client> clientList = new ArrayList<>();
        for (String clientName : itemsPerClientName.keySet())
        {
            ArrayList<RequestItem> clientItems = itemsPerClientName.get(clientName);
            clientList.add(new Client(clientName, clientItems));
        }

        return clientList;
    }

    private static HashMap<String, ArrayList<RequestItem>> separateItemsPerClient(ArrayList<AttributeExcelRow> rows) throws NumberFormatException
    {
        HashMap<String, ArrayList<RequestItem>> itemsPerClientName = new HashMap<>();

        String currentProductName = null;
        int currentRowNumber = -1;

        try {

            // Separating items per client
            if (ConverterController.typeFile.equals(ConverterController.GIV_LABEL_STRING)) {
                for (AttributeExcelRow row : rows) {
                    String productCode = row.getAttributeValue(MandatoryFieldsGIV.CODIGO_PRODUTO).split("-")[0].trim();
                    String productName = row.getAttributeValue(MandatoryFieldsGIV.NOME_PRODUTO);
                    String productBrand = row.getAttributeValue(MandatoryFieldsGIV.MARCA);
                    Product product = new Product(productName, productCode, null, productBrand);

                    currentProductName = row.getAttributeValue(MandatoryFieldsGIV.NOME_PRODUTO);
                    currentRowNumber = row.getRowNumber();
                    int volume = Integer.parseInt(row.getAttributeValue(MandatoryFieldsGIV.QUANTIDADE));

                    String storeName = row.getAttributeValue(MandatoryFieldsGIV.PDV);
                    String storeCode = row.getAttributeValue(MandatoryFieldsGIV.CODIGO_PDV);
                    String bannerName = row.getAttributeValue(MandatoryFieldsGIV.BANDEIRA);
                    Store store = new Store(storeName, storeCode, new Banner(bannerName));

                    String replenisherName = row.getAttributeValue(MandatoryFieldsGIV.NOTIFICANTE);
                    Replenisher replenisher = new Replenisher(replenisherName);

                    String date = row.getAttributeValue(MandatoryFieldsGIV.DATA_E_HORA_PESQUISA);
                    date = Utils.getFormattedDateUSA(date);

                    String orderNum = row.getAttributeValue(MandatoryFieldsGIV.PEDIDO);

                    String comments = row.getAttributeValue(MandatoryFieldsGIV.OBSERVACOES);

                    RequestItem item = new RequestItem(product, volume, store, replenisher,
                            date, orderNum, comments);

                    itemsPerClientName.computeIfAbsent(productBrand, k -> new ArrayList<>());
                    itemsPerClientName.get(productBrand).add(item);
                }
            } else if (ConverterController.typeFile.equals(ConverterController.DYSRUP_LABEL_STRING)) {
                for (AttributeExcelRow row : rows) {
                    String productCodeNetwork = row.getAttributeValue(MandatoryFieldsDysrup.CODIGO_PRODUTO_REDE).split("-")[0].trim();
                    String productCodeIndustry = row.getAttributeValue(MandatoryFieldsDysrup.CODIGO_PRODUTO_INDUSTRIA).split("-")[0].trim();
                    String productName = row.getAttributeValue(MandatoryFieldsDysrup.NOME_PRODUTO);
                    String productBrand = row.getAttributeValue(MandatoryFieldsDysrup.CLIENTE);
                    Product product = new Product(productName, productCodeNetwork, productCodeIndustry, productBrand);

                    currentProductName = row.getAttributeValue(MandatoryFieldsDysrup.NOME_PRODUTO);
                    currentRowNumber = row.getRowNumber();
                    int volume = Integer.parseInt(row.getAttributeValue(MandatoryFieldsDysrup.QUANTIDADE));

                    String storeName = row.getAttributeValue(MandatoryFieldsDysrup.PDV);
                    String storeCode = null;
                    String bannerName = row.getAttributeValue(MandatoryFieldsDysrup.BANDEIRA);
                    Store store = new Store(storeName, storeCode, new Banner(bannerName));

                    String replenisherName = row.getAttributeValue(MandatoryFieldsDysrup.REPOSITOR);
                    Replenisher replenisher = new Replenisher(replenisherName);

                    String[] dataSplit = row.getAttributeValue(MandatoryFieldsDysrup.DATA_E_HORA_PESQUISA).split(" ")[0].split("/");
                    String date = dataSplit[1] + "/" + dataSplit[0] + "/" + dataSplit[2];
                    date = Utils.getFormattedDateUSA(date);

                    String orderNum = row.getAttributeValue(MandatoryFieldsDysrup.PEDIDO);

                    String comments = row.getAttributeValue(MandatoryFieldsDysrup.OBSERVACOES);

                    RequestItem item = new RequestItem(product, volume, store, replenisher,
                            date, orderNum, comments);

                    itemsPerClientName.computeIfAbsent(productBrand, k -> new ArrayList<>());
                    itemsPerClientName.get(productBrand).add(item);
                }
            }
        }
        catch(NumberFormatException nfe) {
            String message = String.format(
                            "\n\nNão foi possível ler a quantidade do produto \n\n\"%s\"\n\n " +
                            "Verifique se a célula de quantidade na linha (%s) possui um valor numérico válido não vazio",
                    currentProductName, currentRowNumber);
            throw new NumberFormatException(message);
        }

        return itemsPerClientName;
    }

    public static ArrayList<ProductRequest> getListOfProductRequests(Client client)
    {
        ArrayList<ProductRequest> listOfProductRequests = new ArrayList<>();

        HashMap<String, ArrayList<RequestItem>> itemsPerStore = separateItemsPerStore(client.getAllRequestedItems());
        for (String orderNum : itemsPerStore.keySet())
        {
            ArrayList<RequestItem> listOfItems = itemsPerStore.get(orderNum);
            RequestItem requestInformation = listOfItems.get(0);

            listOfProductRequests.add(new ProductRequest(
                    requestInformation.getProduct().getBrand(),
                    new Store(requestInformation.getStore().getName(), requestInformation.getStore().getCode(), requestInformation.getStore().getBanner()),
                    requestInformation.getReplenisher(),
                    requestInformation.getDate(),
                    listOfItems,
                    requestInformation.getOrderNum(),
                    requestInformation.getComments()
            ));
        }

        return listOfProductRequests;
    }

    public static HashMap<String, ArrayList<RequestItem>> separateItemsPerStore(ArrayList<RequestItem> allItems)
    {
        HashMap<String, ArrayList<RequestItem>> itemsPerStore = new HashMap<>();

        for (RequestItem item : allItems)
        {
            String storeName = item.getStore().getName();
            itemsPerStore.computeIfAbsent(storeName, s -> new ArrayList<>());
            itemsPerStore.get(storeName).add(item);
        }

        return itemsPerStore;
    }

    public static HashMap<String, ArrayList<RequestItem>> separateItemsPerOrderNumber(ArrayList<RequestItem> allItems)
    {
        HashMap<String, ArrayList<RequestItem>> itemsPerStore = new HashMap<>();

        for (RequestItem item : allItems)
        {
            String orderNumber = item.getOrderNum();
            itemsPerStore.computeIfAbsent(orderNumber, s -> new ArrayList<>());
            itemsPerStore.get(orderNumber).add(item);
        }

        return itemsPerStore;
    }
}
