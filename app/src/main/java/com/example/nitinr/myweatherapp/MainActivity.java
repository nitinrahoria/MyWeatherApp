package com.example.nitinr.myweatherapp;

import android.Manifest;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nitinr.myweatherapp.Models.CallMode;
import com.example.nitinr.myweatherapp.Models.CityWeatherAttr;
import com.example.nitinr.myweatherapp.Utility.JsonParser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements HeadlessFragment.AsyncResponse, LocationListener {

    private static final int PERMISSIONS_REQUEST_LOCATION = 100;
    private static final String PREFERENCES = "WeatherAppPref";
    private static final String CITY_COUNT = "city_count";
    LocationManager mLocationManager;
    ProgressDialog mProgressDialog;
    SharedPreferences mSharedpreferences;
    private RecyclerView mRecyclerView;
    private CityWeatherAdapter mAdapter;
    private List<CityWeatherAttr> cityWeatherList;
    private HeadlessFragment mFragment;
    private Location mLocation;
    private Button mFilterButton;
    private TextView mFilterProgressTV;
    private SeekBar mFilterSeekbar;
    private int cnt;
    private Dialog mFilterDialog;
    private int min = 10;

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMan.getActiveNetworkInfo() != null && conMan.getActiveNetworkInfo().isConnected())
            return true;
        else
            return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        if (!isNetworkAvailable(this)) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(R.string.caution);
            alertDialogBuilder.setMessage(R.string.internet_connection_request);
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            alertDialogBuilder.show();
            return;
        }
        mSharedpreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        cnt = mSharedpreferences.getInt(CITY_COUNT, 0);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        cityWeatherList = new ArrayList<>();
        mAdapter = new CityWeatherAdapter(this, cityWeatherList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        FragmentManager mMgr = getFragmentManager();
        mFragment = (HeadlessFragment) mMgr
                .findFragmentByTag(HeadlessFragment.TAG_HEADLESS_FRAGMENT);
        if (mFragment == null) {
            mFragment = new HeadlessFragment();
            mMgr.beginTransaction()
                    .add(mFragment, HeadlessFragment.TAG_HEADLESS_FRAGMENT)
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isNetworkAvailable(this)) {
            return;
        }
        if(mLocation == null)
            getCurrentLocation(this);
        else
            onLocationChanged(mLocation);;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    void getCurrentLocation(Context context) {
        mLocationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        String provider;
        boolean gps_enabled, network_enabled = false;
        gps_enabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        network_enabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!gps_enabled && !network_enabled) {
            showSettingsAlert();
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_LOCATION);
            return;
        }
        provider = LocationManager.GPS_PROVIDER;
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.location_loading_msg));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        if (gps_enabled) {
            provider = LocationManager.GPS_PROVIDER;
        }
        if (network_enabled) {
            provider = LocationManager.NETWORK_PROVIDER;
        }
        mLocationManager.requestLocationUpdates(provider, 0, 0, this);
        mLocation = mLocationManager.getLastKnownLocation(provider);

        if (mLocation != null) {
            onLocationChanged(mLocation);
        }
    }

    @Override
    public void processFinish(JSONObject jsonWeather) {
        cityWeatherList.clear();
        cityWeatherList.addAll(JsonParser.getJsonObjectToCWObjectList(jsonWeather));
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation(this);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.location_error_msg, Toast.LENGTH_LONG).show();
                    finish();
                }
                return;
            }

        }
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.gps_settings);
        alertDialog.setMessage(R.string.gps_enabling_msg);
        ;
        alertDialog.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
        HeadlessFragment mFragment = (HeadlessFragment) getFragmentManager()
                .findFragmentByTag(HeadlessFragment.TAG_HEADLESS_FRAGMENT);
        if (mFragment == null) {
            mFragment = new HeadlessFragment();
            getFragmentManager().beginTransaction()
                    .add(mFragment,HeadlessFragment.TAG_HEADLESS_FRAGMENT)
                    .commit();
        }
        mFragment.startBackgroundTask(CallMode.MULTIPLE, String.valueOf(mLocation.getLatitude()),
                String.valueOf(mLocation.getLongitude()), String.valueOf(cnt + min));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_LOCATION);
            mProgressDialog.dismiss();
            return;
        }
        mLocationManager.removeUpdates(this);
        mProgressDialog.dismiss();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter:
                showmFilterDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void showmFilterDialog() {
        mFilterDialog = new Dialog(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.seekbar_dialog, (ViewGroup) findViewById(R.id.dialog_root_element));
        mFilterProgressTV = (TextView) layout.findViewById(R.id.progress);
        mFilterSeekbar = (SeekBar) layout.findViewById(R.id.dialog_seekbar);
        mFilterButton = (Button) layout.findViewById(R.id.dialog_button);
        mFilterSeekbar.setMax((100 - 10));
        mFilterSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                cnt = progress;
                mFilterProgressTV.setText(String.valueOf(cnt + min));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        cnt = mSharedpreferences.getInt(CITY_COUNT, 0);
        mFilterSeekbar.setProgress(cnt);
        mFilterProgressTV.setText(String.valueOf(cnt + min));
        mFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragment.startBackgroundTask(CallMode.MULTIPLE, String.valueOf(mLocation.getLatitude()),
                        String.valueOf(mLocation.getLongitude()), (String) mFilterProgressTV.getText());
                SharedPreferences.Editor editor = mSharedpreferences.edit();
                editor.putInt(CITY_COUNT, cnt);
                editor.commit();
                mFilterDialog.dismiss();
            }
        });
        mFilterDialog.setContentView(layout);
        mFilterDialog.setTitle(R.string.select_range_of_near_by_cities);
        mFilterDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
