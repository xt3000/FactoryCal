package net.finch.calendar;

public class MarkItem {
    int time;
    String info;

    MarkItem(int time, String info) {
        this.time = time;
        this.info = info;
    }

    public int getTime() {
        return time;
    }

    public String getInfo() {
        return info;
    }
}
