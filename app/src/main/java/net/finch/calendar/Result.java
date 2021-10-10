package net.finch.calendar;

import com.google.gson.internal.$Gson$Preconditions;

public class Result {
    public static final String OK = "ok";                                           // ok
    public static final String NAME_USES = "the name is already in use";            // the name is already in use
    public static final String ERR = "error";                                       //  error

    private String res;

    public Result(String res) {
        this.res = res;
    }

    public String getRes() {
        return res;
    }
}
