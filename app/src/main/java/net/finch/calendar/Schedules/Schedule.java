package net.finch.calendar.Schedules;

import android.content.Context;
import net.finch.calendar.MainActivity;
import net.finch.calendar.R;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static net.finch.calendar.Schedules.Shift.D;
import static net.finch.calendar.Schedules.Shift.N;
import static net.finch.calendar.Schedules.Shift.S;
import static net.finch.calendar.Schedules.Shift.U;
import static net.finch.calendar.Schedules.Shift.V;
import static net.finch.calendar.Schedules.Shift.W;
import static net.finch.calendar.Schedules.Shift.sftChr;


public class Schedule {
    private int id;
    private String name, sdl;
    private Map<String, Integer> shiftColors;


    public Schedule(JSONObject jsonSdl) throws JSONException {
        deserialize(jsonSdl);
    }

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

    public Schedule(int id, String name, String sdl, Map<String, Integer> shiftColors) {
        this.id = id;
        this.name = name;
        this.sdl = sdl;
        this.shiftColors = shiftColors;
    }



//*** Сеттеры *** //
    public void setId(int id) {
        this.id = id;
    }
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
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getSdl() {
        return sdl;
    }

    public ArrayList<Integer> getSftIdList() {
        ArrayList<Integer> sdlIdList = new ArrayList<>();
        for (Character chr : sdl.toCharArray()) {
            for(int i=0; i<Shift.sftChr.length; i++) {
                if (chr.equals(Shift.sftChr[i])) {
                    sdlIdList.add(i);
                    break;
                }
            }
        }
        return sdlIdList;
    }

    public int getShiftColor(char shift) {
        Integer c = shiftColors.get(Character.toString(shift));
        if (c != null) return c;
        return 0x00000000;
    }
    public Map<String, Integer> getMapSdlColors() {
        return shiftColors;
    }

//*** Цвета смен по умолчанию ***//
    public static Map<String, Integer> getDefaultShiftColors() {
        Context ctx = MainActivity.getContext();
        Map<String, Integer> colors = new HashMap<>();

        colors.put(sftChr[U].toString(), ctx.getColor(R.color.U));
        colors.put(sftChr[D].toString(), ctx.getColor(R.color.D));
        colors.put(sftChr[N].toString(), ctx.getColor(R.color.N));
        colors.put(sftChr[S].toString(), ctx.getColor(R.color.S));
        colors.put(sftChr[V].toString(), ctx.getColor(R.color.V));
        colors.put(sftChr[W].toString(), ctx.getColor(R.color.W));

        return colors;
    }



    //*** работа с JSON ***//
    public JSONObject serialize() throws JSONException {
        JSONObject shiftColorsObj = new JSONObject();
        for (Character sft : sftChr) {
            shiftColorsObj.put(sft.toString(), shiftColors.get(sft.toString()));
        }
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("id", id);
        jsonObj.put("name", name);
        jsonObj.put("sdl", sdl);
        jsonObj.put("shiftColors", shiftColorsObj);

        return  jsonObj;
    }

    public void deserialize(JSONObject jsonObj) throws JSONException {
        setName(jsonObj.getString("name"));
        setSdl(jsonObj.getString("sdl"));
        setId(jsonObj.getInt("id"));
        JSONObject shiftColorsObj = jsonObj.getJSONObject("shiftColors");
        shiftColors = new HashMap<>();
        for (Character sft : sftChr) shiftColors.put(sft.toString(), shiftColorsObj.getInt(sft.toString()));
    }
}
