package com.example.andreas.sunshine.common;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Andreas on 16.11.2014.
 */
public class JsonRequest {
    private final String LOG_TAG = JsonRequest.class.getSimpleName();
    private int _count= 7;
    private String _postalCode;
    private StringBuffer _responseBuffer;

    public void setCount(int count){ _count = count; }
    public int getCount(){ return _count; }

    public Unit getUnit() { return Unit.Metric; }

    public void setPostalCode(String city) { _postalCode = city; }
    public String getPostalCode() { return _postalCode; }


    public String Response()
    {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try
        {
            // api.openweathermap.org/data/2.5/weather?id=5375480&units=metric&cnt=7
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("api.openweathermap.org")
                    .appendPath("data")
                    .appendPath("2.5")
                    .appendPath("forecast")
                    .appendPath("daily")
                    .appendQueryParameter("q", _postalCode)
                    .appendQueryParameter("mode","json")
                    .appendQueryParameter("units","metric")
                    .appendQueryParameter("cnt", String.valueOf(_count));
            String urlString = builder.build().toString();
            Log.d(LOG_TAG,"Url string: "+urlString);
            URL url = new URL(urlString);

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if(buffer.length() == 0)
                return null;

            String result = buffer.toString();
            return result;
        }
        catch (IOException e)
        {
            Log.e("JsonRequest.Response","Error",e);
            return null;
        }
        finally {
            if(urlConnection != null)
                urlConnection.disconnect();

            if(reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (final IOException e)
                {
                    Log.e("JsonRequest.Response","Error while closing reader",e);
                }
            }
        }
    }
}

