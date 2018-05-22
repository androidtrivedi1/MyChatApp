package com.trivediinfoway.mychatapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class RadioButtonActivity extends AppCompatActivity {

    ListView list;
    RadioGroup rg;
    RadioButton r1, r2, r3, r4, r5;
    String[] array_style = {"Default", "STICKY NOTES 1", "STICKY NOTES 2", "STICKY NOTES 3", "STICKY NOTES 4"
            , "STICKY NOTES 5", "STICKY NOTES 6", "STICKY NOTES 7"};

    /* int[] array_style_1 = {getResources().getColor(R.color.color1), getResources().getColor(R.color.color2),
             getResources().getColor(R.color.color3), getResources().getColor(R.color.color4),
             getResources().getColor(R.color.color5)
             , getResources().getColor(R.color.color6),
             getResources().getColor(R.color.color7), getResources().getColor(R.color.color8)};*/
    int[] array_style_1 = {R.color.color1, R.color.color2,
            R.color.color3, R.color.color4,
            R.color.color5};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio_button);
        list = (ListView) findViewById(R.id.list);
        rg = (RadioGroup) findViewById(R.id.rg);
        r1 = (RadioButton) findViewById(R.id.r1);
        r2 = (RadioButton) findViewById(R.id.r2);
        r3 = (RadioButton) findViewById(R.id.r3);
        r4 = (RadioButton) findViewById(R.id.r4);
        r5 = (RadioButton) findViewById(R.id.r5);

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.text, null);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(RadioButtonActivity.this, R.layout.text, R.id.tt, array_style);

        TextView tt = (TextView) view.findViewById(R.id.tt);
        for (int i = 0; i < array_style_1.length; i++)
            tt.setTextColor(array_style_1[i]);

        list.setAdapter(adapter);
        r1.setTextColor(getResources().getColor(R.color.color1));
        r2.setTextColor(getResources().getColor(R.color.color2));
        r3.setTextColor(getResources().getColor(R.color.color3));
        r4.setTextColor(getResources().getColor(R.color.color4));
        r5.setTextColor(getResources().getColor(R.color.color5));

        SharedPreferences settings = getSharedPreferences("demo", 0);
        final int login_preference = settings.getInt("demo", 0);
        rg.check(login_preference);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rb = (RadioButton) rg.findViewById(i);
                SharedPreferences settings = getSharedPreferences("demo", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("demo", i);
                editor.commit();
                rb.setChecked(true);
                rb.getTypeface();
            }
        });
    }
}
