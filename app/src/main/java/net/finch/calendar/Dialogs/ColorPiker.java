package net.finch.calendar.Dialogs;

//import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
//import android.support.annotation.RequiresApi;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorChangedListener;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import net.finch.calendar.MainActivity;
import net.finch.calendar.SDLEditor.SdlEditorActivity;
import net.finch.calendar.SDLEditor.SdleVM;

import static net.finch.calendar.CalendarVM.TAG;


@RequiresApi(api = Build.VERSION_CODES.N)
public class ColorPiker {
private static boolean change = false;

    public static void Bilder(Context ctx, final String sft, int currentColor) {
//        int currentColor = ctx.getColor(R.color.U);
        final SdleVM colorsVM = SdlEditorActivity.getSdleVM();

        ColorPickerDialogBuilder
                .with(ctx)
                .setTitle("Выберите цвет")
                .initialColor(currentColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorChangedListener(selectedColor -> {
                    change = true;
                    Log.d(TAG, "Bilder: colorChanged");
                })
                .setOnColorSelectedListener(selectedColor -> {
                    Log.d(TAG, "Bilder: ");
                    change = true;
                    toast("onColorSelected: 0x" + Integer.toHexString(selectedColor));
                })
                .setPositiveButton("ok", (dialog, selectedColor, allColors) -> {
                    Log.d(TAG, "onClick: current = "+currentColor+"; new = "+selectedColor);
                    if (change) {
                        change = false;
                        SdlEditorActivity.isChanged(true);
                        colorsVM.setColor(sft, selectedColor);
                    }
                })
                .setNegativeButton("cancel", (dialog, which) -> {
                    change = false;
                })
                .build()
                .show();
    }

    private static void toast(String s) {
        Toast.makeText(MainActivity.getContext(), s, Toast.LENGTH_SHORT).show();
    }

}
