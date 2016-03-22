package com.example.donguyen.gasolineconsumption;

import static com.example.donguyen.gasolineconsumption.Constants.FIRST_COLUMN;
import static com.example.donguyen.gasolineconsumption.Constants.SECOND_COLUMN;
import static com.example.donguyen.gasolineconsumption.Constants.THIRD_COLUMN;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
public class ShowDataActivity extends AppCompatActivity {

    private ArrayList<HashMap<String, String>> list;
    private SharedPreferences data;
    private ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data);

        ListView listView=(ListView)findViewById(R.id.listView);
        backBtn = (ImageButton) findViewById(R.id.back);

        list = new ArrayList<>();
        data = getSharedPreferences("Fuel Data", 0);
        backBtn.setBackgroundResource(R.drawable.back_button);

        for (int i = 1; i <= data.getInt("Array_Size", 0); i++) {
            HashMap<String,String> temp = new HashMap<String, String>();
            temp.put(FIRST_COLUMN, data.getString("Date_" + i, "Cannot find data"));
            temp.put(SECOND_COLUMN, data.getString("Odometer_" + i, "Cannot find data"));
            temp.put(THIRD_COLUMN, data.getString("Fuel_" + i, "Cannot find data"));
            list.add(temp);
        }

        ListViewAdapters adapter=new ListViewAdapters(this, list);
        listView.setAdapter(adapter);
    }

    public void backToMain(View v) {
        Intent intent = new Intent(this, DisplayActivity.class);
        startActivity(intent);
    }

    public void resetData(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ShowDataActivity.this);
        builder.setMessage("This action will delete all data has been stored. Continue?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        reset();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.setTitle("Warning!!");
        dialog.show();
    }

    public void reset() {
        SharedPreferences.Editor editor = data.edit();
        for (int i = 1; i <= data.getInt("Array_Size", 0); i++) {
            editor.remove("Date_" + i);
            editor.remove("Odometer_" + i);
            editor.remove("Fuel_" + i);
        }
        editor.remove("Max_Consumption_Per_Day_In_Km");
        editor.remove("Min_Consumption_Per_Day_In_Km");
        editor.remove("Max_Consumption_Per_Day_In_Miles");
        editor.remove("Min_Consumption_Per_Day_In_Miles");
        editor.remove("Latest_Consumption_In_Km");
        editor.remove("Latest_Consumption_In_Miles");
        editor.remove("dataIsStored");
        editor.remove("Array_Size");
        editor.commit();

        Intent intent = new Intent(this, DisplayActivity.class);
        startActivity(intent);
    }
}