package net.finch.calendar;

import java.util.ArrayList;
import java.util.List;

public class ScheduleArray {
    ArrayList<Schedule> sdlArray;

    ScheduleArray(){
        sdlArray = new ArrayList<>();
    }

    public void add(Schedule sdl) {
        sdlArray.add(sdl);
    }

    public ArrayList<String> getNames() {
        ArrayList<String> list = new ArrayList<>();

        for (Schedule sdl : sdlArray) {
            list.add(sdl.getName());
        }
        return list;
    }

    public Schedule get(int id) {
        return sdlArray.get(id);
    }
}
