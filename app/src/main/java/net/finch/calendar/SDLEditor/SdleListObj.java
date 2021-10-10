package net.finch.calendar.SDLEditor;

import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

public class SdleListObj {
    private String sdlName;
    private String shift;

    public SdleListObj(String shift) {
        this.shift = shift;
    }

    public SdleListObj(String sdlName, String shift) {
        this.sdlName = sdlName;
        this.shift = shift;
    }


//  Getters
    public String getShift() {
        return shift;
    }


    // Setters
    public void setShift(String shift) {
        this.shift = shift;
    }

}
