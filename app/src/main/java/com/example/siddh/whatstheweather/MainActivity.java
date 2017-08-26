package com.example.siddh.whatstheweather;

import android.content.Context;
import android.hardware.input.InputManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements OnItemSelectedListener {

    EditText cityName;
    TextView resultTextView;
    TextView resultTempTextView;
    String tempSymbol = "°F";
    Spinner mySpinner;

    public void findWeather(View view) {

        Log.i("cityname", cityName.getText().toString());
        DownloadTask task = new DownloadTask();
        try {
            task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + cityName.getText().toString()).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityName.getWindowToken(), 0);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = (EditText) findViewById(R.id.cityName);
        resultTextView = (TextView) findViewById(R.id.resultTextView);
        resultTempTextView = (TextView) findViewById(R.id.resultTempTextView);
        mySpinner = (Spinner) findViewById(R.id.mySpinner);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Temp));
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(spinnerAdapter);

        mySpinner.setOnItemSelectedListener(MainActivity.this);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String selectedTemp = (String) parent.getItemAtPosition(position);
        if(selectedTemp == "°C") {

            tempSymbol = "°C";

        } else {

            tempSymbol = "°F";

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

        tempSymbol = "°C";

    }

    public class DownloadTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {

                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {

                    char current = (char) data;
                    result += current;
                    data = reader.read();

                }

                return result;

            } catch (Exception e) {

                e.printStackTrace();
                return "Failed";

            }


        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {

                String mainMessage = "";
                String tempMessage = "";

                JSONObject jsonObject = new JSONObject(result);
                String weatherInfo = jsonObject.getString("weather");
                Log.i("weather Contents", weatherInfo);

                JSONArray arr = new JSONArray(weatherInfo);

                for(int i = 0; i < arr.length(); i++) {

                    JSONObject jsonPart = arr.getJSONObject(i);

                    String main = "";
                    String description = "";
                    main = jsonPart.getString("main");
                    description = jsonPart.getString("description");

                    if (main != "" && description != "") {

                        mainMessage += main + " : " + description + "\r\n";

                    }

                }

                if (mainMessage != "") {

                    resultTextView.setText(mainMessage);

                } else {

                    Toast.makeText(MainActivity.this, "Could not find weather.", Toast.LENGTH_SHORT).show();

                }

                String mainInfo = jsonObject.getString("main");

                JSONArray mainArray = new JSONArray(mainInfo);

                for(int i = 0; i < mainArray.length(); i++) {

                    JSONObject jsonPart1 = mainArray.getJSONObject(i);

                    String temp = "";
                    String pressure = "";
                    String humidity = "";
                    String minTemp = "";
                    String maxTemp = "";

                    temp = jsonPart1.getString("temp") + " ~F";
                    pressure = jsonPart1.getString("pressure") + " Pa";
                    humidity = jsonPart1.getString("humidity");
                    minTemp = jsonPart1.getString("temp_min") + " ~F";
                    maxTemp = jsonPart1.getString("temp_max") + " ~F";

                    if (temp != "" && pressure != "") {

                        tempMessage += "Temperature : " + temp + "\r\n" + "Pressure : " + pressure + "\r\n" + "Humidity : " + humidity + "\r\n" + "Minimum Temperature : " + minTemp + "\r\n" + "Minimum Temperature : " + minTemp + "\r\n";


                    }

                    if (tempMessage != "") {

                        resultTempTextView.setText(tempMessage);

                    } else {

                        Toast.makeText(MainActivity.this, "Could not find weather.", Toast.LENGTH_SHORT).show();

                    }

                }
                String windInfo = jsonObject.getString("wind");

                JSONArray windArray = new JSONArray(windInfo);

                for(int i = 0; i < windArray.length(); i++) {

                    JSONObject jsonPart2 = windArray.getJSONObject(i);

                    String speed = "";
                    String deg = "";

                    speed = jsonPart2.getString("speed") + " km/h";
                    deg = jsonPart2.getString("deg");


                }

            } catch (JSONException e) {
                e.printStackTrace();
            }



        }
    }
}
