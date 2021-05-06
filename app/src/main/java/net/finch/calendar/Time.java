package net.finch.calendar;

public class Time {
    final static int NULLTIME = 1500;

    public static int toInt(String strTime) {
        if (strTime.equals("- - : - -")) return NULLTIME;
        String[] t = strTime.split(":");
        int h = Integer.parseInt(t[0]);
        int m = Integer.parseInt(t[1]);

        return (h*60)+m;
    }

    public static String toStr(int intTime) {
        if (intTime == NULLTIME) return "- - : - -";
        int h = intTime/60;
        int m = intTime%60;

        return conv(h) + ":" + conv(m);
    }

    private static String conv(int i) {
        if (i<10) return "0"+i;
        else return ""+i;
    }


}
