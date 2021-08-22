package net.finch.calendar.Dialogs;

//import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
//import android.support.annotation.RequiresApi;
//import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import net.finch.calendar.MainActivity;
import net.finch.calendar.SDLEditor.SdlEditorActivity;
import net.finch.calendar.SDLEditor.SdleVM;


@RequiresApi(api = Build.VERSION_CODES.N)
public class ColorPiker {


    public static void Bilder(Context ctx, final String sft, int currentColor) {
//        int currentColor = ctx.getColor(R.color.U);
        final SdleVM colorsVM = SdlEditorActivity.getSdleVM();

        ColorPickerDialogBuilder
                .with(ctx)
                .setTitle("Выберите цвет")
                .initialColor(currentColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                        toast("onColorSelected: 0x" + Integer.toHexString(selectedColor));
                    }
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
//                        changeBackgroundColor(selectedColor);
                        colorsVM.setColor(sft, selectedColor);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }

    private static void toast(String s) {
        Toast.makeText(MainActivity.getContext(), s, Toast.LENGTH_SHORT).show();
    }

}
