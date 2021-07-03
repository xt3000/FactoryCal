package net.finch.calendar;

public class ParseDate {
    int d;
    int m;
    int y;

    public ParseDate(String date) {
        String[] dt = date.split("\\.");
        d = Integer.parseInt(dt[0]);
        m = Integer.parseInt(dt[1])-1;
        y = Integer.parseInt(dt[2]);
    }

    public int getD() {
        return d;
    }

    public int getM() {
        return m;
    }

    public int getY() {
        return y;
    }
}
