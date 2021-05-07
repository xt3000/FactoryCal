package net.finch.calendar;

public class InfoListItem {
    public String time;
    public String info;

    InfoListItem(String time, String info) {
        this.time = time;
        this.info = info;
    }

    public String getTime() {
        return time;
    }

    public String getInfo() {
        return info;
    }
}
