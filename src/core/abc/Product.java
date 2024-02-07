package core.abc;

public class Product
{
    private final String name;
    private final String networkCode;
    private final String industryCode;
    private final String brand;

    public Product(String name, String networkCode, String industryCode, String brand)
    {
        this.name = name;
        this.networkCode = networkCode;
        this.industryCode = industryCode;
        this.brand = brand;
    }

    public String getName() {
        return name;
    }

    public String getNetworkCode() {
        return networkCode;
    }

    public String getIndustryCode() { return industryCode; }

    public String getBrand() {
        return brand;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", networkCode='" + networkCode + '\'' +
                ", industryCode='" + industryCode + '\'' +
                ", brand='" + brand + '\'' +
                '}';
    }
}

