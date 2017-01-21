package com.example.nitinr.myweatherapp.Models;

/**
 * Created by nitin.r on 17-Jan-17.
 */
public class CityWeatherAttr {
    String city;
    String description;
    String temperature;
    String humidity;
    String pressure;
    String lan;
    String lat;

    public CityWeatherAttr(String city, String description, String temperature,
                           String humidity, String pressure, String lan, String lat) {
        this.city = city;
        this.description = description;
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
        this.lan = lan;
        this.lat = lat;
    }

    public CityWeatherAttr(){}

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getLan() {
        return lan;
    }

    public void setLan(String lan) {
        this.lan = lan;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }
}
