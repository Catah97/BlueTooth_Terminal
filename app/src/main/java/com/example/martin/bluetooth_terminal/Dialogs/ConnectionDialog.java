package com.example.martin.bluetooth_terminal.Dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.martin.bluetooth_terminal.BlueTooth.BlueToothConnection;
import com.example.martin.bluetooth_terminal.R;

/**
 * Created by Martin on 18.08.2015.
 */
public class ConnectionDialog {
    public static AlertDialog LoadingDialog(String message, LayoutInflater inflater, Context context,final BlueToothConnection connection) {                      /**posila jmeno za��zen� na kter� se p�ipojuje*/
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.CustomDialog));
        View rootView = inflater.inflate(R.layout.waitdialog_connection, null);
        TextView text = (TextView) rootView.findViewById(R.id.txtSetDialog);
        text.setText(message);
        builder.setNegativeButton("Zrušit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                connection.cancel(true);
                dialog.dismiss();
            }
        });
        builder.setView(rootView);
        ImageView loding = (ImageView) rootView.findViewById(R.id.imgConnectLoading);
        Animation anim_loadin = AnimationUtils.loadAnimation(context, R.anim.loading_rotation);
        loding.startAnimation(anim_loadin);
        ImageView loadingrevers = (ImageView) rootView.findViewById(R.id.imgConnectionLoadingRevers);
        Animation anim_loadingrevers = AnimationUtils.loadAnimation(context, R.anim.loading_rotation_reverse);
        loadingrevers.startAnimation(anim_loadingrevers);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false); /**nastavi dialog tak ze se nevypne pokod je kliknuto mimo nej*/
        return dialog;
    }
}
