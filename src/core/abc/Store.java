package core.abc;

public class Store
{
    private final String name;
    private String code;
    private Banner banner;
    private int visitPosition;

    public Store(String name, Banner banner)
    {
        this.name = name;
        this.banner = banner;
        this.code = null;
    }

    public Store(String name, String code, Banner banner) {
        this.name = name;
        this.code = code;
        this.banner = banner;
    }

    public Store(String name, int visitPosition)
    {
        this.name = name;
        this.visitPosition = visitPosition;
    }


    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public Banner getBanner() {
        return banner;
    }

    @Override
    public String toString() {
        return "Store{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", banner=" + banner +
                ", visitPosition=" + visitPosition +
                '}';
    }

    public int getVisitPosition() {
        return visitPosition;
    }
}
