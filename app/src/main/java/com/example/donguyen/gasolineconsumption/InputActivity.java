package com.example.donguyen.gasolineconsumption;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class InputActivity extends AppCompatActivity {

    private EditText dateTxtField, odometerTxtField, amountTxtField;
    private TextView alert;
    private ArrayList<String> arrDate = new ArrayList<>();
    private ArrayList<String> arrOdometer = new ArrayList<>();
    private ArrayList<String> arrFuelAmount = new ArrayList<>();
    public static String dataStoringFile = "Fuel Data";
    public SharedPreferences data;
    private Boolean activityIsCreatedAgain;
    private int arraySize = 0;
    private Boolean dateAlreadyHasValue;
    private Boolean wrongOdometerInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        dateTxtField = (EditText) findViewById(R.id.dateTxtField);
        odometerTxtField = (EditText) findViewById(R.id.odometerTxtField);
        amountTxtField = (EditText) findViewById(R.id.amountTxtField);
        ImageButton backBtn = (ImageButton) findViewById(R.id.back);
        alert = (TextView) findViewById(R.id.textView2);
        alert.setVisibility(View.GONE);

        backBtn.setBackgroundResource(R.drawable.back_button);
        data = getSharedPreferences(dataStoringFile, 0);

        activityIsCreatedAgain = true;
        dateAlreadyHasValue = false;
        wrongOdometerInput = false;
    }

    public void submitData(View v) {
        for (int i = 1; i <= data.getInt("Array_Size", 0); i++) {
            int savedOdometer = Integer.parseInt(data.getString("Odometer_" + i, "0"));
            int newOdometer = Integer.parseInt(odometerTxtField.getText().toString());
            if (data.getString("Date_" + i, "Cannot find data").equals(dateTxtField.getText().toString())) {
                dateAlreadyHasValue = true;
            }

            if (newOdometer <= savedOdometer) {
                wrongOdometerInput = true;
            }
        }
        if (dateTxtField.getText().toString().isEmpty()) {
            alertOperation("Please input date!", "red");
        } else if (odometerTxtField.getText().toString().isEmpty()) {
            alertOperation("Please input odometer value!", "red");
        } else if (amountTxtField.getText().toString().isEmpty()) {
            alertOperation("Please input fuel amount!", "red");
        } else if (dateAlreadyHasValue) {
            alertOperation("Data on this date has already existed!", "red");
            dateAlreadyHasValue = false;
        } else if (wrongOdometerInput) {
            alertOperation("You may have input odometer value less than or equal before!", "red");
            wrongOdometerInput = false;
        } else {
            alertOperation("Data has been successfully added.", "green");
            SharedPreferences.Editor editor = data.edit();
            editor.putBoolean("dataIsStored", true);

            String date = dateTxtField.getText().toString();
            String odometer = odometerTxtField.getText().toString();
            String fuelAmount = amountTxtField.getText().toString();
            arrDate.add(date);
            arrOdometer.add(odometer);
            arrFuelAmount.add(fuelAmount);

            if (activityIsCreatedAgain) {
                arraySize = data.getInt("Array_Size", 0);
                activityIsCreatedAgain = false;
            }

            editor.putInt("Array_Size", (arraySize + arrDate.size())); // = 1
            editor.putString("Date_" + (arraySize + arrDate.size()), arrDate.get(arrDate.size() - 1));
            editor.putString("Odometer_" + (arraySize + arrOdometer.size()), arrOdometer.get(arrOdometer.size() - 1));
            editor.putString("Fuel_" + (arraySize + arrFuelAmount.size()), arrFuelAmount.get(arrFuelAmount.size() - 1));
            editor.commit();

            Double totalFuelAmount = 0.0;
            for (int i = 1; i <= data.getInt("Array_Size", 0) - 1; i++) {
                totalFuelAmount += Double.parseDouble(data.getString("Fuel_" + i, "0"));
            }
            int distanceTraveled = Integer.parseInt(data.getString("Odometer_" + data.getInt("Array_Size", 0), "0")) -
                    Integer.parseInt(data.getString("Odometer_" + 1, "0"));

            Double averageConsumptionInKm = 100 * totalFuelAmount / distanceTraveled;
            Double averageConsumptionInMiles = (distanceTraveled * 0.621371192) / (totalFuelAmount * 0.264172052);

            editor.putLong("Consumption_In_Liters_Per_100km", Double.doubleToRawLongBits(averageConsumptionInKm));
            editor.putLong("Consumption_In_Miles_Per_Miles", Double.doubleToRawLongBits(averageConsumptionInMiles));
            editor.commit();

            ArrayList<Integer> arrOdometerValue = new ArrayList<>();
            ArrayList<Integer> arrDistanceTraveledByDay = new ArrayList<>();
            ArrayList<Double> arrRefuelAmount = new ArrayList<>();

            for (int i = 1; i <= data.getInt("Array_Size", 0); i++) {
                arrOdometerValue.add(Integer.parseInt(data.getString("Odometer_" + i, "0")));
                arrRefuelAmount.add(Double.parseDouble(data.getString("Fuel_" + i, "0")));
            }

            if (arrOdometerValue.size() > 1) {
                for (int i = 0; i < arrOdometerValue.size(); i++) {
                    int distanceTraveledByDay;
                    if (i > 0) {
                        distanceTraveledByDay = arrOdometerValue.get(i) - arrOdometerValue.get(i - 1);
                        arrDistanceTraveledByDay.add(distanceTraveledByDay);
                    }
                }
            }

            if (arrDistanceTraveledByDay.size() != 0) {
                ArrayList<Double> arrConsumptionPerDayInKm = new ArrayList<>();
                ArrayList<Double> arrConsumptionPerDayInMiles = new ArrayList<>();
                for (int i = 0; i < arrDistanceTraveledByDay.size(); i++) {
                    double consumptionPerDayInKm, consumptionPerDayInMiles;
                    consumptionPerDayInKm = 100 * arrRefuelAmount.get(i) / arrDistanceTraveledByDay.get(i);
                    consumptionPerDayInMiles = arrDistanceTraveledByDay.get(i) * 0.621371192 / (arrRefuelAmount.get(i) * 0.264172052);
                    arrConsumptionPerDayInKm.add(consumptionPerDayInKm);
                    arrConsumptionPerDayInMiles.add(consumptionPerDayInMiles);
                }

                Double maxConsumptionPerDayInKm = arrConsumptionPerDayInKm.get(0);
                Double minConsumptionPerDayInKm = arrConsumptionPerDayInKm.get(0);
                Double maxConsumptionPerDayInMiles = arrConsumptionPerDayInMiles.get(0);
                Double minConsumptionPerDayInMiles = arrConsumptionPerDayInMiles.get(0);
                Double latestConsumptionInKm = arrConsumptionPerDayInKm.get(arrConsumptionPerDayInKm.size() - 1);
                Double latestConsumptionInMiles = arrConsumptionPerDayInMiles.get(arrConsumptionPerDayInMiles.size() - 1);

                for (int i = 0; i < arrConsumptionPerDayInKm.size(); i++) {
                    Double consumptionPerDayInKm = arrConsumptionPerDayInKm.get(i);
                    Double consumptionPerDayInMiles = arrConsumptionPerDayInMiles.get(i);

                    if (consumptionPerDayInKm > maxConsumptionPerDayInKm) {
                        maxConsumptionPerDayInKm = consumptionPerDayInKm;
                    }

                    if (consumptionPerDayInKm < minConsumptionPerDayInKm) {
                        minConsumptionPerDayInKm = consumptionPerDayInKm;
                    }

                    if (consumptionPerDayInMiles > maxConsumptionPerDayInMiles) {
                        maxConsumptionPerDayInMiles = consumptionPerDayInMiles;
                    }

                    if (consumptionPerDayInMiles < minConsumptionPerDayInMiles) {
                        minConsumptionPerDayInMiles = consumptionPerDayInMiles;
                    }
                }

                editor.putLong("Max_Consumption_Per_Day_In_Km", Double.doubleToRawLongBits(maxConsumptionPerDayInKm));
                editor.putLong("Min_Consumption_Per_Day_In_Km", Double.doubleToRawLongBits(minConsumptionPerDayInKm));
                editor.putLong("Max_Consumption_Per_Day_In_Miles", Double.doubleToRawLongBits(maxConsumptionPerDayInMiles));
                editor.putLong("Min_Consumption_Per_Day_In_Miles", Double.doubleToRawLongBits(minConsumptionPerDayInMiles));
                editor.putLong("Latest_Consumption_In_Km", Double.doubleToRawLongBits(latestConsumptionInKm));
                editor.putLong("Latest_Consumption_In_Miles", Double.doubleToRawLongBits(latestConsumptionInMiles));
                editor.commit();
            }
        }
    }

    public void alertOperation(String alertSentence, String color) {
        alert.setVisibility(View.VISIBLE);
        alert.setText(alertSentence);
        if (color.equals("green")) {
            alert.setTextColor(Color.GREEN);
        } else {
            alert.setTextColor(Color.RED);
        }
    }

    public void backToMain(View v) {
        Intent intent = new Intent(this, DisplayActivity.class);
        startActivity(intent);
    }
}
