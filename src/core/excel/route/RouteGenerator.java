package core.excel.route;

import core.abc.Replenisher;
import core.abc.Route;
import core.abc.Store;
import core.excel.*;
import core.utils.Utils;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static core.abc.Route.daysOfWeek;
import static core.excel.route.RouteAttributes.ROUTE_ATTRIBUTES;
import static core.utils.Utils.getCurrentDateString;

public class RouteGenerator
{
    public static void generateRoute(ArrayList<ExcelRow> excelRows, String destinationFolderPath) throws Exception
    {
        ExcelRow firstRow = excelRows.get(0);
        if (firstRow.isEmpty()) {
            throw new Exception("Erro ao ler o arquivo. Verifique se a primeira linha possui dados preenchidos.");
        }

        int mondayPosition = firstRow.getValues().indexOf(Route.SEGUNDA);
        int tuesdayPosition = firstRow.getValues().indexOf(Route.TERCA);
        int wednesdayPosition = firstRow.getValues().indexOf(Route.QUARTA);
        int thursdayPosition = firstRow.getValues().indexOf(Route.QUINTA);
        int fridayPosition = firstRow.getValues().indexOf(Route.SEXTA);
        int saturdayPosition = firstRow.getValues().indexOf(Route.SABADO);

        if (mondayPosition == -1 || tuesdayPosition == -1 || wednesdayPosition == -1 ||
                thursdayPosition == -1 || fridayPosition == -1 || saturdayPosition == -1) {
            throw new Exception("Erro ao ler o arquivo!\nAlgum dia da semana está faltando!");
        }

        int finalPosition = saturdayPosition;
        String value = firstRow.getValues().get(saturdayPosition);
        while (!value.trim().isEmpty())
        {
            finalPosition++;
            try {
                value = firstRow.getValues().get(finalPosition);
            }
            catch (IndexOutOfBoundsException e)
            {
                finalPosition--;
                break;
            }
        }

        int finalPosition1 = finalPosition;
        HashMap<String, DayRange> daysRanges = new HashMap<>() {{
            put(Route.SEGUNDA, new DayRange(Route.SEGUNDA, mondayPosition, tuesdayPosition));
            put(Route.TERCA, new DayRange(Route.TERCA, tuesdayPosition, wednesdayPosition));
            put(Route.QUARTA, new DayRange(Route.QUARTA, wednesdayPosition, thursdayPosition));
            put(Route.QUINTA, new DayRange(Route.QUINTA, thursdayPosition, fridayPosition));
            put(Route.SEXTA, new DayRange(Route.SEXTA, fridayPosition, saturdayPosition));
            put(Route.SABADO, new DayRange(Route.SABADO, saturdayPosition, finalPosition1));
        }};

        List<ExcelRow> sliced = excelRows.subList(1, excelRows.size());
        ArrayList<Replenisher> replenishers = setReplenisherRoutes(new ArrayList<>(sliced), daysRanges);
        SortManager.sortReplenishersByAlphabeticName(replenishers);
        String finalPath = generateFilepath(destinationFolderPath);
        ExcelWriter writer = new ExcelWriter(finalPath, "Roteiro");
        writeRoutePage(writer, replenishers);
        ExcelUtils.autoAdjustColumns(writer.getCurrentSheet());
        writer.saveFile();
        writer.closeFile();
    }

    private static String generateFilepath(String saveDirectoryPath) {
        String dateString = getCurrentDateString();
        String finalName = String.format("%s at %s.xlsx", "Roteiro_Involves", dateString);
        return Paths.get(saveDirectoryPath, finalName).toString();
    }

