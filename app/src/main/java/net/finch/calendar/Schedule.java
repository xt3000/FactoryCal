package net.finch.calendar;

public class Schedule {
    private String name;
    private String sdl;

    Schedule(String name, String sdl) {
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
