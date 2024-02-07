package core.excel.converter;

import core.abc.Product;
import core.abc.Replenisher;
import core.abc.Store;

public class RequestItem
{
    private final Product product;
    private final int volume;
    private final Store store;
    private final Replenisher replenisher;
    private final String date;
    private final String orderNum;
    private final String comments;

    public RequestItem(Product product, int volume, Store store, Replenisher replenisher,
                       String date, String orderNum, String comments)
    {
        this.product = product;
        this.volume = volume;
        this.store = store;
        this.replenisher = replenisher;
        this.date = date;
        this.orderNum = orderNum;
        this.comments = comments;
    }

    public Product getProduct() {
        return product;
    }

    public int getVolume() {
        return volume;
    }

    public Store getStore() {
        return store;
    }

    public Replenisher getReplenisher() {
        return replenisher;
    }

    public String getDate() {
        return date;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public String getComments() {
        return comments;
    }

    @Override
    public String toString() {
        return "RequestItem{" +
                "product=" + product +
                ", volume=" + volume +
                ", store=" + store +
                ", replenisher=" + replenisher +
                ", date='" + date + '\'' +
                '}';
    }
}
