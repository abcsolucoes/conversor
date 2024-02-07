package core.abc.Provider;

import java.util.ArrayList;

public class ProviderInfo
{
    private final String name;
    private final String code;
    private final ArrayList<ProviderItem> items;

    public ProviderInfo(String name, String code, ArrayList<ProviderItem> items)
    {
        this.name = name;
        this.code = code;
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public ArrayList<ProviderItem> getItems() {
        return items;
    }

    @Override
    public String toString() {
        StringBuilder items = new StringBuilder();
        for (ProviderItem item : this.items)
        {
            items.append(item).append("\n");
        }
        return "ProviderInfo{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' + "\n" +
                ", items=" + items +
                '}';
    }
}
