package com.example.andreas.sunshine.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.andreas.sunshine.common.JsonRequest;
import com.example.andreas.sunshine.common.WeatherDataParser;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    private static final String DEFAULT_POSTAL_CODE ="94043";
    private final String LOG_NAME = ForecastFragment.class.getSimpleName();
    private ArrayAdapter<String> forecastAdapter;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater)
    {
        menuInflater.inflate(R.menu.forecast_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_refresh)
        {
            WeatherConfiguration config = new WeatherConfiguration();
            config.NumberOfDays= 10;
            config.PostalCode="2340,AUT"; // for Mödling
            new FetchWeatherTask().execute(config);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ListView lv = (ListView) rootView.findViewById(R.id.listView_forecast);

        List<String> list = new ArrayList<String>();
        forecastAdapter = new ArrayAdapter<String>(rootView.getContext(), R.layout.list_item_forecast,R.id.list_item_forecast_textview ,list);
        lv.setAdapter(forecastAdapter);

        new FetchWeatherTask().execute(new WeatherConfiguration());
        return rootView;
    }

    private class FetchWeatherTask extends AsyncTask<WeatherConfiguration,Void,String[]>{

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        @Override
        protected String[] doInBackground(WeatherConfiguration... configurations)
        {
            WeatherConfiguration config = configurations[0];
            JsonRequest jsonRequest = new JsonRequest();
            jsonRequest.setCount(config.NumberOfDays);
            jsonRequest.setPostalCode(config.PostalCode);
            String response =  jsonRequest.Response();

            WeatherDataParser parser = new WeatherDataParser();
            try
            {
                return parser.getWeatherDataFromJson(response,jsonRequest.getCount());
            }
            catch(JSONException e)
            {
                Log.e(LOG_TAG,"Can not convert to json",e);
            }
            return new String[0];
        }

        @Override
        protected void onPostExecute(String[] results) {
            super.onPostExecute(results);
            if(results != null && results.length >0)
            {
                forecastAdapter.setNotifyOnChange(false);
                forecastAdapter.clear();
                for(String r:results)
                    forecastAdapter.add(r);
                forecastAdapter.notifyDataSetChanged();
            }

        }
    }

    private class WeatherConfiguration
    {
        public int NumberOfDays=7;
        public String PostalCode=DEFAULT_POSTAL_CODE;
    }
}
