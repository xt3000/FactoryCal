package net.finch.calendar.Schedules;


import net.finch.calendar.R;


public class Shift {
    public static final int U = 0;//Утренняя
    public static final int D = 1; //Дневная
    public static final int N = 2; //Ночная
    public static final int S = 3; //Сутки
    public static final int V = 4; //Вечерняя
    public static final int W = 5; //Выходной
    public static final int[] sftRes = {R.string.U, R.string.D, R.string.N, R.string.S, R.string.V, R.string.W};
    public static final Character[] sftChr = {'U', 'D', 'N', 'S', 'V', 'W'};

    private final int db_id;
    private final int sftId;
    private final String sdlName;
    private final int color;
    private final boolean prime;


    public Shift(int db_id, int sftId, String sdlName, int color, boolean prime) {
        this.db_id = db_id;
        this.sftId = sftId;
        this.sdlName = sdlName;
        this.color = color;
        this.prime = prime;

    }


    public boolean isPrime() {
        return prime;
    }

    public int getDb_id() {
        return db_id;
    }

    public char getShift() {
        return sftChr[sftId];
    }

    public int getSftId() {
        return sftId;
    }

    public int getColor() {
        return color;
    }

    public String getSdlName() {
        return sdlName;
    }

}







