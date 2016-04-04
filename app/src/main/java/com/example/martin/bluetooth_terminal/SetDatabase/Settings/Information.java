package com.example.martin.bluetooth_terminal.SetDatabase.Settings;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.martin.bluetooth_terminal.Other.Konstanty;
import com.example.martin.bluetooth_terminal.R;

/**
 * Created by Martin on 11. 1. 2016.
 * Univerzální třida, kde se vypisují informace k nastavení jednotlyvých informací o položkách
 * informace o tom s jakou položkou pracuje získává v intentu
 */
public class Information extends AppCompatActivity {

    String item,itemText;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);                  /**zobrazí iconu sipku zpet*/
        actionBar.setHomeButtonEnabled(true);                       /**nastaví iconu eneble pro click*/
        actionBar.setTitle("Informace pro "+item);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.animator.set_control_left_in,R.animator.set_control_left_out);
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            finish();
            overridePendingTransition(R.animator.set_control_left_in,R.animator.set_control_left_out);
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ImageView img = (ImageView) findViewById(R.id.imgInformation);
        TextView txt = (TextView) findViewById(R.id.textInformation);
        item = getIntent().getExtras().getString("item");

        if (item.equals(Konstanty.BUTTON)){
            item = "Tlačítko";
            itemText = getString(R.string.buttonInformation);
            img.setImageResource(R.drawable.information_buttton);

        }
        if (item.equals(Konstanty.SWITCH)){
            item = "Přepínač";
            itemText = getString(R.string.switchInformation);
            img.setImageResource(R.drawable.information_switch);
        }
        if (item.equals(Konstanty.SEEK_BAR)){
            item = "Posuvník";
            itemText = getString(R.string.seekBarInformation);
            img.setImageResource(R.drawable.information_seekbar);
        }
        if (item.equals(Konstanty.POZNAMKY)){
            item = "Poznámku";
            itemText = getString(R.string.poznamkyInformation);
            img.setImageResource(R.drawable.information_poznamka);
        }
        if (item.equals(Konstanty.PLYN)){
            item = "Plyn";
            itemText = getString(R.string.plynInfomation);
            img.setImageResource(R.drawable.information_plyn);
        }
        if (item.equals(Konstanty.VOLANT)){
            item = "Volant";
            itemText = getString(R.string.volantInformation);
            img.setImageResource(R.drawable.information_volant);
        }
        txt.setText(itemText);
    }
}
