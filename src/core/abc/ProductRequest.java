package core.abc;


import core.excel.converter.RequestItem;

import java.util.ArrayList;

public class ProductRequest
{
    public static int code = 0;
    private final String brand;
    private final Store store;
    private final Replenisher replenisher;
    private final String date;
    private final ArrayList<RequestItem> requestItems;
    private final String orderNum;
    private final String comments;

    public ProductRequest(String brand, Store store, Replenisher replenisher, String date,
                          ArrayList<RequestItem> requestItems, String orderNum, String comments)
    {
        this.brand = brand;
        this.store = store;
        this.replenisher = replenisher;
        this.date = date;
        this.requestItems = requestItems;
        this.orderNum = orderNum == null ? null : orderNum.replace(",", "").replace(".", "");
        this.comments = comments;
        ProductRequest.code++;
    }

    public ProductRequest getClone() {
        return new ProductRequest(
                this.brand, this.store,
                this.replenisher,this.date,
                new ArrayList<>(), this.orderNum,
                this.comments
        );
    }

    public String generateTitle()
    {
        return String.format("Solicitação de pedidos - %s", this.store.getBanner().getName());
    }

    public static int getCode() {
        return code;
    }

    public String getBrand() { return brand; }

    public Store getStore() {
        return store;
    }

    public Replenisher getReplenisher() {
        return replenisher;
    }

    public String getDate() {
        return date;
    }

    public ArrayList<RequestItem> getRequestItems() {
        return requestItems;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public String getComments() {
        return comments;
    }

    @Override
    public String toString() {
        return "ProductRequest{" +
                "store=" + store +
                ", replenisher=" + replenisher +
                ", date='" + date + '\'' +
                ", requestItems=" + requestItems +
                '}';
    }
}
