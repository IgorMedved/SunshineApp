package com.example.android.sunshine.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment
{


    public DetailActivityFragment ()
    {
    }

    @Override
    public void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }




    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        RelativeLayout fragmentLayout = (RelativeLayout)inflater.inflate(R.layout.fragment_detail, container, false);
        TextView weatherTxt = (TextView)fragmentLayout.findViewById(R.id.weather_txt);
        weatherTxt.setText (getActivity().getIntent().getStringExtra("dayWeatherForecast"));

        return fragmentLayout;
    }
}
