package net.finch.calendar;

public class SdlListItem {
    private String name;
    private String sdl;

    SdlListItem(String name, String sdl) {
        this.name = name;
        this.sdl = sdl;
    }

    public String getName() {
        return name;
    }

    public String getSdl() {
        return sdl;
    }
}