    private static void writeRoutePage(ExcelWriter writer, ArrayList<Replenisher> replenishers)
    {
        // Writing the first row (attribute row)
        String[] attributes = new String[ROUTE_ATTRIBUTES.size()];
        attributes = ROUTE_ATTRIBUTES.toArray(attributes);
        writer.append(writer.cellStyles.get(CellStyles.REGULAR), attributes);

        for (Replenisher replenisher : replenishers)
        {
            ArrayList<String> storesNames = replenisher.getRoute().getAllUniqueStoresNames();
            for (String storeName : storesNames)
            {
                String[] excelLine = generateString(replenisher, storeName);
                writer.append(writer.cellStyles.get(CellStyles.REGULAR), excelLine);
            }
        }
    }

    private static String[] generateString(Replenisher replenisher, String storeName)
    {
        String currentDate = getCurrentDateString().split(" ")[0].replace('-', '/');
        String formattedDate = Utils.getFormattedDateBrazil(currentDate);

        return new String[] {
                "", "", "", replenisher.getName(),
                "", "", "", Utils.normalize(storeName),
                "", "", "", "",
                "", "", "", "",
                "REPETICAO_ORDEM", formattedDate, "", "S", "1",
                replenisher.getRoute().getStorePositionAtDay(storeName, Route.SEGUNDA),
                replenisher.getRoute().getStorePositionAtDay(storeName, Route.TERCA),
                replenisher.getRoute().getStorePositionAtDay(storeName, Route.QUARTA),
                replenisher.getRoute().getStorePositionAtDay(storeName, Route.QUINTA),
                replenisher.getRoute().getStorePositionAtDay(storeName, Route.SEXTA),
                replenisher.getRoute().getStorePositionAtDay(storeName, Route.SABADO),
        };
    }


    private static ArrayList<Replenisher> setReplenisherRoutes(ArrayList<ExcelRow> rows, HashMap<String, DayRange> daysPositions)
    {
        HashMap<String, Route> routePerReplenisher = new HashMap<>();

        // Creating the routes for each replenisher
        for (ExcelRow row : rows)
        {
            String replenisherName = row.getValues().get(0);
            routePerReplenisher.put(replenisherName, new Route());
        }

        for (ExcelRow row : rows)
        {
            String replenisherName = row.getValues().get(0);
            Route route = routePerReplenisher.get(replenisherName);
            if (route != null)
            {
                for (String day : daysOfWeek)
                {
                    ArrayList<Store> storesOfThatDay = getStoresOfCertainDay(row, daysPositions, day);
                    for (Store store : storesOfThatDay)
                    {
                        route.addStoreToDay(day, store);
                    }
                }
            }
        }

        ArrayList<Replenisher> replenishers = new ArrayList<>();
        for (String replenisherName : routePerReplenisher.keySet())
        {
            Replenisher replenisher = new Replenisher(replenisherName);
            replenisher.setRoute(routePerReplenisher.get(replenisherName));
            replenishers.add(replenisher);
        }
        return replenishers;
    }


    private static ArrayList<Store> getStoresOfCertainDay(ExcelRow row, HashMap<String, DayRange> daysPositions, String certainDay)
    {
        ArrayList<Store> stores = new ArrayList<>();
        int startPosition = daysPositions.get(certainDay).getStart();
        int finalPosition = daysPositions.get(certainDay).getEnd();

        int visitPosition = 1;

        // In order to avoid index out of bound exception
        int rowSize = row.getValues().size();
        int mandatoryRowSize = daysPositions.get(Route.SABADO).getEnd();
        int sizeDifference = mandatoryRowSize - rowSize;

        if (sizeDifference > 0)
        {
            ArrayList<String> appendix = new ArrayList<>(sizeDifference);
            for (int i = 0; i < sizeDifference; i++)
            {
                appendix.add("");
            }
            row.getValues().addAll(appendix);
        }

        for (int j = startPosition; j < finalPosition; j++)
        {
            String storeName = row.getValues().get(j);
            if (!storeName.trim().isEmpty())
            {
                stores.add(new Store(storeName, visitPosition));
                visitPosition++;
            }
        }

        return stores;
    }
}

