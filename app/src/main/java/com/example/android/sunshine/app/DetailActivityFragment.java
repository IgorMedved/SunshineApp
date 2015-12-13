package com.example.android.sunshine.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment
{

    private CustomIconShareProvider mShareActionProvider;
    private static final String HASHTAG = " #SunhineApp";
    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    public DetailActivityFragment ()
    {
    }

    @Override
    public void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


   /* @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.menu_item_share)
        {


            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater)
    {
        //super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.detailfragment, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = new CustomIconShareProvider (getActivity());
                MenuItemCompat.setActionProvider(item, mShareActionProvider);
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
        else
        {
            Log.v(LOG_TAG, "Share action provider is null");
        }

    }

    private Intent createShareForecastIntent()
    {
        Intent share = new Intent (Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_TEXT, getActivity().getIntent()
                                                       .getStringExtra("dayWeatherForecast")+ HASHTAG);
        share.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.setType("text/plain");
        return share;
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
