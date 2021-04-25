package net.finch.calendar;

public class InfoListItem {
    public String time;
    public String info;

    InfoListItem() {
        this.time = "--:--";
        this.info = "--  --  --  --";
    }

    InfoListItem(String time, String info) {
        this.time = time;
        this.info = info;
    }

    InfoListItem(String info) {
        this.time = "- - : - -";
        this.info = info;
    }



}
