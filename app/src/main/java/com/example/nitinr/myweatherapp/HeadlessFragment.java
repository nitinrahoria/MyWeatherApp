package com.example.nitinr.myweatherapp;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.nitinr.myweatherapp.Models.CallMode;
import com.example.nitinr.myweatherapp.NetworkUtility.HTTPRequest;

import org.json.JSONObject;

/**
 * Created by nitin on 1/17/2017.
 */

public class HeadlessFragment extends Fragment {

    public static final String TAG_HEADLESS_FRAGMENT = "HEAD_LESS_FRAGMENT";
    AsyncResponse mCallback;
    WeatherDataAsyncTask mWeatherDataAsyncTask;
    String mLat, mLan, cnt;
    private boolean isTaskExecuting = false;
    private CallMode mode;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCallback = getActivity() instanceof AsyncResponse ? (AsyncResponse) getActivity() : null;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        setRetainInstance(true);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    public void startBackgroundTask(CallMode mode, String mLat, String mLan) {
        this.mLan = mLan;
        this.mLat = mLat;
        this.mode = mode;
        startBackgroundTask();
    }

    public void startBackgroundTask(CallMode mode, String mLat, String mLan, String cnt) {
        this.mLan = mLan;
        this.mLat = mLat;
        this.mode = mode;
        this.cnt = cnt;
        startBackgroundTask();
    }

    public void startBackgroundTask() {
        if (!isTaskExecuting) {
            mWeatherDataAsyncTask = new WeatherDataAsyncTask();
            mWeatherDataAsyncTask.execute();
            isTaskExecuting = true;
        } else {
            showProgressDiag();
        }
    }

    public void showProgressDiag() {
        if (isTaskExecuting) {
            mWeatherDataAsyncTask.showDiag();
        }
    }

    public interface AsyncResponse {
        void processFinish(JSONObject jsonWeather);
    }

    public class WeatherDataAsyncTask extends AsyncTask<Void, Integer, JSONObject> {

        ProgressDialog asyncDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDiag();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {

            JSONObject jsonWeather = null;
            try {
                jsonWeather = HTTPRequest.getCityWeatherJSON(mode, mLat, mLan, cnt);
            } catch (Exception e) {
                Log.d("Error", getString(R.string.json_result_error), e);
            }

            return jsonWeather;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            asyncDialog.dismiss();
            isTaskExecuting = false;
            if (result == null) {
                Toast.makeText(getActivity()
                        , R.string.weather_api_data_null
                        , Toast.LENGTH_LONG).show();
                super.onPostExecute(result);
                return;
            }
            if (mCallback != null)
                mCallback.processFinish(result);
            super.onPostExecute(result);
        }

        public void dismissDiag() {
            asyncDialog.dismiss();
        }

        public void showDiag() {
            asyncDialog = new ProgressDialog(getActivity());
            asyncDialog.setMessage(getString(R.string.loading));
            asyncDialog.setCancelable(false);
            asyncDialog.show();
        }
    }
}
