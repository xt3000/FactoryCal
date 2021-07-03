package net.finch.calendar.Marks;

public class Mark {
    private int db_id;
    private String time;
    private String info;

    public Mark(int db_id, String time, String info) {
        this.db_id = db_id;
        this.time = time;
        this.info = info;
    }

    public String getTime() {
        return time;
    }

    public String getInfo() {
        return info;
    }

    public int getDB_id() {
        return db_id;
    }
}
