package core.excel.route;

public class DayRange
{
    private final String day;
    private final int start;
    private final int end;

    public DayRange(String day, int start, int end) {
        this.day = day;
        this.start = start;
        this.end = end;
    }

    public String getDay() {
        return day;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return "DayRange{" +
                "day='" + day + '\'' +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
