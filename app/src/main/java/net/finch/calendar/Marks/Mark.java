package net.finch.calendar.Marks;

public class Mark {
    public String time;
    public String info;

    public Mark(String time, String info) {
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
