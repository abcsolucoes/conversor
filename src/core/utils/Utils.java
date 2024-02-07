package core.utils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils
{
    public static String getCurrentDateString()
    {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");
        Date date = new Date();
        return formatter.format(date);
    }

    public static String getFormattedDateUSA(String date)
    {
        String[] newData = date.split("/");
        String month = newData[0];
        String day = newData[1];
        String year = newData[2];

        if (day.length() == 1)
        {
            day = "0" + day;
        }

        if (month.length() == 1)
        {
            month = "0" + month;
        }

        if (year.length() == 2)
        {
            year = "20" + year;
        }

        return String.format("%s/%s/%s", day, month, year);
    }


    public static String getFormattedDateBrazil(String date)
    {
        String[] newDate = date.split("/");
        String day = newDate[0];
        String month = newDate[1];
        String year = newDate[2];

        if (day.length() == 1)
        {
            day = "0" + day;
        }

        if (month.length() == 1)
        {
            month = "0" + month;
        }

        if (year.length() == 2)
        {
            year = "20" + year;
        }

        return String.format("%s/%s/%s", day, month, year);
    }




    /**
     * Given a string with non-ascii characters, normalizes it into ASCII
     */
    private static String toASCII(String s)
    {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
    }

    /**
     * Normalizes the string, removing all accents and trailing whitespaces.
     *
     * @return normalized string
     */
    public static String normalize(String s)
    {
        return toASCII(s.trim());
    }


    /**
     * Checks whether a string a numeric
     */
    public static boolean isNumeric(String strNum)
    {
        if (strNum == null)
        {
            return false;
        }
        try
        {
            Double.parseDouble(strNum);
        }
        catch (NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

    public static boolean folderExist(String path)
    {
        return Files.isDirectory(Paths.get(path));
    }
}
