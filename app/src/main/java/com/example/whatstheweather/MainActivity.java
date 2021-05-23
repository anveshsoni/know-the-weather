package com.example.whatstheweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    EditText cityName;
    TextView resultText;

    public void findWeather(View view){

        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityName.getWindowToken(),0);
        Log.i("cityName ",cityName.getText().toString());

        try{
            String encodedCityName= URLEncoder.encode(cityName.getText().toString(),"UTF-8");
            DownloadTask  task = new DownloadTask();
            task.execute("http://api.openweathermap.org/data/2.5/weather?q="+encodedCityName+"&appid=6cb43e6a263a6d43cbd581fce74452eb");
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Could not find Weather", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName =(EditText) findViewById(R.id.location);
        resultText=(TextView)findViewById(R.id.reusltText);

    }

    public class DownloadTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {
            String result ="";
            URL url;
            HttpURLConnection urlConnection = null;
            try{
                url = new URL(urls[0]);
                urlConnection=(HttpURLConnection)url.openConnection();

                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data =reader.read();
                while (data!=-1){
                    char current =(char) data;
                    result+= current;
                    data=reader.read();

                }
                return result;
            }catch (Exception e){
                showToast("Could Not Find Weather");

            }

            return null;

        }

        private void showToast(final String text) {
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    resultText.setText("");
                    Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
                }
            }); }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);

            try {
                String message ="";
                JSONObject jsonObject = new JSONObject(result);
                String weatherInfo=jsonObject.getString("weather");
                Log.i("Content",result);
                Log.i("Website Content",weatherInfo);
                JSONObject t = jsonObject.getJSONObject("main");
                String temp = t.getString("temp");

                Log.i("temp",temp);

                JSONArray arr = new JSONArray(weatherInfo);
                for(int i=0;i<arr.length();i++){
                    JSONObject jsonObject1 = arr.getJSONObject(i);

                    String main="";
                    String description="";

                    main = jsonObject1.getString("main");

                    description=jsonObject1.getString("description");
                    if(main!= "" &&description!= "" ){
                        message += "Temprature(in Kelvin):"+temp +"K\nWeather:"+main +"\nDescription:"+description+"\r\n";
                    }
                }
                if(message!=""){
                    resultText.setText(message);
                }else{

                    showToast("Could Not Find Weather");

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}