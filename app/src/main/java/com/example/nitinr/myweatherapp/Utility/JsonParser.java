package com.example.nitinr.myweatherapp.Utility;

import android.util.Log;

import com.example.nitinr.myweatherapp.Models.CityWeatherAttr;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by nitin on 1/18/2017.
 */

public class JsonParser {
    public static ArrayList<CityWeatherAttr> getJsonObjectToCWObjectList(JSONObject json){
        ArrayList<CityWeatherAttr> attrList = new ArrayList<>();
        try {
            if(json != null){
                int count = Integer.parseInt(json.getString("count"));
                JSONArray list = json.getJSONArray("list");
                for(int i =0; i < count; i++) {
                    CityWeatherAttr attr = new CityWeatherAttr();
                    JSONObject listItem = list.getJSONObject(i);
                    JSONObject details = listItem.getJSONArray("weather").getJSONObject(0);
                    JSONObject main = listItem.getJSONObject("main");
                    JSONObject coord = listItem.getJSONObject("coord");
                    attr.setCity(listItem.getString("name").toUpperCase(Locale.US) + ", " + listItem.getJSONObject("sys").getString("country"));
                    attr.setDescription(details.getString("description").toUpperCase(Locale.US));
                    attr.setTemperature(String.format("%.2f", main.getDouble("temp")) + "°");
                    attr.setHumidity(main.getString("humidity") + "%");
                    attr.setPressure(main.getString("pressure") + " hPa");
                    attr.setLan(coord.getString("lon"));
                    attr.setLat(coord.getString("lat"));
                    attrList.add(attr);
                }
            }
        } catch (JSONException e) {
            Log.e("Nitin", "Cannot process JSON results", e);
        }
        return attrList;
    }

    public static CityWeatherAttr getJsonObjectToCWObject(JSONObject jsonWeather) {
        CityWeatherAttr attr = new CityWeatherAttr();
        try {
            JSONObject details = jsonWeather.getJSONArray("weather").getJSONObject(0);
            JSONObject main = jsonWeather.getJSONObject("main");
            JSONObject coord = jsonWeather.getJSONObject("coord");
            attr.setCity(jsonWeather.getString("name").toUpperCase(Locale.US) + ", " + jsonWeather.getJSONObject("sys").getString("country"));
            attr.setDescription(details.getString("description").toUpperCase(Locale.US));
            attr.setTemperature(String.format("%.2f", main.getDouble("temp")) + "°");
            attr.setHumidity(main.getString("humidity") + "%");
            attr.setPressure(main.getString("pressure") + " hPa");
            attr.setLan(coord.getString("lon"));
            attr.setLat(coord.getString("lat"));
        } catch (JSONException e) {
                Log.e("Nitin", "Cannot process JSON results", e);
        }
        return attr;
    }
}
