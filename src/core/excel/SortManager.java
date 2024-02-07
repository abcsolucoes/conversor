package core.excel;

import core.abc.*;
import core.excel.converter.RequestItem;
import core.excel.igarape.ClientBlockCells;

import java.util.*;
import java.util.function.Function;

public class SortManager
{
    public static void sortItemsById(ArrayList<RequestItem> items) throws Exception
    {
        try
        {
            items.sort(Comparator.comparingInt(item -> Integer.parseInt(item.getProduct().getName().split("-")[0].trim())));
        }
        catch (NumberFormatException e)
        {
            throw new NumberFormatException(
                    "Algo deu errado ao ordenar os itens do pedido.\n" +
                            "Verifique se todos os produtos possuem ID de ordenação.\n" + e.getMessage());
        }
        catch (Exception e)
        {
            throw new Exception("Algo deu errado ao ordenar os itens do pedido: \n" + e.getMessage());
        }
    }

    public static void sortByAlphabeticStoreName(ArrayList<ProductRequest> pages) throws Exception
    {
        try{
            pages.sort(Comparator.comparing(productRequest -> productRequest.getStore().getName()));
        }
        catch(NumberFormatException e)
        {
            throw new NumberFormatException(
                    "Algo deu errado ao ordenar as páginas por nome alfabético de loja.\n" +
                            "Verifique se todas as lojas possuem nome correto.\n" + e.getMessage());
        }
        catch(Exception e)
        {
            throw new Exception("Algo deu errado ao ordenar as páginas por nome alfabético de loja: \n" + e.getMessage());
        }
    }

    public static void sortByAlphabeticBannerNameAndStoreNameAndOrderNum(ArrayList<ProductRequest> pages) throws Exception
    {
        try{
            Function<ProductRequest, Store> store = ProductRequest::getStore;
            Function<ProductRequest, Banner> banner = store.andThen(Store::getBanner);
            Function<ProductRequest, String> storeName = store.andThen(Store::getName);
            Function<ProductRequest, String> bannerName = banner.andThen(Banner::getName);

            pages.sort(Comparator.comparing(bannerName).thenComparing(storeName).thenComparing(ProductRequest::getOrderNum));
        }
        catch(NumberFormatException e)
        {
            throw new NumberFormatException(
                    "Algo deu errado ao ordenar as páginas por nome alfabético de loja.\n" +
                            "Verifique se todas as lojas possuem nome correto.\n" + e.getMessage());
        }
        catch(Exception e)
        {
            throw new Exception("Algo deu errado ao ordenar as páginas por nome alfabético de loja: \n" + e.getMessage());
        }
    }

    public static void sortClientBlockCellsArraylistByStoreNumber(ArrayList<ClientBlockCells> items) throws Exception {
        try {
            items.sort(Comparator.comparingInt(ClientBlockCells -> Integer.parseInt(ClientBlockCells.getStoreNumber())));
        } catch (NumberFormatException e) {
            throw new NumberFormatException(
                    "Algo deu errado ao ordenar os itens do pedido.\n" +
                            "Verifique se todos os produtos possuem número da loja.\n" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    public static void sortClientsByAlphabeticName(ArrayList<Client> clientsList) throws Exception
    {
        try
        {
            clientsList.sort(Comparator.comparing(Client::getName));
        }
        catch(NumberFormatException e)
        {
            throw new NumberFormatException(
                    "Algo deu errado ao ordenar as abas por nome alfabético de cliente.\n" +
                            "Verifique se todos os clientes possuem nome correto.\n" + e.getMessage());
        }
        catch(Exception e)
        {
            throw new Exception("Algo deu errado ao ordenar as abas por nome alfabético de cliente: \n" + e.getMessage());
        }
    }

    public static void sortReplenishersByAlphabeticName(ArrayList<Replenisher> replenishers)
    {
        replenishers.sort(Comparator.comparing(Replenisher::getName));
    }
}

