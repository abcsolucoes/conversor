package core.abc.Provider;

public class ProviderItem
{
    private final String code;
    private final String name;
    private final int skuCode;

    public ProviderItem(String code, String name, int skuCode) {
        this.code = code;
        this.name = name;
        this.skuCode = skuCode;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getSkuCode() {
        return skuCode;
    }

    @Override
    public String toString() {
        return "ProviderItem{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", skuCode=" + skuCode +
                '}';
    }
}
