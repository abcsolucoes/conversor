package core.abc;

import core.excel.converter.RequestItem;

import java.util.ArrayList;

public class Client
{
    private final String name;
    private final ArrayList<RequestItem> allRequestedItems;

    public Client(String name, ArrayList<RequestItem> allRequestedItems)
    {
        this.name = name;
        this.allRequestedItems = allRequestedItems;
    }

    public String getName()
    {
        return name;
    }

    public ArrayList<RequestItem> getAllRequestedItems() {
        return allRequestedItems;
    }

    @Override
    public String toString() {
        return "Client{" +
                "name='" + name + '\'' +
                ", allRequestedItems=" + allRequestedItems +
                '}';
    }
}
