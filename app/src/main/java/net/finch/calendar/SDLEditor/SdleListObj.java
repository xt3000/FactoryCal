package net.finch.calendar.SDLEditor;

import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

public class SdleListObj {
    private String shift;
    private Integer id;
    private Integer position;

    public SdleListObj(int id, String shift) {
        this.shift = shift;
        this.id = id;
        this.position = 0;
    }

    public SdleListObj(int id, String shift, Integer position) {
        this.shift = shift;
        this.id = id;
        this.position = position;
    }


//  Getters
    public String getShift() {
        return shift;
    }

    public int getId() {
        return id;
    }

    public Integer getPosition() {
        return position;
    }

    // Setters
    public void setShift(String shift) {
        this.shift = shift;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}
