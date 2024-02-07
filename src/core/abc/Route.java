package core.abc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;


public class Route
{
    public static final String SEGUNDA = "SEGUNDA-FEIRA";
    public static final String TERCA = "TERÇA-FEIRA";
    public static final String QUARTA = "QUARTA-FEIRA";
    public static final String QUINTA = "QUINTA-FEIRA";
    public static final String SEXTA = "SEXTA-FEIRA";
    public static final String SABADO = "SÁBADO";


    public static ArrayList<String> daysOfWeek = new ArrayList<>(Arrays.asList(
            SEGUNDA, TERCA, QUARTA, QUINTA, SEXTA, SABADO
    ));

    private HashMap<String, ArrayList<Store>> storesPerDay;

    public Route()
    {
        this.storesPerDay = new HashMap<>();
        for (String day : daysOfWeek)
        {
            this.storesPerDay.put(day, new ArrayList<>());
        }
    }

    public void addStoreToDay(String day, Store store)
    {
        this.storesPerDay.get(day).add(store);
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        for (String day : daysOfWeek)
        {
            string.append(day);
            string.append("\n");
            string.append("\t");
            string.append(this.storesPerDay.get(day));
            string.append("\n\t");
        }
        return string.toString();
    }

    public ArrayList<String> getAllUniqueStoresNames()
    {
        HashSet<String> uniqueStoresNames = new HashSet<>();
        for (String day : daysOfWeek)
        {
            ArrayList<Store> storesOfThatDay = storesPerDay.get(day);
            for (Store s : storesOfThatDay)
            {
                uniqueStoresNames.add(s.getName());
            }
        }

        return new ArrayList<>(uniqueStoresNames);
    }


    public String getStorePositionAtDay(String storeName, String day)
    {
        for (Store store : storesPerDay.get(day))
        {
            if (store.getName().equals(storeName))
            {
                return String.valueOf(store.getVisitPosition());
            }
        }

        return "";
    }
}
