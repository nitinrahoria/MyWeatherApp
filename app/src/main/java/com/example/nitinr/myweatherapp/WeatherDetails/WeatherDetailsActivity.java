package com.example.nitinr.myweatherapp.WeatherDetails;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nitinr.myweatherapp.Models.CallMode;
import com.example.nitinr.myweatherapp.Models.CityWeatherAttr;
import com.example.nitinr.myweatherapp.HeadlessFragment;
import com.example.nitinr.myweatherapp.R;
import com.example.nitinr.myweatherapp.Utility.JsonParser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.Locale;

public class WeatherDetailsActivity extends FragmentActivity implements
        HeadlessFragment.AsyncResponse,
        OnMapReadyCallback,
        View.OnClickListener {

    private static final int PERMISSIONS_REQUEST_LOCATION = 100;
    String lan, lat, city;
    HeadlessFragment mFragment;
    private TextView cityField, updatedField,
            detailsField, currentTemperatureField,
            humidity_field, pressure_field;
    private ImageView mapImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.lan = getIntent().getStringExtra("lan");
        this.lat = getIntent().getStringExtra("lat");
        setContentView(R.layout.activity_weather_details);
        mapImage = (ImageView) findViewById(R.id.imageView);
        cityField = (TextView) findViewById(R.id.city_field);
        updatedField = (TextView) findViewById(R.id.updated_field);
        detailsField = (TextView) findViewById(R.id.details_field);
        currentTemperatureField = (TextView) findViewById(R.id.current_temperature_field);
        humidity_field = (TextView) findViewById(R.id.humidity_field);
        pressure_field = (TextView) findViewById(R.id.pressure_field);
        mapImage.setOnClickListener(this);
        FragmentManager mMgr = getFragmentManager();
        mFragment = (HeadlessFragment) mMgr
                .findFragmentByTag(HeadlessFragment.TAG_HEADLESS_FRAGMENT);
        if (mFragment == null) {
            mFragment = new HeadlessFragment();
            mMgr.beginTransaction()
                    .add(mFragment, HeadlessFragment.TAG_HEADLESS_FRAGMENT)
                    .commit();
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFragment.startBackgroundTask(CallMode.SINGLE, lat, lan);
    }

    @Override
    public void processFinish(JSONObject jsonWeather) {
        CityWeatherAttr cityWeatherAttr = JsonParser.getJsonObjectToCWObject(jsonWeather);
        city = cityWeatherAttr.getCity();
        cityField.setText(city);
        detailsField.setText(cityWeatherAttr.getDescription());
        currentTemperatureField.setText(cityWeatherAttr.getTemperature());
        humidity_field.setText(getString(R.string.humidity) + cityWeatherAttr.getHumidity());
        pressure_field.setText(getString(R.string.pressure) + cityWeatherAttr.getPressure());
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mapImage)) {
            Uri gmmIntentUri = Uri.parse(String.format(Locale.ENGLISH, "geo:%s,%s?q=%s,%s(%s)", lat, lan, lat, lan, city));
            Intent intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            intent.setPackage("com.google.android.apps.maps");
            startActivity(intent);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLan = new LatLng(Double.parseDouble(lat),Double.parseDouble(lan));
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_LOCATION);
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.addMarker(new MarkerOptions().position(latLan)
                .title(city));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLan));

        //Animating the camera
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(getApplicationContext(),getString(R.string.location_error_msg),Toast.LENGTH_LONG).show();
                    finish();
                }
                return;
            }

        }
    }
}
