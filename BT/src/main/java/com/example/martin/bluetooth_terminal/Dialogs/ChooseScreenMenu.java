package com.example.martin.bluetooth_terminal.Dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.martin.bluetooth_terminal.R;


/**
 * Created by Martin on 09.08.2015.
 */
public class ChooseScreenMenu {

    public static AlertDialog MenuDialog(final Context context, final Handler handler, String[] list){
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.DialogTheme));
        MenuListAdapter listAdapter = new MenuListAdapter(new ContextThemeWrapper(context, R.style.DialogTheme),list);
        builder.setAdapter(listAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Message zprava = new Message();
                zprava.arg1 = which;
                handler.sendMessage(zprava);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.TOP | Gravity.RIGHT;

        return dialog;
    }
    public static class MenuListAdapter extends ArrayAdapter<String> {

        private final Context context;
        final String[] list;

        public MenuListAdapter(Context context,String[] mainList) {
            super(context, R.layout.menu_dialog_list_view, mainList);
            this.list = mainList;
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, final ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.menu_dialog_list_view, parent, false);
            TextView text=(TextView)rowView.findViewById(R.id.menuTextView);
            text.setText(list[position]);
            ImageView icon = (ImageView) rowView.findViewById(R.id.imgViewIcon);
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.mipmap.lupa);
            switch (position){
                case 0:
                    bitmap = BitmapFactory.decodeResource(context.getResources(),R.mipmap.lupa);
                    break;
                case 1:
                    bitmap = BitmapFactory.decodeResource(context.getResources(),R.mipmap.setting);
                    break;
            }
            icon.setImageBitmap(bitmap);
            return rowView;
        }
    }
}
