package net.finch.calendar.Dialogs;

import android.content.Context;
import android.widget.Toast;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import net.finch.calendar.MainActivity;
import net.finch.calendar.SDLEditor.SdlEditorActivity;
import net.finch.calendar.SDLEditor.SdleVM;


public class ColorPiker {
private static boolean change = false;

    public static void Bilder(Context ctx, final String sft, int currentColor) {
        final SdleVM colorsVM = SdlEditorActivity.getSdleVM();

        ColorPickerDialogBuilder
                .with(ctx)
                .setTitle("Выберите цвет")
                .initialColor(currentColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorChangedListener(selectedColor -> {
                    change = true;
//                    Log.d(TAG, "Bilder: colorChanged");
                })
                .setOnColorSelectedListener(selectedColor -> {
//                    Log.d(TAG, "Bilder: ");
                    change = true;
                    toast("onColorSelected: 0x" + Integer.toHexString(selectedColor));
                })
                .setPositiveButton("ok", (dialog, selectedColor, allColors) -> {
//                    Log.d(TAG, "onClick: current = "+currentColor+"; new = "+selectedColor);
                    if (change) {
                        change = false;
                        SdlEditorActivity.isChanged(true);
                        colorsVM.setColor(sft, selectedColor);
                    }
                })
                .setNegativeButton("cancel", (dialog, which) -> change = false)
                .build()
                .show();
    }

    private static void toast(String s) {
        Toast.makeText(MainActivity.getContext(), s, Toast.LENGTH_SHORT).show();
    }

}
