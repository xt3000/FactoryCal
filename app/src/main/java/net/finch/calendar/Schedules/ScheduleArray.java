package net.finch.calendar.Schedules;

import net.finch.calendar.Schedules.Schedule;

import java.util.ArrayList;

public class ScheduleArray {
    private ArrayList<Schedule> sdlArray;

    public ScheduleArray(){
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
