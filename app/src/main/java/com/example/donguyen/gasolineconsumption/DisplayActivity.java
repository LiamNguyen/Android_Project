package com.example.donguyen.gasolineconsumption;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DisplayActivity extends AppCompatActivity {

    private TextView dateView, mainScreenDisp, averageConsumption;
    private Button changeUnitBtn;
    private SharedPreferences data;
    private Double maxConsumptionPerDayInKm;
    private Double minConsumptionPerDayInKm;
    private Double maxConsumptionPerDayInMiles;
    private Double minConsumptionPerDayInMiles;
    private Double lastInKm;
    private Double lastInMiles;
    private Boolean willBeMpg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        dateView = (TextView) findViewById(R.id.dateDisplay);
        mainScreenDisp = (TextView) findViewById(R.id.dispInfo);
        averageConsumption = (TextView) findViewById(R.id.textView);
        changeUnitBtn = (Button) findViewById(R.id.changeUnit);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy h:mm a");
        String formattedDate = df.format(c.getTime());
        dateView.setText(formattedDate);

        data = getSharedPreferences("Fuel Data", 0);
        maxConsumptionPerDayInKm = getDouble("Max_Consumption_Per_Day_In_Km", 0);
        minConsumptionPerDayInKm = getDouble("Min_Consumption_Per_Day_In_Km", 0);
        maxConsumptionPerDayInMiles = getDouble("Max_Consumption_Per_Day_In_Miles", 0);
        minConsumptionPerDayInMiles = getDouble("Min_Consumption_Per_Day_In_Miles", 0);
        lastInKm = getDouble("Latest_Consumption_In_Km" , 0);
        lastInMiles = getDouble("Latest_Consumption_In_Miles", 0);

        if (data.getBoolean("dataIsStored", false)) {
            String btnText = "Change to mpg unit";
            displayByUnit(getDouble("Consumption_In_Liters_Per_100km", 0), maxConsumptionPerDayInKm, minConsumptionPerDayInKm, lastInKm, btnText, false);
        }
    }

    public void inputDataBtn(View v) {
        Intent intent = new Intent(this, InputActivity.class);
        startActivity(intent);
    }

    public void displayDataBtn(View v) {
        Intent intent = new Intent(this, ShowDataActivity.class);
        startActivity(intent);
    }

    public double getDouble(final String key, final double defaultValue) {
        return Double.longBitsToDouble(data.getLong(key, Double.doubleToLongBits(defaultValue)));
    }

    public void changeUnit(View v) {
        if (willBeMpg) {
            String btnText = "Change to mpg unit";
            displayByUnit(getDouble("Consumption_In_Liters_Per_100km", 0), maxConsumptionPerDayInKm, minConsumptionPerDayInKm, lastInKm, btnText, false);
        } else {
            String btnText = "Change to l/100km unit";
            displayByUnit(getDouble("Consumption_In_Miles_Per_Miles", 0), maxConsumptionPerDayInMiles, minConsumptionPerDayInMiles, lastInMiles, btnText, true);
        }
    }

    public Double roundUp(Double number) {
        return Math.round( number * 100.0 ) / 100.0;
    }

    public void displayByUnit(Double average, Double max, Double min, Double last, String text, Boolean unitStatus) {
        if (data.getBoolean("dataIsStored", false)) {
            String stringAverage = roundUp(average).toString();
            String maxMinDisp = "∆ Highest Gasoline Consumption: " + roundUp(max)
                    + "\n∆ Lowest Gasoline Consumption: " + roundUp(min)
                    + "\n∆ Consumption in the last refueling time: " + roundUp(last);
            averageConsumption.setText(stringAverage);
            mainScreenDisp.setText(maxMinDisp);
            changeUnitBtn.setText(text);
            willBeMpg = unitStatus;
        } else {
            String inform = "No data has been stored";
            mainScreenDisp.setText(inform);
        }
    }
}
