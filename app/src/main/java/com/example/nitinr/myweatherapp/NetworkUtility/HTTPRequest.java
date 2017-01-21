package com.example.nitinr.myweatherapp.NetworkUtility;

import com.example.nitinr.myweatherapp.Models.CallMode;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by nitin.r on 17-Jan-17.
 */
public class HTTPRequest {

    private static final String KEY = "6f6ca80eab03c99472db55b16e3c5157";
    private static final String OPEN_SINGLE_WEATHER_MAP_URL =
            "http://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&APPID=%s";
    private static final String OPEN_MULTIPLE_WEATHER_MAP_URL =
            "http://api.openweathermap.org/data/2.5/find?lat=%s&lon=%s&cnt=%s&APPID=%s";

    public static JSONObject getCityWeatherJSON(CallMode mode, String lat, String lon, String cnt){
        if(cnt == null){
            cnt = "10";//min
        }
        if(mode == CallMode.SINGLE){
            return getSingleCityWeatherJSON(lat, lon);
        }else if(mode == CallMode.MULTIPLE){
            return getMultipleCityWeatherJSON(lat, lon, cnt);
        } else
            return getSingleCityWeatherJSON(lat, lon);
    }

    public static JSONObject getSingleCityWeatherJSON(String lat, String lon){
        URL url = null;
        try {
            url = new URL(String.format(OPEN_SINGLE_WEATHER_MAP_URL, lat, lon, KEY));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return getJSONFromURL(url);
    }

    public static JSONObject getMultipleCityWeatherJSON(String lat, String lon, String cnt){
        URL url = null;
        try {
            url = new URL(String.format(OPEN_MULTIPLE_WEATHER_MAP_URL, lat, lon, cnt, KEY));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return getJSONFromURL(url);
    }

    public static JSONObject getJSONFromURL(URL url){
        try{
            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();
            connection.addRequestProperty("x-api-key", KEY);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());

            if(data.getInt("cod") != 200){
                return null;
            }

            return data;
        }catch(Exception e){
            return null;
        }
    }
}
