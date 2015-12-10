package com.example.android.sunshine.app;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by Admin User on 12/10/2015.
 */
public class ForecastAdapter extends ArrayAdapter <String>
{
    public ForecastAdapter (Context context, int resource, int textViewResourceId, List<String> objects)
    {
        super(context, resource, textViewResourceId, objects);
    }

    public void loadData (List<String> forecast)
    {
        super.clear();
        super.addAll (forecast);

    }
}
