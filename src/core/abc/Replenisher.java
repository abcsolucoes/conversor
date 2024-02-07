package core.abc;


public class Replenisher
{
    private final String name;
    private Route route;

    public Replenisher(String name)
    {
        this.name = name;
        this.route = null;
    }

    public String getName() {
        return name;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    @Override
    public String toString() {
        return name +
                "\n" +
                route;
    }
}

