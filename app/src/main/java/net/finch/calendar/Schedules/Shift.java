package net.finch.calendar.Schedules;

public class Shift {
    private int db_id;
    private final String shiftName;
    private final char shift;
    private final String sdlName;
    private final int color;
    private boolean prime;

    public Shift(int db_id, char shift, String sdlName, int color) {
        this.db_id = db_id;
        this.shift = shift;
        this.sdlName = sdlName;
        this.color = color;
        this.shiftName = shiftName(shift);
        this.prime = false;
    }

    public Shift(int db_id, char shift, String sdlName, int color, boolean prime) {
        this.db_id = db_id;
        this.shift = shift;
        this.sdlName = sdlName;
        this.color = color;
        this.shiftName = shiftName(shift);
        this.prime = prime;
    }

    public boolean isPrime() {
        return prime;
    }

    public int getDb_id() {
        return db_id;
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
