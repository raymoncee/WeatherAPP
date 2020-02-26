package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText cityEditTest;
    TextView resultTestView, timeTestView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityEditTest = findViewById(R.id.cityEditTest);
        resultTestView = findViewById(R.id.resultTestView);
        timeTestView = findViewById(R.id.timeTestView);
    }

    public void getWeather(View view){

        try {
            DownloardTask task = new DownloardTask();
            String cityNames = URLEncoder.encode( cityEditTest.getText().toString(), "UTF-8");
            task.execute("http://api.weatherstack.com/current?access_key=42d19cd2d2d4f9d640376b9ca4cfa52d&query=" + cityNames);
            resultTestView.setVisibility(View.VISIBLE);
            timeTestView.setVisibility(View.VISIBLE);
            InputMethodManager methodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            methodManager.hideSoftInputFromWindow(cityEditTest.getWindowToken(), 0);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public class DownloardTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                int data = reader.read();

                while (data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;

            }catch (Exception e){
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("current");
                String weatherInfo1 = jsonObject.getString("location");
                Log.i("WeatherInfo", weatherInfo + weatherInfo1);

                JSONObject currentJsonObject = new JSONObject(weatherInfo + weatherInfo1);
                String temperatureInC = currentJsonObject.getString("temperature") + "°c";
                String time = currentJsonObject.getString("location");

                if(!temperatureInC.equals("")){
                    resultTestView.setText(temperatureInC);
                    timeTestView.setText(time);
                }else {
                    Toast.makeText(MainActivity.this, "Temperature was no find :( ",Toast.LENGTH_SHORT).show();
                }

            }catch (Exception e){
                e.printStackTrace();
                resultTestView.setVisibility(View.INVISIBLE);
                timeTestView.setVisibility(View.INVISIBLE);
                Toast.makeText(MainActivity.this, "The city was Not Found :( ",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
