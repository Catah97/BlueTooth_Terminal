package com.example.martin.bluetooth_terminal.Dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.martin.bluetooth_terminal.R;

/**
 * Created by Martin on 25. 11. 2015.
 */
public class DatabaseCreating {

    public static AlertDialog dialog;

    public static void Dialog (Context context,LayoutInflater inflater){
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.CustomDialog));
        View rootView = inflater.inflate(R.layout.waitdialog_databasecreating, null);
        ImageView loding = (ImageView) rootView.findViewById(R.id.imgConnectLoading);
        Animation anim_loadin = AnimationUtils.loadAnimation(context, R.anim.loading_rotation);
        loding.startAnimation(anim_loadin);
        ImageView loadingrevers = (ImageView) rootView.findViewById(R.id.imgConnectionLoadingRevers);
        Animation anim_loadingrevers = AnimationUtils.loadAnimation(context, R.anim.loading_rotation_reverse);
        loadingrevers.startAnimation(anim_loadingrevers);
        builder.setView(rootView);
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }
}
