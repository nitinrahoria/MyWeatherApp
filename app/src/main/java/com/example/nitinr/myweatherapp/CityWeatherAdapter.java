package com.example.nitinr.myweatherapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nitinr.myweatherapp.Models.CityWeatherAttr;
import com.example.nitinr.myweatherapp.WeatherDetails.WeatherDetailsActivity;

import java.util.List;

/**
 * Created by nitin.r on 17-Jan-17.
 */
public class CityWeatherAdapter extends RecyclerView.Adapter<CityWeatherAdapter.CityWeatherHolder> {
    private final Context mContext;
    private final List<CityWeatherAttr> cityWeatherList;

    public CityWeatherAdapter(Context mContext, List<CityWeatherAttr> cityWeatherList) {
        this.mContext = mContext;
        this.cityWeatherList = cityWeatherList;
    }

    @Override
    public CityWeatherAdapter.CityWeatherHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_item, parent, false);

        return new CityWeatherHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CityWeatherAdapter.CityWeatherHolder holder, int position) {
        CityWeatherAttr CityWeatherAttr = cityWeatherList.get(position);
        holder.city.setText(CityWeatherAttr.getCity());
        holder.current_temperature.setText(CityWeatherAttr.getTemperature());
        holder.details.setText(CityWeatherAttr.getDescription());
        holder.humidity.setText(mContext.getString(R.string.humidity) + CityWeatherAttr.getHumidity());
        holder.pressure.setText(mContext.getString(R.string.pressure) + CityWeatherAttr.getPressure());
        holder.setId(position);
    }

    @Override
    public int getItemCount() {
        return cityWeatherList.size();
    }

    public class CityWeatherHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView city, details,
                current_temperature, humidity, pressure;
        private int id;

        public CityWeatherHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            city = (TextView) view.findViewById(R.id.city_field);
            current_temperature = (TextView) view.findViewById(R.id.current_temperature_field);
            details = (TextView) view.findViewById(R.id.details_field);
            humidity = (TextView) view.findViewById(R.id.humidity_field);
            pressure = (TextView) view.findViewById(R.id.pressure_field);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext,WeatherDetailsActivity.class);
            intent.putExtra("lan",cityWeatherList.get(id).getLan());
            intent.putExtra("lat",cityWeatherList.get(id).getLat());
            mContext.startActivity(intent);
        }
        public void setId(int id) {
            this.id = id;
        }
    }
}
