package yuku.ambilwarna;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.View;

/**
 * Created by Martin on 8. 2. 2016.
 */
public class WaringDialog {
    public static void Dialog (Context context, final Handler handler,String text){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Varování");
        builder.setMessage(text);
        builder.setPositiveButton("Ano", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Message msg = new Message();
                msg.arg1 = 1;
                handler.sendMessage(msg);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Ne", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Message msg = new Message();
                msg.arg1 = 0;
                handler.sendMessage(msg);
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        try {
            int dividerID = dialog.getContext().getResources().getIdentifier("titleDivider", "id", "android");
            View titleDivider = dialog.getWindow().getDecorView().findViewById(dividerID);
            titleDivider.setBackgroundColor(Color.BLACK); // change divider color
        }
        catch (Exception ignore){
        }
    }
}