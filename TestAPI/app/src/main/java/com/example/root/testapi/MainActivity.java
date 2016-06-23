package com.example.root.testapi;

import android.app.Activity;
import android.renderscript.Double2;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity  implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private TextView textNegara,textDaerah,textCuaca,textSuhu,textKelembapan;
    private Button butRefresh;
    private Spinner spinCount,spinCity;
    private static String TAG = MainActivity.class.getSimpleName();
    public String city,country,countCode;
    public int countNumb;
    public List<String > listCity = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textNegara = (TextView)findViewById(R.id.negara);
        textDaerah = (TextView)findViewById(R.id.daerah);
        textCuaca = (TextView)findViewById(R.id.cuaca);
        textSuhu = (TextView)findViewById(R.id.suhu);
        textKelembapan = (TextView)findViewById(R.id.kelembapan);

        spinCount = (Spinner)findViewById(R.id.spinner);
        spinCity = (Spinner)findViewById(R.id.spinner2);

        butRefresh = (Button)findViewById(R.id.refresh);

        spinCount.setOnItemSelectedListener(this);
        spinCity.setOnItemSelectedListener(this);

        // nggone country
        final List<String > listNegara = new ArrayList<>();
        listNegara.add("Select your country");

        // mengambil data negara
        takeCountryList();

        ArrayAdapter<String> dataAdaptA = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, listNegara);

        dataAdaptA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinCount.setAdapter(dataAdaptA);

        // nggone city
//        List<String > listCity = new ArrayList<>();
        listCity.add("Select your city");

        ArrayAdapter<String> dataAdaptB = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, listCity);

        dataAdaptA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinCity.setAdapter(dataAdaptB);

    }

    private void takeCountryList() {
        //appending offset to url
        String urlCountry = "https://gist.githubusercontent.com/keeguon/2310008/raw/bdc2ce1c1e3f28f9cab5b4393c7549f38361be4e/countries.json";

        //Volley's json array request object
//        JSONArray Response = new JSONArray();

        JsonArrayRequest req = new JsonArrayRequest(urlCountry,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        try {
                            for (int i = 0; i<response.length(); i++){
                                JSONObject datCount = (JSONObject) response.get(i);
                                String namaNegara = datCount.optString("name");
                                if (namaNegara.equals("")){
                                    listNegara.add("kosongbro" + i );
                                }else{
                                    listNegara.add(namaNegara);
                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyLog.d(TAG, "Error: " + volleyError.getMessage());
                Toast.makeText(getApplicationContext(),
                        volleyError.getMessage(), Toast.LENGTH_SHORT).show();
//                Log.i("cher error ", "Server Error: " + volleyError.getMessage());
            }
        });
        weathApplication.getInstance().addToRequestQueue(req);

    }


    private void fetching() {
        String url = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "," + countCode + "&appid=258f9289b9828f472e591493d5028386";

        JSONObject response = new JSONObject();

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,url,response,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        //mengambil data negara
                        JSONObject sys = null;
                        try {
                            sys = response.getJSONObject("sys");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String datNeg = sys.optString("country");
                        // set data negara ke textview
                        textNegara.setText(datNeg);

                        // mengambil data daerah
                        String datDaer = response.optString("name");
                        // set data daerah ke textview
                        textDaerah.setText(datDaer);

                        // mengambil data cuaca
                        JSONArray weather = null;
                        try {
                            weather = response.getJSONArray("weather");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        JSONObject descipt = null;
                        try {
                            descipt = weather.getJSONObject(0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        String datCuaca = descipt.optString("description");
                        //set data cuaca ke textview
                        textCuaca.setText(datCuaca);

                        //mengambil data suhu dan kelembapan
                        JSONObject main = null;
                        try {
                            main = response.getJSONObject("main");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        String datSuhuKel = main.optString("temp");
                        String datKelem = main.optString("humidity");

                        double datSuhuCel = Double.parseDouble(datSuhuKel);
                        double datSuhu = (datSuhuCel)-(273.15);
                        DecimalFormat df = new DecimalFormat("#.##");

                        String datSuhuStr = Double.toString(Double.parseDouble(df.format(datSuhu)));



                        //set data suhu dan kelembapan ke textview
                        textSuhu.setText(datSuhuStr + "°C");


                        textKelembapan.setText(datKelem + " %");

                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error : " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

        weathApplication.getInstance().addToRequestQueue(req);

    }

    @Override
    public void onClick(View v) {
        fetching();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner spinner = (Spinner) parent;
        if(spinner.getId() == R.id.spinner)
        {
            country = parent.getItemAtPosition(position).toString();
            countNumb = parent.getSelectedItemPosition();
            fetchCity();
            takeCountryCode();

        }
        else if(spinner.getId() == R.id.spinner2)
        {
            city = parent.getItemAtPosition(position).toString();


        }
    }

    private void takeCountryCode() {

        //appending offset to url
        String urlCountry = "https://gist.githubusercontent.com/keeguon/2310008/raw/bdc2ce1c1e3f28f9cab5b4393c7549f38361be4e/countries.json";

        //Volley's json array request object
//        JSONArray Response = new JSONArray();

        JsonArrayRequest req = new JsonArrayRequest(urlCountry,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        try {

                                JSONObject datCount = (JSONObject) response.get(countNumb);
                                countCode = datCount.optString("code");


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyLog.d(TAG, "Error: " + volleyError.getMessage());
                Toast.makeText(getApplicationContext(),
                        volleyError.getMessage(), Toast.LENGTH_SHORT).show();
//                Log.i("cher error ", "Server Error: " + volleyError.getMessage());
            }
        });
        weathApplication.getInstance().addToRequestQueue(req);

    }

    private void fetchCity() {

        listCity.clear();
        listCity.add("Select your city");

        String urlCity = "https://raw.githubusercontent.com/Sulistyarif/androidProjects/master/city.json";

        String urlTest = "http://api.5min.com/search/funny/videos.Json?num_of_videos=1&page=1&show_renditions=true";

        JSONObject responsd = new JSONObject();

        JsonObjectRequest yason = new JsonObjectRequest(Request.Method.GET,urlCity,responsd,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject object) {
                        try{
                            String data = object.optString(country);
                            JSONArray cityArray = new JSONArray(data);

                            for (int i = 0; i<cityArray.length(); i++) {
                                String tambahCity = cityArray.optString(i);
                                listCity.add(tambahCity);
                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        );

        weathApplication.getInstance().addToRequestQueue(yason);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
