package net.finch.calendar.Schedules;

import android.content.Context;
import android.os.Build;
//import android.support.annotation.RequiresApi;
import android.util.Log;

import androidx.annotation.RequiresApi;

import net.finch.calendar.MainActivity;
import net.finch.calendar.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Schedule {
    public static final int U = 0;//Утренняя
    public static final int D = 1; //Дневная
    public static final int N = 2; //Ночная
    public static final int S = 3; //Сутки
    public static final int V = 4; //Вечерняя
    public static final int W = 5; //Выходной

    public static final String[] SHIFTS = {"U", "D", "N", "S", "V", "W"};

    private String name, sdl;
    private Map<String, Integer> shiftColors;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Schedule(String name, String sdl) {
        this.name = name;
        this.sdl = sdl;
        this.shiftColors = getDefaultShiftColors();
    }

    public Schedule(String name, String sdl, Map<String, Integer> shiftColors) {
        this.name = name;
        this.sdl = sdl;
        this.shiftColors = shiftColors;
    }

//*** Сеттеры *** //
    public void addShift(char sft) {
        sdl += sft;
    }
    public void addShift(String sft) {
        sdl += sft;
    }

    public void delShift(int pos){
        sdl = sdl.substring(0, pos) + sdl.substring(pos + 1);
    } //отсчёт pos с 0!!

    public void setSdl(String sdl) {
        this.sdl = sdl;
    }
    public void setShiftColors(Map<String, Integer> shiftColors) {
        this.shiftColors = shiftColors;
    }
    public void setName(String name) {
        this.name = name;
    }
    //*** Геттеры ***//

    public String getName() {
        return name;
    }

    public String getSdl() {
        return sdl;
    }

    public int getShiftColor(char shift) {
        return shiftColors.get(Character.toString(shift));
    }

//*** Цвета смен по умолчанию ***//
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Map<String, Integer> getDefaultShiftColors() {
        Context ctx = MainActivity.getContext();
        Map<String, Integer> colors = new HashMap<>();


        colors.put(SHIFTS[U], ctx.getColor(R.color.U));
        colors.put(SHIFTS[D], ctx.getColor(R.color.D));
        colors.put(SHIFTS[N], ctx.getColor(R.color.N));
        colors.put(SHIFTS[S], ctx.getColor(R.color.S));
        colors.put(SHIFTS[V], ctx.getColor(R.color.V));
        colors.put(SHIFTS[W], ctx.getColor(R.color.W));

        return colors;
    }

    public static String getNextSft(String sft) {
        for (int i=0; i<SHIFTS.length; i++) {
            if (sft.equals(SHIFTS[i])) {
                if (i == SHIFTS.length-1) return SHIFTS[0];
                return SHIFTS[i+1];
            }
        }

        return "";
    }

    //*** работа с JSON ***//
    public JSONObject serialize() throws JSONException {
        JSONObject shiftColorsObj = new JSONObject();
        for (String sft : SHIFTS) {
            shiftColorsObj.put(sft, shiftColors.get(sft));
        }
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("name", name);
        jsonObj.put("sdl", sdl);
        jsonObj.put("shiftColors", shiftColorsObj);

        return  jsonObj;
    }

    public void deserialize(String jsonStr) throws JSONException {
        JSONObject jsonObj = new JSONObject(jsonStr);
        name = jsonObj.getString("name");
        sdl = jsonObj.getString("sdl");
        JSONObject shiftColorsObj = jsonObj.getJSONObject("shiftColors");
        shiftColors = new HashMap<>();
        for (String sft : SHIFTS) shiftColors.put(sft, shiftColorsObj.getInt(sft));
    }
}
