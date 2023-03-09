package com.example.weather.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.weather.R;
import com.example.weather.adapter.ActiveAdapter;
import com.example.weather.adapter.AutoCompleteAdapter;
import com.example.weather.location.GpsTracker;
import com.example.weather.model.Item;
import com.example.weather.model.WeatherGetSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Active extends Fragment {

    private ActiveAdapter adapter;
    private RecyclerView recyclerView;
    private Context context;
    private ArrayList<WeatherGetSet> Weather_ArrayList;
    private List<Item> cityList;

    String icon, city, status;
    ImageView iv_icon;
    TextView tv_main, tv_city, tv_temperature, tv_max_temperature_value, tv_min_temperature_value;
    TextView wind_speed_value, tv_sunrise_value, tv_sunset_value, tv_humidity_value;
    String temp, min_temp, max_temp;
    String wind_speed, sunrise, sunset, humidity;

    TextView tv_active;

    private GpsTracker gpsTracker;

    public Active(){
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_active, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.context = getContext();

        iv_icon = view.findViewById(R.id.iv_icon);
        tv_main = view.findViewById(R.id.tv_main);
        tv_city = view.findViewById(R.id.tv_city);
        tv_temperature = view.findViewById(R.id.tv_temperature);
        tv_max_temperature_value = view.findViewById(R.id.tv_max_temperature_value);
        tv_min_temperature_value = view.findViewById(R.id.tv_min_temperature_value);

        wind_speed_value = view.findViewById(R.id.wind_speed_value);
        tv_sunrise_value = view.findViewById(R.id.tv_sunrise_value);
        tv_sunset_value = view.findViewById(R.id.tv_sunset_value);
        tv_humidity_value = view.findViewById(R.id.tv_humidity_value);

        tv_active = view.findViewById(R.id.tv_active);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setHasFixedSize(true);

        cityList = new ArrayList<>();
        CityList();

        AutoCompleteTextView editText = view.findViewById(R.id.actv);
        AutoCompleteAdapter adapter = new AutoCompleteAdapter(context, cityList);
        editText.setAdapter(adapter);
        editText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                if (item instanceof Item) {
                    Item city = (Item) item;

                    populateList(city.getLatitude(), city.getLongitude());
                    populateListCurrent(city.getLatitude(), city.getLongitude());

                    ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    editText.clearFocus();
                }
            }
        });

        tv_active.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getLocation();
                editText.setText("Current Location");
                editText.clearFocus();
            }
        });



        getLocation();

    }

    private void CityList() {

        cityList.add(new Item("Delhi", 28.6600, 77.2300));
        cityList.add(new Item("Mumbai", 18.9667, 72.8333));
        cityList.add(new Item("Kolkata", 22.5411, 88.3378));
        cityList.add(new Item("Bangalore", 12.9699, 77.5980));
        cityList.add(new Item("Chennai", 13.0825, 80.2750));
        cityList.add(new Item("Hyderabad", 17.3667, 78.4667));
        cityList.add(new Item("Pune", 18.5196, 73.8553));
        cityList.add(new Item("Ahmedabad", 23.0300, 72.5800));
    }

    // for 5 day
    public void populateList(double lat, double longi){

        Weather_ArrayList = new ArrayList<>();

        String get_url = "https://api.openweathermap.org/data/2.5/forecast?lat="+lat+"&lon="+longi+"&appid=fae7190d7e6433ec3a45285ffcf55c86&units=metric";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, get_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Weather_ArrayList.clear();

                        try {

                            JSONArray jsonArray = response.getJSONArray("list");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                WeatherGetSet obWeatherDetails = new WeatherGetSet();

                                JSONObject list = jsonArray.getJSONObject(i);

                                obWeatherDetails.setDate(list.getString("dt_txt"));

                                JSONObject jsonArray1 = list.getJSONArray("weather").getJSONObject(0);
                                JSONObject jsonObject = list.getJSONObject("main");

                                obWeatherDetails.setMain(jsonArray1.getString("main"));
                                obWeatherDetails.setIcon(jsonArray1.getString("icon"));
                                obWeatherDetails.setTemp(jsonObject.getString("temp"));


                                Weather_ArrayList.add(obWeatherDetails);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        adapter =  new ActiveAdapter(getContext(), Weather_ArrayList);
                        recyclerView.setAdapter(adapter);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

            }
        });

        RequestQueue productRequestQueue = Volley.newRequestQueue(context);
        productRequestQueue.add(request);
    }

    // for now
    public void populateListCurrent(double lat, double longi){

        Weather_ArrayList = new ArrayList<>();

        String get_url = "https://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+longi+"&appid=fae7190d7e6433ec3a45285ffcf55c86&units=metric";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, get_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //PeopleARSDetailsList.clear();
                        try {
                            JSONArray weather = response.getJSONArray("weather");
                            JSONObject main = response.getJSONObject("main");
                            JSONObject wind = response.getJSONObject("wind");
                            JSONObject sys = response.getJSONObject("sys");

                            city = response.getString("name");

                            for (int i = 0; i < weather.length(); i++) {

                                JSONObject hit = weather.getJSONObject(i);
                                icon = hit.getString("icon");
                                status = hit.getString("description");

                            }

                            for (int i = 0; i < main.length(); i++) {

                                temp = main.getString("temp");
                                min_temp = main.getString("temp_min");
                                max_temp = main.getString("temp_max");
                                humidity = main.getString("humidity");

                            }
                            for (int i = 0; i < wind.length(); i++) {

                                wind_speed = wind.getString("speed");

                            }
                            for (int i = 0; i < sys.length(); i++) {

                                sunrise = sys.getString("sunrise");
                                sunset = sys.getString("sunset");

                            }

                            tv_city.setText(city);
                            Integer temperature = Integer.valueOf((int) Math.round(Float.parseFloat(temp)));
                            tv_temperature.setText(""+temperature + "\u2103");
                            Integer min_temperature = Integer.valueOf((int) Math.round(Float.parseFloat(min_temp)));
                            tv_min_temperature_value.setText(""+min_temperature + "\u2103");
                            Integer max_temperature = Integer.valueOf((int) Math.round(Float.parseFloat(max_temp)));
                            tv_max_temperature_value.setText(""+max_temperature + "\u2103");

                            wind_speed_value.setText(wind_speed+"m/s");

                            long dv = Long.parseLong(sunrise)*1000;
                            Date df = new java.util.Date(dv);
                            String vv = new SimpleDateFormat("KK:mm aaa").format(df);
                            tv_sunrise_value.setText(vv);

                            long dv1 = Long.parseLong(sunset)*1000;
                            Date df1 = new java.util.Date(dv1);
                            String vv1 = new SimpleDateFormat("KK:mm aaa").format(df1);
                            tv_sunset_value.setText(vv1);

                            tv_humidity_value.setText(humidity+"%");

                            tv_main.setText(status);

                            String iconUrl = "https://openweathermap.org/img/w/" + icon + ".png";

                            Glide.with(context)
                                    .load(iconUrl)
                                    .apply(RequestOptions.centerCropTransform())
                                    .into(iv_icon);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        RequestQueue productRequestQueue = Volley.newRequestQueue(context);
        productRequestQueue.add(request);
    }

    public void getLocation(){
        gpsTracker = new GpsTracker(getContext());
        if(gpsTracker.canGetLocation()){
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();

            populateList(latitude, longitude);
            populateListCurrent(latitude,longitude);

        }else{
            gpsTracker.showSettingsAlert();
        }
    }
}