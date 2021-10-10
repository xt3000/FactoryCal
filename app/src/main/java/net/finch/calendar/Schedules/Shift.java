package net.finch.calendar.Schedules;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.HashMap;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.N)
public class Shift {
    private static Map<Character, String> shiftMap;

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
        this.shiftName = shiftNameOf(shift);
        this.prime = false;
    }

    public Shift(int db_id, char shift, String sdlName, int color, boolean prime) {
        shiftMap = initSftMap();
        this.db_id = db_id;
        this.shift = shift;
        this.sdlName = sdlName;
        this.color = color;
        this.shiftName = shiftNameOf(shift);
        this.prime = prime;

    }

    private static Map<Character, String> initSftMap() {
        Map<Character, String> map = new HashMap<>();
        map.put('U', "Утро");
        map.put('D', "День");
        map.put('N', "Ночь");
        map.put('S', "Сут");
        map.put('V', "Вчр");
        map.put('W', "Вых");
        return map;
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

    public String getShiftName() {
        return shiftName;
    }

    public static String shiftNameOf(Character sftChar) {
        shiftMap = initSftMap();
        return shiftMap.getOrDefault(sftChar, "undefined");

    }

    public static Character shiftCharOf(String sftName) {
        shiftMap = initSftMap();
        for (Map.Entry obj : shiftMap.entrySet()) {
            if (sftName.equals(obj.getValue())) return (Character) obj.getKey();
        }
        return null;
    }
}
