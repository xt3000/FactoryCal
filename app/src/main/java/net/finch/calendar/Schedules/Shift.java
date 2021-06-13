package net.finch.calendar.Schedules;

public class Shift {
    private final char shift;
    private final String sdlName;
    private final int color;

    public Shift(char shift, String sdlName, int color) {
        this.shift = shift;
        this.sdlName = sdlName;
        this.color = color;
    }

    public char getShift() {
        return shift;
    }

    public int getColor() {
        return color;
    }

    public String getSdlName() {
        return sdlName;
    }
}
