package net.finch.calendar.Schedules;

public class Shift {
    private final String shiftName;
    private final char shift;
    private final String sdlName;
    private final int color;

    public Shift(char shift, String sdlName, int color) {
        this.shift = shift;
        this.sdlName = sdlName;
        this.color = color;
        this.shiftName = shiftName(shift);
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

    private String shiftName(Character shiftChar) {
        switch (shiftChar) {
            case 'U':
                return "Утренняя";
            case 'D':
                return "Дневная";
            case 'N':
                return "Ночная";
            case 'S':
                return "Сутки";
            case 'V':
                return "Вечерняя";
            case 'W':
                return "Выходной";
        }
        return "";
    }

    public String getShiftName() {
        return shiftName;
    }
}
