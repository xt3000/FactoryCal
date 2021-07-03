package net.finch.calendar.Schedules;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import net.finch.calendar.MainActivity;
import net.finch.calendar.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Schedule {
    public static final String U = "U";
    public static final String D = "D";
    public static final String N = "N";
    public static final String S = "S";
    public static final String V = "V";
    public static final String W = "W";

    private String name;
    private String sdl;
    private Map<String, Integer> shiftColors;

    @RequiresApi(api = Build.VERSION_CODES.M)
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
    @RequiresApi(api = Build.VERSION_CODES.M)
    public Map<String, Integer> getDefaultShiftColors() {
        Context ctx = MainActivity.getContext();
        shiftColors = new HashMap<>();

        shiftColors.put("U", ctx.getColor(R.color.U));
        shiftColors.put("D", ctx.getColor(R.color.D));
        shiftColors.put("N", ctx.getColor(R.color.N));
        shiftColors.put("S", ctx.getColor(R.color.S));
        shiftColors.put("V", ctx.getColor(R.color.V));
        shiftColors.put("W", ctx.getColor(R.color.W));

        return shiftColors;
    }

    //*** работа с JSON ***//
    public String serialize() throws JSONException {
        JSONObject shiftColorsObj = new JSONObject();
        shiftColorsObj.put("U", shiftColors.get("U"));
        shiftColorsObj.put("D", shiftColors.get("D"));
        shiftColorsObj.put("N", shiftColors.get("N"));
        shiftColorsObj.put("S", shiftColors.get("S"));
        shiftColorsObj.put("V", shiftColors.get("V"));
        shiftColorsObj.put("W", shiftColors.get("W"));

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("name", name);
        jsonObj.put("sdl", sdl);
        jsonObj.put("shiftColors", shiftColorsObj);

        return  jsonObj.toString();
    }

    public void deserialize(String jsonStr) throws JSONException {
        JSONObject jsonObj = new JSONObject(jsonStr);
        name = jsonObj.getString("name");
        sdl = jsonObj.getString("sdl");
        JSONObject shiftColorsObj = jsonObj.getJSONObject("shiftColors");
        shiftColors = new HashMap<>();
        shiftColors.put("U", shiftColorsObj.getInt("U"));
        shiftColors.put("D", shiftColorsObj.getInt("D"));
        shiftColors.put("N", shiftColorsObj.getInt("N"));
        shiftColors.put("S", shiftColorsObj.getInt("S"));
        shiftColors.put("V", shiftColorsObj.getInt("V"));
        shiftColors.put("W", shiftColorsObj.getInt("W"));
    }
}
